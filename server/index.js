// server/index.js
// HackSecure Relay Server — v1.0.0
// Blind relay: routes encrypted packets without inspecting content
// Only knows: conversationId, messageId, routing
//
// Run: node index.js
// Dependencies: npm install express ws tweetnacl

"use strict";

const express = require("express");
const http = require("http");
const WebSocket = require("ws");
const nacl = require("tweetnacl");
const crypto = require("crypto");

// ══════════════════════════════════════════════════════════════════════════════
// SERVER CONFIG
// ══════════════════════════════════════════════════════════════════════════════
const PORT = process.env.PORT || 8443;
const TICKET_EXPIRY_SECONDS = 3600; // 1 hour
const MAX_CONNECTIONS_PER_CONV = 10;

// Server Ed25519 signing keypair — generate once, hardcode public key in app
// In production: load from secure key store
let serverKeyPair;
try {
  const savedKey = process.env.SERVER_PRIVATE_KEY_HEX;
  if (savedKey) {
    const privBytes = Buffer.from(savedKey, "hex");
    serverKeyPair = nacl.sign.keyPair.fromSeed(privBytes);
  } else {
    serverKeyPair = nacl.sign.keyPair();
    console.log("⚠️  NEW SERVER KEYPAIR GENERATED");
    console.log("   Hardcode this public key in BuildConfig.SERVER_PUBLIC_KEY_HEX:");
    console.log("   " + Buffer.from(serverKeyPair.publicKey).toString("hex"));
    console.log("   Set SERVER_PRIVATE_KEY_HEX env var to persist across restarts");
  }
} catch (e) {
  serverKeyPair = nacl.sign.keyPair();
}

console.log(`Server public key: ${Buffer.from(serverKeyPair.publicKey).toString("hex")}`);

// ══════════════════════════════════════════════════════════════════════════════
// IN-MEMORY ROUTING TABLE
// Maps conversationId → Set<WebSocket>
// ══════════════════════════════════════════════════════════════════════════════
const conversationSockets = new Map(); // conversationId → Set<ws>
const presenceMap = new Map();         // identityHash → { connectionToken, lastSeen }

// Offline message buffer: conversationId → [{data: Buffer, ts: number}]
const messageBuffer = new Map();
const MAX_BUFFER_PER_CONV = 50;
const BUFFER_TTL_MS = 3_600_000; // 1 hour

// ══════════════════════════════════════════════════════════════════════════════
// GHOST MODE — IN-MEMORY REGISTRIES (ZERO PERSISTENCE)
// ══════════════════════════════════════════════════════════════════════════════
const ghostRegistry = new Map();   // codename → { token, registeredAt, lastHeartbeat }
const ghostTokenIndex = new Map(); // token → codename (reverse lookup)
const ghostRequests = new Map();   // targetCodename → [{ requestId, fromCodename, timestamp }]
const ghostChannels = new Map();   // ghostChannelId → { codenames: [A, B], sockets: Set<ws> }
const GHOST_HEARTBEAT_TIMEOUT_MS = 60_000;  // 60s no heartbeat → purge
const GHOST_REQUEST_TTL_MS = 300_000;       // 5 min → auto-delete request

// ══════════════════════════════════════════════════════════════════════════════
// EXPRESS REST API
// ══════════════════════════════════════════════════════════════════════════════
const app = express();
app.use(express.json());

// Health check
app.get("/health", (req, res) => {
  res.json({
    status: "ok",
    version: "1.0.0",
    connections: getTotalConnections(),
    timestamp: Date.now()
  });
});

/**
 * POST /api/v1/ticket
 * Issues a signed session ticket for a pair of identity hashes.
 *
 * Ticket binary (140 bytes):
 *   [32] a_id
 *   [32] b_id
 *   [8]  timestamp_ms big-endian
 *   [4]  expiry_seconds big-endian
 *   [64] Ed25519 signature of above 76 bytes
 */
