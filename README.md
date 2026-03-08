# HackSecure Messenger — v1.0.0

> Secure peer-to-peer chat with end-to-end encryption, self-destructing messages, and basic anonymity

---

## Architecture: The 6-Stage Integrity Chain

```
Stage 1: Identity       → Ed25519 keypair (BouncyCastle + Keystore-wrapped)
Stage 2: Session Auth   → Server-signed Kerberos-style ticket (140 bytes)
Stage 3: Key Derivation → X25519 DH + SHA-256(secret ∥ A_id ∥ B_id ∥ ticket_hash ∥ ts)
Stage 4: Ratchet        → SHA-256 hash chain (K₀→K₁→…Kₙ) for forward secrecy
Stage 5: Message        → ChaCha20-Poly1305 AEAD + Ed25519 outer signature
Stage 6: Storage/Expiry → SQLCipher + per-message Keystore keys (deleted on expiry)
```

Each stage commits to the previous via hashes and signatures. Tampering at any link breaks all subsequent links.

---

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 100% |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Async | Coroutines + Flow |
| Crypto | BouncyCastle (Ed25519, X25519, ChaCha20-Poly1305) |
| Key Storage | Android Keystore (AES-256-GCM wrapping) |
| Local DB | Room + SQLCipher (AES-256) |
| Networking | OkHttp WebSocket + Retrofit |
| Background | WorkManager (expiry) |
| QR | ZXing |

---

## Cryptographic Primitives

### Identity (Stage 1)
- **Algorithm**: Ed25519 (BouncyCastle)
- **Private key**: AES-256-GCM encrypted, stored in Android Keystore
- **Fingerprint**: SHA-256(public_key) displayed as hex, verified out-of-band
- **QR format**: `hacksecure://id/v1/<BASE64URL(0x01 | public_key_32_bytes)>`

### Session Key (Stage 3)
```
K₀ = SHA-256(X25519_shared_secret ∥ A_id ∥ B_id ∥ ticket_hash ∥ timestamp_ms)
```
- Both peer identities bound — prevents server from swapping one participant
- Ticket hash bound — authenticates server was in the trust path during setup
- Ephemeral X25519 private key **zeroized** immediately after shared secret computed

### Message Encryption (Stage 5)
```
nonce      = SHA-256(Kₙ ∥ counter)[0:12]
header_hash = SHA-256(header_json)
ciphertext  = ChaCha20-Poly1305-Encrypt(Kₙ, nonce, plaintext, additionalData=header_hash)
sig_digest  = SHA-256(header_bytes ∥ ciphertext)
signature   = Ed25519_Sign(identity_private_key, sig_digest)
```

### Receive Pipeline (strict validation order)
1. Parse wire packet
2. **Verify Ed25519 signature** — if fails: REJECT. Do NOT attempt decryption.
3. **Verify timestamp** — reject if |now - ts| > 5 minutes
4. **Verify counter** — reject if counter ≤ last_seen (replay protection)
5. Advance ratchet to counter position
6. Derive nonce deterministically
7. **AEAD decrypt** with header_hash as additional data
8. Update last_seen counter only on success

---

## Wire Packet Format

```
[4 bytes big-endian] header_length
[N bytes]            JSON header (senderId, timestampMs, counter, expirySeconds, messageType)
[4 bytes big-endian] ciphertext_length
[M bytes]            ciphertext + 16-byte Poly1305 tag
[64 bytes]           Ed25519 signature
```

---

## Message Storage (Stage 6 — Dual Encryption)

1. **Network layer**: Messages encrypted with session ratchet key (ChaCha20-Poly1305)
2. **Storage layer**: Decrypted message re-encrypted with a per-message AES-256-GCM key stored in Android Keystore
3. **Database**: SQLCipher-encrypted Room database (AES-256)

**Expiry mechanism:**
1. WorkManager deletes the Keystore entry for the message key
2. Overwrites ciphertext blob with zeros in DB
3. Deletes the row

Without the Keystore key, the stored ciphertext is **unrecoverable noise**.

---

## Project Structure

```
app/src/main/java/com/hacksecure/messenger/
├── di/                         Hilt modules
├── domain/
│   ├── model/                  DomainModels.kt — pure Kotlin, no Android deps
│   ├── crypto/
│   │   ├── IdentityKeyManager  Ed25519 keypair management
│   │   ├── SessionKeyManager   X25519 DH + session key derivation
│   │   ├── HashRatchet         SHA-256 ratchet (forward secrecy)
│   │   ├── AEADCipher          ChaCha20-Poly1305 encryption/decryption
│   │   ├── TicketManager       Server ticket verification
│   │   └── MessageProcessor    Full send/receive pipeline (stages 4+5)
│   └── repository/             Repository interfaces
├── data/
│   ├── local/
│   │   ├── db/                 Room database + DAOs + entities (SQLCipher)
│   │   └── keystore/           Android Keystore wrapper
│   ├── remote/
│   │   ├── api/                Retrofit interfaces
│   │   └── websocket/          OkHttp WebSocket relay client
│   └── repository/             Repository implementations
├── presentation/
│   ├── ui/
│   │   ├── screens/            Composable screens (all 8 screens)
│   │   ├── components/         Reusable Composables
│   │   └── theme/              MaterialTheme colors + typography
│   └── viewmodel/              ViewModels (one per screen)
├── worker/                     MessageExpiryWorker (WorkManager)
├── HackSecureApp.kt
└── MainActivity.kt             Navigation graph

server/
├── index.js                    Node.js blind relay server
└── package.json
```

---

## Security Properties

| Property | Implementation |
|---|---|
| End-to-end encryption | ChaCha20-Poly1305 with ratcheted keys |
| Forward secrecy | SHA-256 ratchet — past keys zeroized after each message |
| Message authentication | Ed25519 outer signature + Poly1305 inner tag |
| Replay protection | Strictly monotonic counter per sender |
| Timestamp freshness | ±5 minute window checked before decryption |
| Key pinning | Contact public keys pinned, key changes trigger warning |
| Storage security | SQLCipher + per-message Keystore keys |
| Self-destructing messages | Keystore key deletion makes ciphertext unrecoverable |
| Screenshot protection | FLAG_SECURE on all message windows (toggleable) |
| No cloud backup | data_extraction_rules.xml excludes all sensitive files |
| Ephemeral key zeroization | X25519 private key filled with 0x00 after DH |
| Blind relay | Server routes bytes, never inspects content |

---

## Version Roadmap

| Version | Features |
|---|---|
| **v1.0.0** (current) | Messaging — full E2EE, self-destruct, QR identity |
| v1.1.0 | Voice notes (opus encoded, encrypted same pipeline) |
| v2.0.0 | Voice calls (WebRTC DataChannel → audio, same crypto) |
| v3.0.0 | Group messaging |

---

## Building

```bash
# Android app
./gradlew assembleDebug

# Run tests
./gradlew test

# Relay server
cd server && npm install && node index.js
```

---

## Testing

All 8 test scenarios per spec:
1. IdentityHash SHA-256 known vectors
2. X25519 DH symmetry: DH(A,B_pub) == DH(B,A_pub)
3. Ratchet: K₅≠K₄≠K₃, forward preimage resistance
4. AEAD: correct roundtrip, bit-flip in ciphertext → fail, bit-flip in AD → fail
5. Counter replay detection
6. Ticket verification: valid/tampered/expired
7. Full two-party 5-message integration test
8. MITM public key substitution → AEAD failure

---

*Security constraint: the integrity chain is only as strong as its weakest link. Every stage is mandatory.*
