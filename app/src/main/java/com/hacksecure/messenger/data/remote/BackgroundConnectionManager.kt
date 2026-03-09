// data/remote/BackgroundConnectionManager.kt
// Maintains relay WebSocket connections for ALL conversations while the app is alive.
// Allows messages to be received even when the chat screen is not open.
package com.hacksecure.messenger.data.remote

import com.hacksecure.messenger.data.remote.api.PresenceRequest
import com.hacksecure.messenger.data.remote.api.RelayApi
import com.hacksecure.messenger.data.remote.api.TicketRequest
import com.hacksecure.messenger.data.remote.websocket.RelayEvent
import com.hacksecure.messenger.data.remote.websocket.RelayWebSocketClient
import com.hacksecure.messenger.domain.crypto.*
import com.hacksecure.messenger.domain.model.*
import com.hacksecure.messenger.domain.repository.ContactRepository
import com.hacksecure.messenger.domain.repository.IdentityRepository
import com.hacksecure.messenger.domain.repository.MessageRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import java.util.Collections
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton that keeps a relay WebSocket alive for every known conversation.
 *
 * When the user opens a chat screen, [ChatViewModel] registers itself as the
 * active handler via [registerChatHandler]. All incoming MESSAGE packets are
 * then forwarded to ChatViewModel for decryption and UI display. When the user
 * leaves the chat, [unregisterChatHandler] is called and this manager takes over
 * background decryption + DB storage so messages are never lost.
 *
 * The DH handshake is always performed here, keeping crypto state in one place.
 * ChatViewModel receives the established [MessageProcessor] via [onSessionReady].
 */