app.post("/api/v1/ticket", (req, res) => {
  const { a_id, b_id } = req.body;

  if (!a_id || !b_id || typeof a_id !== "string" || typeof b_id !== "string") {
    return res.status(400).json({ error: "a_id and b_id are required hex strings" });
  }

  if (a_id.length !== 64 || b_id.length !== 64) {
    return res.status(400).json({ error: "Identity hashes must be 32-byte hex strings (64 chars)" });
  }

  try {
    const aIdBytes = Buffer.from(a_id, "hex");
    const bIdBytes = Buffer.from(b_id, "hex");

    const timestamp = Date.now();
    const expirySeconds = TICKET_EXPIRY_SECONDS;

    // Build 76-byte payload
    const payload = Buffer.alloc(76);
    aIdBytes.copy(payload, 0);
    bIdBytes.copy(payload, 32);
    payload.writeBigInt64BE(BigInt(timestamp), 64);
    payload.writeInt32BE(expirySeconds, 72);

    // Sign with server Ed25519 key
    const signature = nacl.sign.detached(payload, serverKeyPair.secretKey);

    const ticket = Buffer.concat([payload, Buffer.from(signature)]);

    console.log(`Ticket issued: ${a_id.slice(0, 8)}... ↔ ${b_id.slice(0, 8)}...`);

    res.json({
      ticket_b64: ticket.toString("base64"),
      server_public_key_b64: Buffer.from(serverKeyPair.publicKey).toString("base64")
    });
  } catch (e) {
    console.error("Ticket error:", e);
    res.status(500).json({ error: "Internal error" });
  }
});

/**
 * POST /api/v1/presence
 * Register or query presence for an identity.
 */
app.post("/api/v1/presence", (req, res) => {
  const { identity_hash, connection_token } = req.body;

  if (!identity_hash) return res.status(400).json({ error: "identity_hash required" });

  const token = connection_token || crypto.randomBytes(32).toString("hex");

  presenceMap.set(identity_hash, {
    connectionToken: token,
    lastSeen: Date.now()
  });

  res.json({ online: true, token });
});

// ══════════════════════════════════════════════════════════════════════════════
// GHOST MODE REST API
// ══════════════════════════════════════════════════════════════════════════════

/** Helper: validate ghost token from header, returns codename or null. */
function resolveGhostToken(req) {
  const token = req.headers["x-ghost-token"];
  if (!token) return null;
  return ghostTokenIndex.get(token) || null;
}

/**
 * POST /api/v1/ghost/register
 * Register a codename for ghost mode. Returns a ghostToken.
 * Body: { codename: string }
 */
app.post("/api/v1/ghost/register", (req, res) => {
  const { codename } = req.body;
  if (!codename || typeof codename !== "string" || codename.trim().length < 2 || codename.trim().length > 32) {
    return res.status(400).json({ error: "Codename must be 2-32 characters" });
  }
  const name = codename.trim().toUpperCase();
  if (ghostRegistry.has(name)) {
    return res.status(409).json({ error: "Codename already taken" });
  }
  const token = crypto.randomUUID();
  const now = Date.now();
  ghostRegistry.set(name, { token, registeredAt: now, lastHeartbeat: now });
  ghostTokenIndex.set(token, name);
  console.log(`👻 Ghost registered: ${name}`);
  res.json({ ghostToken: token, codename: name });
});

/**
 * GET /api/v1/ghost/search?q=<substring>
 * Search online ghost codenames. Requires X-Ghost-Token header.
 */
app.get("/api/v1/ghost/search", (req, res) => {
  const myCodename = resolveGhostToken(req);
  if (!myCodename) return res.status(401).json({ error: "Invalid or missing ghost token" });
  // Heartbeat on any authenticated request
  const entry = ghostRegistry.get(myCodename);
  if (entry) entry.lastHeartbeat = Date.now();

  const query = (req.query.q || "").trim().toUpperCase();
  if (query.length < 1) return res.json({ results: [] });

  const results = [];
  for (const [name] of ghostRegistry) {
    if (name !== myCodename && name.includes(query)) {
      results.push(name);
      if (results.length >= 20) break;
    }
  }
  res.json({ results });
});

/**
 * POST /api/v1/ghost/request
 * Send a chat request to another ghost codename.
 * Body: { targetCodename: string }
 * Requires X-Ghost-Token header.
 */
app.post("/api/v1/ghost/request", (req, res) => {
  const myCodename = resolveGhostToken(req);
  if (!myCodename) return res.status(401).json({ error: "Invalid ghost token" });
  const entry = ghostRegistry.get(myCodename);
  if (entry) entry.lastHeartbeat = Date.now();

  const { targetCodename } = req.body;
  if (!targetCodename) return res.status(400).json({ error: "targetCodename required" });
  const target = targetCodename.trim().toUpperCase();
  if (!ghostRegistry.has(target)) return res.status(404).json({ error: "Target not online" });
  if (target === myCodename) return res.status(400).json({ error: "Cannot request yourself" });

  const requestId = crypto.randomUUID();
  const pending = ghostRequests.get(target) || [];
  // Prevent duplicate requests from same sender
  if (pending.some(r => r.fromCodename === myCodename)) {
    return res.status(409).json({ error: "Request already pending" });
  }
  pending.push({ requestId, fromCodename: myCodename, timestamp: Date.now() });
  ghostRequests.set(target, pending);
  console.log(`👻 Request: ${myCodename} → ${target} (${requestId})`);
  res.json({ requestId, sent: true });
});

