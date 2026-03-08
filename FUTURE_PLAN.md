# HackSecure Messenger - Future Implementation Plan

## Current State (v1.0.2)

The application currently functions as a **local-only encrypted note-taking app** with the following working features:

### ✅ Implemented Features

- **Identity Management**
  - ✓ Generates Ed25519 keypairs
  - ✓ Stores private keys securely in Android Keystore (AES-256-GCM wrapped)
  - ✓ Computes identity fingerprints (SHA-256 of public key)
  - ✓ Generates QR codes for identity sharing

- **Contact Management**
  - ✓ Scans QR codes to add contacts
  - ✓ Verifies identity fingerprints
  - ✓ Stores verified contacts in SQLCipher database

- **Cryptographic Pipeline (Complete)**
  - ✓ X25519 Diffie-Hellman key exchange
  - ✓ Session key derivation (SHA-256 based)
  - ✓ Hash ratchet for forward secrecy (K₀→K₁→...→Kₙ)
  - ✓ ChaCha20-Poly1305 AEAD encryption
  - ✓ Ed25519 message signatures
  - ✓ Full message encryption/decryption pipeline

- **Local Storage (Complete)**
  - ✓ SQLCipher encrypted database (AES-256)
  - ✓ Per-message Keystore encryption
  - ✓ Self-destructing message mechanism (key deletion)
  - ✓ Message expiry via WorkManager

- **Server Infrastructure**
  - ✓ Node.js blind relay server (Docker-ready)
  - ✓ WebSocket relay implementation
  - ✓ Ticket-based session authentication
  - ✓ Health monitoring endpoints

### ❌ Missing Features (Critical Gap)

- **Network Layer Integration**
  - ❌ Does NOT send messages over the network
  - ❌ Cannot receive messages from other phones
  - ❌ WebSocket client not connected to ViewModels
  - ❌ No real-time message synchronization

---

## What Needs to Be Implemented

### Priority 1: Network Layer Integration (Critical)

To enable actual peer-to-peer messaging, the following components need to be connected:

#### 1. Inject RelayWebSocketClient into ChatViewModel

**File:** `app/src/main/java/com/hacksecure/messenger/presentation/viewmodel/ViewModels.kt`

**Current State:**
```kotlin
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val contactRepository: ContactRepository,
    private val identityRepository: IdentityRepository,
    private val identityKeyManager: IdentityKeyManager,
    private val sessionKeyManager: SessionKeyManager
) : ViewModel()
```

**Needs:**
```kotlin
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val contactRepository: ContactRepository,
    private val identityRepository: IdentityRepository,
    private val identityKeyManager: IdentityKeyManager,
    private val sessionKeyManager: SessionKeyManager,
    private val relayWebSocketClient: RelayWebSocketClient  // ADD THIS
) : ViewModel()
```

#### 2. Connect WebSocket When Opening Conversation

**File:** `ChatViewModel.kt` - `initConversation()` method

**Add after line 333:**
```kotlin
// Establish WebSocket connection to relay server
viewModelScope.launch {
    // Get auth token from presence API
    val authToken = relayApi.registerPresence(myIdentity.identityHex)

    // Connect to WebSocket
    relayWebSocketClient.connect(convId, authToken)

    // Listen for relay events
    relayWebSocketClient.events.collect { event ->
        when (event) {
            is RelayEvent.Connected -> {
                _uiState.update { it.copy(connectionState = ConnectionState.CONNECTED_RELAY) }
            }
            is RelayEvent.MessageReceived -> {
                receiveRawPacket(event.message.packetBytes, contact)
            }
            is RelayEvent.Disconnected -> {
                _uiState.update { it.copy(connectionState = ConnectionState.DISCONNECTED) }
            }
            is RelayEvent.Error -> {
                _events.emit(ChatEvent.ShowError("Connection error: ${event.throwable.message}"))
            }
        }
    }
}
```

#### 3. Send Messages Over Network

**File:** `ChatViewModel.kt` - Line 411

**Current Code:**
```kotlin
// TODO: Send wirePacketBytes over WebSocket relay
```

**Replace with:**
```kotlin
// Send wirePacketBytes over WebSocket relay
if (wirePacketBytes != null) {
    val sendSuccess = relayWebSocketClient.send(
        conversationId = convId,
        messageId = messageId,
        packet = wirePacketBytes
    )

    if (!sendSuccess) {
        _events.emit(ChatEvent.ShowError("Failed to send message"))
        // Update message state to failed
        withContext(Dispatchers.IO) {
            messageRepository.updateMessageState(messageId, MessageState.FAILED)
        }
    }
}
```

#### 4. Handle Incoming Messages

**Already implemented:** The `receiveRawPacket()` method exists (line 454) and correctly:
- Decrypts incoming packets
- Verifies signatures
- Stores messages in database
- Updates UI

**Just needs to be called** when WebSocket receives data (see step 2 above).

#### 5. Disconnect on Screen Exit

**Add to ChatViewModel:**
```kotlin
override fun onCleared() {
    super.onCleared()
    relayWebSocketClient.disconnect()
}
```

---

## Implementation Checklist

### Phase 1: Basic Messaging (Essential)