@Singleton
class BackgroundConnectionManager @Inject constructor(
    private val contactRepository: ContactRepository,
    private val identityRepository: IdentityRepository,
    private val messageRepository: MessageRepository,
    private val handshakeManager: HandshakeManager,
    private val identityKeyManager: IdentityKeyManager,
    private val relayApi: RelayApi,
    private val serverConfig: ServerConfig,
    private val okHttpClient: OkHttpClient,
) {
    /** Long-lived supervisor scope that outlives any single ViewModel. */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // ── Per-conversation state ────────────────────────────────────────────────

    class ConvState(
        val conversationId: String,
        val contact: Contact,
        val myIdentity: LocalIdentity,
        val wsClient: RelayWebSocketClient
    ) {
        @Volatile var messageProcessor: MessageProcessor? = null
        @Volatile var ourEphemeralPair: SessionKeyManager.EphemeralKeyPair? = null
        @Volatile var ourOfferPacket: ByteArray? = null
        val seenMessageIds: MutableSet<String> = Collections.synchronizedSet(HashSet())
        var collectionJob: Job? = null

        private val _connState = MutableStateFlow(ConnectionState.DISCONNECTED)
        val connectionState: StateFlow<ConnectionState> = _connState
        fun updateConnectionState(s: ConnectionState) { _connState.value = s }
    }

    private val convStates = ConcurrentHashMap<String, ConvState>()

    // ── ChatViewModel callbacks ───────────────────────────────────────────────

    /** Raw MESSAGE packet handler set by the active ChatViewModel. */
    private val chatPacketHandlers =
        ConcurrentHashMap<String, suspend (msgId: String, packet: ByteArray) -> Unit>()

    /** Notified when the DH handshake completes for a conversation. */
    private val sessionReadyCallbacks =
        ConcurrentHashMap<String, suspend (processor: MessageProcessor) -> Unit>()

    /** Notified on connection state changes. */
    private val connStateCallbacks =
        ConcurrentHashMap<String, suspend (ConnectionState) -> Unit>()

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Called by ChatViewModel when the chat screen opens.
     * - Routes incoming MESSAGE packets to [packetHandler].
     * - Calls [onSessionReady] immediately if a session is already established,
     *   or later when the handshake completes.
     */
    fun registerChatHandler(
        convId: String,
        packetHandler: suspend (msgId: String, packet: ByteArray) -> Unit,
        onSessionReady: suspend (MessageProcessor) -> Unit,
        onConnectionStateChange: suspend (ConnectionState) -> Unit,
    ) {
        chatPacketHandlers[convId] = packetHandler
        sessionReadyCallbacks[convId] = onSessionReady
        connStateCallbacks[convId] = onConnectionStateChange

        // Notify immediately if state is already available
        val state = convStates[convId] ?: return
        scope.launch {
            onConnectionStateChange(state.connectionState.value)
            state.messageProcessor?.let { onSessionReady(it) }
        }
    }

    fun unregisterChatHandler(convId: String) {
        chatPacketHandlers.remove(convId)
        sessionReadyCallbacks.remove(convId)
        connStateCallbacks.remove(convId)
    }

    /** Returns the established [MessageProcessor] for [convId], or null. */
    fun getMessageProcessor(convId: String): MessageProcessor? =
        convStates[convId]?.messageProcessor

    /** Transmits a wire-packet for [convId] via this manager's WebSocket. */
    suspend fun sendPacket(convId: String, messageId: String, packet: ByteArray): Boolean =
        convStates[convId]?.wsClient?.send(convId, messageId, packet) ?: false

    fun isConnected(convId: String): Boolean =
        convStates[convId]?.wsClient?.isConnected() == true

    /**
     * Starts background connections for all known contacts.
     * Idempotent — safe to call multiple times.
     * Called from [HackSecureApp.onCreate].
     */
    fun startAllConnections() {
        scope.launch {
            // Wait for identity (may not exist yet on first launch)
            var identity = identityRepository.getLocalIdentity()
            if (identity == null) {
                repeat(20) {
                    delay(500)
                    identity = identityRepository.getLocalIdentity()
                    if (identity != null) return@repeat
                }
                if (identity == null) return@launch
            }
            val myIdentity = identity!!

            // Connect to existing contacts AND watch for new ones added during the session
            contactRepository.getContacts().collect { contacts ->
                contacts.forEach { contact ->
                    val convId = buildConvId(myIdentity.identityHex, contact.identityHex)
                    if (!convStates.containsKey(convId)) {
                        ensureConnected(myIdentity, contact)
                    }
                }
            }
        }
    }

    /**
     * Ensures a background connection exists for the given conversation.
     * Idempotent — no-op if already connected.
     */
    fun ensureConnected(identity: LocalIdentity, contact: Contact) {
        val convId = buildConvId(identity.identityHex, contact.identityHex)
        if (convStates.containsKey(convId)) return

        val wsClient = RelayWebSocketClient(okHttpClient, serverConfig)
        val state = ConvState(convId, contact, identity, wsClient)
        convStates[convId] = state

        scope.launch { connectConversation(state) }
    }

    /**
     * Tears down and re-establishes the connection for a conversation.
     * Called by ChatViewModel's "Retry Connection" button.
     */
    fun forceReconnect(identity: LocalIdentity, contact: Contact) {
        val convId = buildConvId(identity.identityHex, contact.identityHex)
        convStates.remove(convId)?.let { old ->
            old.collectionJob?.cancel()
            old.ourEphemeralPair?.zeroizePrivate()
            old.wsClient.disconnect()
        }
        ensureConnected(identity, contact)
    }

    // ── Internal connection lifecycle ─────────────────────────────────────────

    private suspend fun connectConversation(state: ConvState) {
        try {
            // Register presence
            val connectionToken = UUID.randomUUID().toString()
            val presence = relayApi.registerPresence(
                url = "${serverConfig.apiBaseUrl}/api/v1/presence",
                request = PresenceRequest(
                    identity_hash = state.myIdentity.identityHex,
                    connection_token = connectionToken
                )
            )
            val authToken = presence.token ?: connectionToken

            // Request session ticket (non-blocking on failure)
            try {
                val sorted = listOf(state.myIdentity.identityHex, state.contact.identityHex).sorted()
                relayApi.requestTicket(
                    url = "${serverConfig.apiBaseUrl}/api/v1/ticket",
                    request = TicketRequest(a_id = sorted[0], b_id = sorted[1])
                )
            } catch (_: Exception) { /* ticket is advisory */ }

            // Connect WebSocket (RelayWebSocketClient handles auto-reconnect internally)
            state.wsClient.connect(state.conversationId, authToken)

            // Collect relay events for this conversation
            state.collectionJob = scope.launch {
                state.wsClient.events.collect { event ->
                    when (event) {
                        is RelayEvent.Connected -> {
                            state.updateConnectionState(ConnectionState.CONNECTED_RELAY)
                            connStateCallbacks[state.conversationId]
                                ?.invoke(ConnectionState.CONNECTED_RELAY)

                            // Send our DH handshake offer
                            val (offerPacket, ephemeralPair) = withContext(Dispatchers.Default) {
                                handshakeManager.createOffer()
                            }
                            state.ourEphemeralPair = ephemeralPair
                            state.ourOfferPacket = offerPacket
                            state.wsClient.send(
                                state.conversationId,
                                "hs_bg_${UUID.randomUUID()}",
                                offerPacket
                            )
                        }

                        is RelayEvent.MessageReceived -> {
                            if (event.message.conversationId == state.conversationId) {
                                val packetBytes = event.message.packetBytes
                                if (handshakeManager.isHandshakeOffer(packetBytes)) {
                                    // Handshake is always handled here, never routed to ChatViewModel
                                    completeHandshake(state, packetBytes)
                                } else {
                                    // Route to ChatViewModel if active, otherwise store in background
                                    val handler = chatPacketHandlers[state.conversationId]
                                    if (handler != null) {
                                        handler(event.message.messageId, packetBytes)
                                    } else {
                                        receiveInBackground(state, event.message.messageId, packetBytes)
                                    }
                                }
                            }
                        }

                        is RelayEvent.Disconnected -> {
                            state.updateConnectionState(ConnectionState.DISCONNECTED)
                            connStateCallbacks[state.conversationId]
                                ?.invoke(ConnectionState.DISCONNECTED)
                        }

                        is RelayEvent.Error -> {
                            state.updateConnectionState(ConnectionState.ERROR)
                            connStateCallbacks[state.conversationId]?.invoke(ConnectionState.ERROR)
                        }
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // RelayWebSocketClient handles reconnect; we just ensure state is correct
        }
    }

    // ── Handshake ─────────────────────────────────────────────────────────────

    private suspend fun completeHandshake(state: ConvState, offerBytes: ByteArray) {
        if (state.messageProcessor != null) return // already established — ignore duplicate

        // Echo our offer so a late-joining peer can also complete the handshake
        state.ourOfferPacket?.let { stored ->
            state.wsClient.send(
                state.conversationId,
                "hs_echo_bg_${UUID.randomUUID()}",
                stored
            )
        }

        val ephemeralPair = state.ourEphemeralPair ?: return

        try {
            val sessionKey = withContext(Dispatchers.Default) {
                handshakeManager.deriveSessionKey(
                    offerBytes = offerBytes,
                    peerIdentityPublicKey = state.contact.publicKeyBytes,
                    ourEphemeralPair = ephemeralPair,   // private key zeroized inside
                    myIdentityHash = state.myIdentity.identityHash,
                    peerIdentityHash = state.contact.identityHash
                )
            }
            state.ourEphemeralPair = null

            val sendRatchet = HashRatchet(sessionKey.copyOf())
            val recvRatchet = HashRatchet(sessionKey.copyOf())
            sessionKey.fill(0) // zeroize root

            val processor = MessageProcessor(
                sendRatchet = sendRatchet,
                recvRatchet = recvRatchet,
                identityKeyManager = identityKeyManager,
                peerPublicKeyBytes = state.contact.publicKeyBytes,
                myIdentityHash = state.myIdentity.identityHash
            )
            state.messageProcessor = processor
            state.updateConnectionState(ConnectionState.CONNECTED_RELAY)

            // Notify active ChatViewModel that the session is ready
            sessionReadyCallbacks[state.conversationId]?.invoke(processor)
            connStateCallbacks[state.conversationId]?.invoke(ConnectionState.CONNECTED_RELAY)

        } catch (_: CryptoError.SignatureInvalid) {
            // Peer signature invalid — ignore offer
        } catch (_: Exception) { }
    }

    // ── Background message processing ─────────────────────────────────────────

    private suspend fun receiveInBackground(
        state: ConvState,
        messageId: String,
        packetBytes: ByteArray
    ) {
        if (!state.seenMessageIds.add(messageId)) return // deduplicate
        val processor = state.messageProcessor ?: return // can't decrypt without session

        try {
            val (plaintextBytes, header) = withContext(Dispatchers.Default) {
                processor.receive(packetBytes)
            }
            val text = plaintextBytes.toString(Charsets.UTF_8)
            val now = System.currentTimeMillis()

            val message = Message(
                id = UUID.randomUUID().toString(),
                conversationId = state.conversationId,
                senderId = header.senderId.toHexString(),
                content = text,
                timestampMs = header.timestampMs,
                counter = header.counter,
                expirySeconds = header.expirySeconds,
                expiryDeadlineMs = if (header.expirySeconds > 0)
                    now + (header.expirySeconds * 1000L) else null,
                isOutgoing = false,
                state = MessageState.DELIVERED,
                messageType = header.messageType
            )

            withContext(Dispatchers.IO) {
                messageRepository.saveMessage(message, plaintextBytes)
            }
        } catch (_: CryptoError) {
            // Silently discard messages that fail crypto checks in background
        } catch (_: Exception) { }
    }

    // ── Shutdown ──────────────────────────────────────────────────────────────

    fun stopAll() {
        convStates.values.forEach { state ->
            state.collectionJob?.cancel()
            state.ourEphemeralPair?.zeroizePrivate()
            state.wsClient.disconnect()
        }
        convStates.clear()
        scope.cancel()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun buildConvId(a: String, b: String): String {
        val sorted = listOf(a, b).sorted()
        return "${sorted[0]}_${sorted[1]}"
    }

    private fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }
}