/**
 * POST /api/v1/ghost/respond
 * Accept or reject a chat request.
 * Body: { requestId: string, accept: boolean }
 * Requires X-Ghost-Token header.
 * On accept: creates a ghostChannelId and returns it.
 */
app.post("/api/v1/ghost/respond", (req, res) => {
  const myCodename = resolveGhostToken(req);
  if (!myCodename) return res.status(401).json({ error: "Invalid ghost token" });
  const entry = ghostRegistry.get(myCodename);
  if (entry) entry.lastHeartbeat = Date.now();

  const { requestId, accept } = req.body;
  if (!requestId) return res.status(400).json({ error: "requestId required" });

  const pending = ghostRequests.get(myCodename) || [];
  const idx = pending.findIndex(r => r.requestId === requestId);
  if (idx === -1) return res.status(404).json({ error: "Request not found" });

  const request = pending.splice(idx, 1)[0];
  if (pending.length === 0) ghostRequests.delete(myCodename);
  else ghostRequests.set(myCodename, pending);

  if (!accept) {
    console.log(`👻 Request rejected: ${request.fromCodename} → ${myCodename}`);
    return res.json({ accepted: false });
  }

  // Create ghost channel
  const channelId = crypto.randomUUID();
  ghostChannels.set(channelId, {
    codenames: [request.fromCodename, myCodename],
    sockets: new Set()
  });
  console.log(`👻 Channel created: ${channelId} (${request.fromCodename} ↔ ${myCodename})`);
  res.json({ accepted: true, channelId, peerCodename: request.fromCodename });
});

/**
 * GET /api/v1/ghost/poll
 * Poll for incoming requests and accepted channels.
 * Requires X-Ghost-Token header.
 * Returns: { requests: [...], channels: [...] }
 */
app.get("/api/v1/ghost/poll", (req, res) => {
  const myCodename = resolveGhostToken(req);
  if (!myCodename) return res.status(401).json({ error: "Invalid ghost token" });
  const entry = ghostRegistry.get(myCodename);
  if (entry) entry.lastHeartbeat = Date.now();

  // Incoming requests addressed to me
  const requests = (ghostRequests.get(myCodename) || []).map(r => ({
    requestId: r.requestId,
    fromCodename: r.fromCodename,
    timestamp: r.timestamp
  }));

  // Channels I'm part of (so the requester can discover the accepted channel)
  const channels = [];
  for (const [channelId, ch] of ghostChannels) {
    if (ch.codenames.includes(myCodename)) {
      const peer = ch.codenames.find(c => c !== myCodename);
      channels.push({ channelId, peerCodename: peer });
    }
  }

  res.json({ requests, channels });
});

/**
 * POST /api/v1/ghost/leave
 * Explicitly leave ghost mode, cleaning up all state.
 * Requires X-Ghost-Token header.
 */
app.post("/api/v1/ghost/leave", (req, res) => {
  const myCodename = resolveGhostToken(req);
  if (!myCodename) return res.status(401).json({ error: "Invalid ghost token" });

  cleanupGhost(myCodename);
  console.log(`👻 Ghost left: ${myCodename}`);
  res.json({ left: true });
});

/** Remove a ghost codename and all its associated state. */
function cleanupGhost(codename) {
  const entry = ghostRegistry.get(codename);
  if (entry) {
    ghostTokenIndex.delete(entry.token);
    ghostRegistry.delete(codename);
  }
  ghostRequests.delete(codename);
  // Also remove outgoing requests this codename sent to others
  for (const [target, reqs] of ghostRequests) {
    const filtered = reqs.filter(r => r.fromCodename !== codename);
    if (filtered.length === 0) ghostRequests.delete(target);
    else ghostRequests.set(target, filtered);
  }
  // Destroy any ghost channels involving this codename
  for (const [channelId, ch] of ghostChannels) {
    if (ch.codenames.includes(codename)) {
      // Force-close all sockets in this channel
      for (const ws of ch.sockets) {
        try { ws.close(4100, "Ghost channel destroyed"); } catch (_) {}
      }
      ghostChannels.delete(channelId);
      console.log(`👻 Channel destroyed: ${channelId}`);
    }
  }
}