- [ ] Add `RelayWebSocketClient` injection to `ChatViewModel`
- [ ] Add `RelayApi` injection to `ChatViewModel` (for presence/auth)
- [ ] Implement WebSocket connection on conversation open
- [ ] Implement WebSocket disconnection on screen close
- [ ] Replace TODO comment with actual `relayClient.send()` call
- [ ] Add relay event handling for incoming messages
- [ ] Test end-to-end messaging between two phones
- [ ] Add connection status indicators in UI
- [ ] Handle network reconnection logic

### Phase 2: Enhanced UX

- [ ] Add "typing indicator" (optional feature)
- [ ] Add "message delivered" confirmation
- [ ] Add "message read" receipts (privacy-preserving)
- [ ] Improve offline message queuing
- [ ] Add retry mechanism for failed sends
- [ ] Show network quality indicators

### Phase 3: Reliability

- [ ] Implement message acknowledgment system
- [ ] Handle server-side message persistence (optional)
- [ ] Add automatic reconnection with exponential backoff
- [ ] Implement message deduplication
- [ ] Add network status monitoring
- [ ] Handle app backgrounding/foregrounding

### Phase 4: Testing

- [ ] Write integration tests for WebSocket flow
- [ ] Test with poor network conditions
- [ ] Test with server restarts
- [ ] Test message ordering with multiple rapid sends
- [ ] Test simultaneous bidirectional messaging
- [ ] Load test with large message volumes

---

## Estimated Implementation Time

| Phase | Estimated Time | Priority |
|-------|---------------|----------|
| Phase 1: Basic Messaging | 4-8 hours | **Critical** |
| Phase 2: Enhanced UX | 8-12 hours | High |
| Phase 3: Reliability | 12-16 hours | High |
| Phase 4: Testing | 8-12 hours | Medium |

**Total:** 32-48 hours of development work

---

## Known Issues to Address

1. **No Presence System Integration**
   - `RelayApi.registerPresence()` exists but not used
   - Need to call before WebSocket connection
   - Should refresh auth tokens periodically

2. **No Session Handshake Flow**
   - DH key exchange code exists (`SessionKeyManager`)
   - But no UI flow to initiate handshake
   - Need to implement session establishment protocol

3. **Message State Management**
   - `MessageState` enum exists (SENDING, SENT, DELIVERED, FAILED)
   - But states not updated based on network events
   - Need to track message lifecycle

4. **Ticket System Incomplete**
   - Server issues tickets (`/api/v1/ticket`)
   - Client has `TicketManager` to verify tickets
   - But ticket request/verification flow not implemented in UI

---

## Architecture Notes

### Current Architecture
```
[ChatViewModel] → [MessageProcessor] → [Local DB]
                       ↓
                 (encrypted bytes)
                       ↓
                   ❌ NOT SENT ❌
```

### Target Architecture
```
[ChatViewModel] → [MessageProcessor] → [Local DB]
        ↓                ↓
        ↓          (encrypted bytes)
        ↓                ↓
   [RelayWSClient] → [Relay Server] → [Other Client]
```

---

## Server Configuration

### Current Setup
- **Server URL:** `http://10.155.36.9:8443`
- **WebSocket:** `ws://10.155.36.9:8443`
- **Server Public Key:** `bf6d828ba46445d0632b8a1810ff68b97a3aa2ed06146458c0592f8706333cb7`
- **Docker Container:** `hacksecure-relay-server` (healthy)

### Verified Endpoints
- ✅ `GET /health` - Server health check
- ✅ `POST /api/v1/ticket` - Request conversation ticket
- ✅ `POST /api/v1/presence` - Register presence/get auth token
- ✅ `WS /ws?conv=<conversationId>` - WebSocket relay connection

---

## Development Priority

1. **Immediate (Week 1):** Implement Phase 1 - Basic messaging functionality
2. **Short-term (Week 2-3):** Add Phase 2 - Enhanced UX features
3. **Medium-term (Month 2):** Implement Phase 3 - Reliability improvements
4. **Ongoing:** Phase 4 - Comprehensive testing and bug fixes

---

## Success Criteria

The implementation will be considered complete when:

1. ✅ Two phones can establish a WebSocket connection to the relay server
2. ✅ Messages typed on Phone A appear on Phone B in real-time
3. ✅ Messages typed on Phone B appear on Phone A in real-time
4. ✅ Server logs show active WebSocket connections: `"connections": 2`
5. ✅ Messages are properly encrypted end-to-end
6. ✅ Connection status is visible in the UI
7. ✅ App handles network interruptions gracefully
8. ✅ All existing cryptographic guarantees are maintained

---

## References

- **ChatViewModel:** `app/src/main/java/com/hacksecure/messenger/presentation/viewmodel/ViewModels.kt`
- **RelayWebSocketClient:** `app/src/main/java/com/hacksecure/messenger/data/remote/websocket/RelayWebSocketClient.kt`
- **MessageProcessor:** `app/src/main/java/com/hacksecure/messenger/domain/crypto/MessageProcessor.kt`
- **Server Code:** `server/index.js`
- **DI Module:** `app/src/main/java/com/hacksecure/messenger/di/AppModule.kt`

---

**Last Updated:** 2026-03-09
**Status:** Network layer integration pending
**Critical Path:** Phase 1 implementation required for basic functionality