// ══════════════════════════════════════════════════════════════════════════════
// WEBSOCKET SERVER
// ══════════════════════════════════════════════════════════════════════════════
const server = http.createServer(app);
const wss = new WebSocket.Server({ server });

wss.on("connection", (ws, req) => {
  const url = new URL(req.url, "http://localhost");
  const conversationId = url.searchParams.get("conv");
  const ghostChannelId = url.searchParams.get("ghost");

  // ── Ghost channel WebSocket ──────────────────────────────────────────────
  if (ghostChannelId) {
    const channel = ghostChannels.get(ghostChannelId);
    if (!channel) {
      ws.close(4010, "Ghost channel not found");
      return;
    }
    if (channel.sockets.size >= 2) {
      ws.close(4011, "Ghost channel full");
      return;
    }
    channel.sockets.add(ws);
    ws._ghostChannelId = ghostChannelId;
    console.log(`👻 Socket joined ghost channel ${ghostChannelId.slice(0, 8)}... (${channel.sockets.size}/2)`);

    ws.on("message", (data, isBinary) => {
      if (!isBinary) { ws.close(4004, "Binary only"); return; }
      // Relay to the OTHER socket in this ghost channel — NO BUFFERING
      for (const peer of channel.sockets) {
        if (peer !== ws && peer.readyState === WebSocket.OPEN) {
          peer.send(data, { binary: true });
        }
      }
      // If peer is offline, packet is silently dropped (zero-trace)
    });

    ws.on("close", () => {
      channel.sockets.delete(ws);
      console.log(`👻 Socket left ghost channel ${ghostChannelId.slice(0, 8)}...`);
      // Relaxed rule: DO NOT force-close the OTHER socket. Let the user stay in the UI, 
      // or let them explicitly exit if they choose to.
      // We also do not delete the ghostChannelId right away so the channel can 
      // theoretically be rejoined if they quickly reconnect (if app supported it)
      // or at least it won't abruptly throw the active participant out.
    });

    ws.on("error", (err) => {
      console.error(`👻 Ghost WebSocket error:`, err.message);
      channel.sockets.delete(ws);
      // Relaxed rule: DO NOT force-close the OTHER socket here either.
    });

    return; // Don't fall through to regular conversation handling
  }

  // ── Regular conversation WebSocket ───────────────────────────────────────
  if (!conversationId) {
    ws.close(4001, "Missing conv or ghost parameter");
    return;
  }

  // Basic conversation ID format check
  if (!/^[a-f0-9_]+$/.test(conversationId) || conversationId.length > 200) {
    ws.close(4002, "Invalid conversation ID");
    return;
  }

  // Join conversation room
  if (!conversationSockets.has(conversationId)) {
    conversationSockets.set(conversationId, new Set());
  }
  const room = conversationSockets.get(conversationId);

  if (room.size >= MAX_CONNECTIONS_PER_CONV) {
    ws.close(4003, "Room full");
    return;
  }

  room.add(ws);
  ws.conversationId = conversationId;
  console.log(`Client joined conversation ${conversationId.slice(0, 16)}... (${room.size} members)`);

  // Deliver any buffered messages to the newly-connected participant
  const backlog = messageBuffer.get(conversationId);
  if (backlog && backlog.length > 0) {
    const now = Date.now();
    for (const entry of backlog) {
      if (now - entry.ts < BUFFER_TTL_MS && ws.readyState === WebSocket.OPEN) {
        ws.send(entry.data, { binary: true });
      }
    }
    messageBuffer.delete(conversationId);
    console.log(`  → delivered ${backlog.length} buffered message(s) to late joiner`);
  }

  ws.on("message", (data, isBinary) => {
    // ALL messages are binary — relay to all OTHER sockets in the same conversation
    if (!isBinary) {
      ws.close(4004, "Text frames not accepted — binary only");
      return;
    }

    const bytes = Buffer.isBuffer(data) ? data : Buffer.from(data);

    // Parse routing header: [2 conv_len][conv_bytes][2 msgId_len][msgId_bytes][...packet]
    if (bytes.length < 6) {
      console.warn("Packet too short, ignoring");
      return;
    }

    // Log routing info (NOT content — relay is blind)
    const convLen = bytes.readUInt16BE(0);
    const msgIdOffset = 2 + convLen;
    const msgIdLen = bytes.readUInt16BE(msgIdOffset);
    const payloadOffset = msgIdOffset + 2 + msgIdLen;
    const payloadSize = bytes.length - payloadOffset;

    console.log(`Routing ${payloadSize}B packet in ${conversationId.slice(0, 8)}...`);

    // Route to all OTHER participants in this conversation
    let relayed = 0;
    for (const peer of room) {
      if (peer !== ws && peer.readyState === WebSocket.OPEN) {
        peer.send(data, { binary: true });
        relayed++;
      }
    }
    console.log(`  → relayed to ${relayed} peers`);

    // If no peer was online, buffer the packet for offline delivery
    if (relayed === 0) {
      const buf = messageBuffer.get(conversationId) || [];
      if (buf.length < MAX_BUFFER_PER_CONV) {
        buf.push({ data: Buffer.from(bytes), ts: Date.now() });
        messageBuffer.set(conversationId, buf);
        console.log(`  → buffered for offline delivery (${buf.length} queued)`);
      }
    }
  });

  ws.on("close", () => {
    room.delete(ws);
    if (room.size === 0) {
      conversationSockets.delete(conversationId);
    }
    console.log(`Client left ${conversationId.slice(0, 16)}... (${room.size} remaining)`);
  });

  ws.on("error", (err) => {
    console.error(`WebSocket error in ${conversationId.slice(0, 8)}...:`, err.message);
    room.delete(ws);
  });
});

function getTotalConnections() {
  let total = 0;
  for (const room of conversationSockets.values()) total += room.size;
  return total;
}

// Periodic cleanup of empty rooms
setInterval(() => {
  for (const [convId, room] of conversationSockets.entries()) {
    for (const ws of room) {
      if (ws.readyState !== WebSocket.OPEN) room.delete(ws);
    }
    if (room.size === 0) conversationSockets.delete(convId);
  }
  
  // Cleanup stale presence entries (older than 5 minutes)
  const cutoff = Date.now() - 300_000;
  for (const [hash, info] of presenceMap.entries()) {
    if (info.lastSeen < cutoff) presenceMap.delete(hash);
  }

  // Cleanup expired offline message buffer entries
  const bufExpiry = Date.now() - BUFFER_TTL_MS;
  for (const [convId, buf] of messageBuffer.entries()) {
    const fresh = buf.filter(e => e.ts > bufExpiry);
    if (fresh.length === 0) messageBuffer.delete(convId);
    else messageBuffer.set(convId, fresh);
  }

  // ── Ghost Mode cleanup ─────────────────────────────────────────────────
  const now = Date.now();

  // Purge ghost codenames with stale heartbeat
  for (const [codename, entry] of ghostRegistry) {
    if (now - entry.lastHeartbeat > GHOST_HEARTBEAT_TIMEOUT_MS) {
      // Check if they are in an active ghost channel (if so, keep them alive)
      let inActiveChannel = false;
      for (const [channelId, ch] of ghostChannels) {
        if (ch.codenames.includes(codename)) {
          inActiveChannel = true;
          // Optionally update heartbeat so they don't immediately purge on disconnect
          entry.lastHeartbeat = now;
          break;
        }
      }
      
      if (!inActiveChannel) {
        console.log(`👻 Purging stale ghost: ${codename}`);
        cleanupGhost(codename);
      }
    }
  }

  // Purge expired ghost requests
  for (const [target, reqs] of ghostRequests) {
    const fresh = reqs.filter(r => now - r.timestamp < GHOST_REQUEST_TTL_MS);
    if (fresh.length === 0) ghostRequests.delete(target);
    else ghostRequests.set(target, fresh);
  }

  // Purge ghost channels with 0 active sockets
  for (const [channelId, ch] of ghostChannels) {
    // Remove dead sockets
    for (const ws of ch.sockets) {
      if (ws.readyState !== WebSocket.OPEN) ch.sockets.delete(ws);
    }
    if (ch.sockets.size === 0) {
      ghostChannels.delete(channelId);
      console.log(`👻 Cleaned up empty ghost channel: ${channelId.slice(0, 8)}...`);
    }
  }
}, 60_000);

server.listen(PORT, () => {
  console.log(`\n🔒 HackSecure Relay Server v1.0.0`);
  console.log(`   Listening on port ${PORT}`);
  console.log(`   REST: http://localhost:${PORT}/api/v1/`);
  console.log(`   WebSocket: ws://localhost:${PORT}/ws?conv=<conversationId>`);
  console.log(`   Ghost:     ws://localhost:${PORT}/ws?ghost=<ghostChannelId>`);
  console.log(`   Server is BLIND — does not log message content\n`);
});
