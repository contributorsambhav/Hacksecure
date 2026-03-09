// presentation/viewmodel/ViewModels.kt
package com.hacksecure.messenger.presentation.viewmodel

import android.util.Base64
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hacksecure.messenger.data.remote.api.PresenceRequest
import com.hacksecure.messenger.data.remote.api.RelayApi
import com.hacksecure.messenger.data.remote.ServerConfig
import com.hacksecure.messenger.data.remote.api.TicketRequest
import com.hacksecure.messenger.data.remote.websocket.RelayEvent
import com.hacksecure.messenger.data.remote.websocket.RelayWebSocketClient
import com.hacksecure.messenger.domain.crypto.*
import com.hacksecure.messenger.domain.model.*
import com.hacksecure.messenger.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
// SPLASH / ONBOARDING VIEWMODEL
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
data class SplashUiState(
    val isLoading: Boolean = true,
    val isReady: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val identityRepository: IdentityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init { initializeIdentity() }

    private fun initializeIdentity() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                var identity = identityRepository.getLocalIdentity()
                if (identity == null) {
                    identity = identityRepository.generateIdentity()
                }
                _uiState.update { it.copy(isLoading = false, isReady = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Failed to initialize identity: ${e.message}") }
            }
        }
    }
}

// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
// HOME VIEWMODEL
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
data class HomeUiState(
    val conversations: List<ConversationUiItem> = emptyList(),
    val isLoading: Boolean = true
)

data class ConversationUiItem(
    val conversationId: String,
    val contact: Contact,
    val lastMessageAt: Long,
    val unreadCount: Int,
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
    private val identityRepository: IdentityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val myHex = withContext(Dispatchers.IO) {
                identityRepository.getLocalIdentity()?.identityHex ?: ""
            }
            contactRepository.getContacts().collect { contacts ->
                val items = contacts.map { contact ->
                    val convId = buildConversationId(myHex, contact.identityHex)
                    ConversationUiItem(
                        conversationId = convId,
                        contact = contact,
                        lastMessageAt = contact.verifiedAt,
                        unreadCount = 0
                    )
                }
                _uiState.update { it.copy(conversations = items, isLoading = false) }
            }
        }
    }

    private fun buildConversationId(a: String, b: String): String {
        val sorted = listOf(a, b).sorted()
        return "${sorted[0]}_${sorted[1]}"
    }
}

// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
// QR DISPLAY VIEWMODEL
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
data class QrDisplayUiState(
    val qrPayload: String = "",
    val fingerprintHex: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class QrDisplayViewModel @Inject constructor(
    private val identityRepository: IdentityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QrDisplayUiState())
    val uiState: StateFlow<QrDisplayUiState> = _uiState.asStateFlow()

    init { loadIdentity() }

    private fun loadIdentity() {
        viewModelScope.launch(Dispatchers.Default) {
            val identity = identityRepository.getLocalIdentity() ?: return@launch
            val qrPayload = buildQrPayload(identity.publicKeyBytes)
            val fingerprint = formatFingerprint(identity.identityHex)
            _uiState.update {
                it.copy(qrPayload = qrPayload, fingerprintHex = fingerprint, isLoading = false)
            }
        }
    }

    /**
     * QR payload: "hacksecure://id/v1/<BASE64URL_NO_PADDING>"
     * Binary: [1 byte version=0x01][32 bytes public key]
     */
    private fun buildQrPayload(publicKeyBytes: ByteArray): String {
        val payload = ByteArray(33)
        payload[0] = 0x01  // version
        publicKeyBytes.copyInto(payload, 1)
        val b64 = Base64.encodeToString(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        return "hacksecure://id/v1/$b64"
    }

    /** Groups fingerprint hex into blocks of 8 for readability */
    private fun formatFingerprint(hex: String): String =
        hex.chunked(8).joinToString(" ")
}

// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
// QR SCAN VIEWMODEL
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
data class QrScanResult(
    val publicKeyBytes: ByteArray,
    val identityHash: ByteArray,
    val fingerprintHex: String
)

sealed class ScanState {
    object Idle : ScanState()
    data class Success(val result: QrScanResult) : ScanState()
    data class Error(val message: String) : ScanState()
}

@HiltViewModel
class QrScanViewModel @Inject constructor() : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    fun processQrResult(rawValue: String) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                if (!rawValue.startsWith("hacksecure://id/v1/")) {
                    _scanState.update { ScanState.Error("Invalid QR code format") }
                    return@launch
                }
                val b64Part = rawValue.removePrefix("hacksecure://id/v1/")
                val decoded = Base64.decode(b64Part, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)

                if (decoded.size < 33) {
                    _scanState.update { ScanState.Error("QR payload too short") }
                    return@launch
                }
                val version = decoded[0]
                if (version != 0x01.toByte()) {
                    _scanState.update { ScanState.Error("Unsupported QR version") }
                    return@launch
                }
                val publicKeyBytes = decoded.copyOfRange(1, 33)
                val identityHash = IdentityHash.compute(publicKeyBytes)
                val fingerprintHex = identityHash.joinToString("") { "%02x".format(it) }
                    .chunked(8).joinToString(" ")

                _scanState.update {
                    ScanState.Success(QrScanResult(publicKeyBytes, identityHash, fingerprintHex))
                }
            } catch (e: Exception) {
                _scanState.update { ScanState.Error("Failed to parse QR code") }
            }
        }
    }

    fun reset() = _scanState.update { ScanState.Idle }
}

// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
// CONTACT CONFIRMATION VIEWMODEL
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
sealed class ConfirmState {
    object Idle : ConfirmState()
    object Saving : ConfirmState()
    object Saved : ConfirmState()
    data class KeyChangeWarning(val existingContact: Contact) : ConfirmState()
    data class Error(val message: String) : ConfirmState()
}

@HiltViewModel
class ContactConfirmViewModel @Inject constructor(
    private val contactRepository: ContactRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ConfirmState>(ConfirmState.Idle)
    val state: StateFlow<ConfirmState> = _state.asStateFlow()

    fun saveContact(
        publicKeyBytes: ByteArray,
        identityHash: ByteArray,
        displayName: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { ConfirmState.Saving }

            val identityHex = identityHash.joinToString("") { "%02x".format(it) }
            val existing = contactRepository.getContactByIdentityHash(identityHex)

            if (existing != null && !existing.publicKeyBytes.contentEquals(publicKeyBytes)) {
                // Key changed â€” warn user
                _state.update { ConfirmState.KeyChangeWarning(existing) }
                return@launch
            }

            val contact = Contact(
                id = existing?.id ?: UUID.randomUUID().toString(),
                identityHash = identityHash,
                publicKeyBytes = publicKeyBytes,
                displayName = displayName.ifBlank { "Unknown Contact" },
                verifiedAt = System.currentTimeMillis(),
                keyChangedAt = if (existing != null &&
                    !existing.publicKeyBytes.contentEquals(publicKeyBytes))
                    System.currentTimeMillis() else null
            )

            if (existing == null) contactRepository.saveContact(contact)
            else contactRepository.updateContact(contact)

            _state.update { ConfirmState.Saved }
        }
    }

    fun forceUpdateKey(
        existing: Contact,
        newPublicKeyBytes: ByteArray,
        newIdentityHash: ByteArray
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = existing.copy(
                publicKeyBytes = newPublicKeyBytes,
                identityHash = newIdentityHash,
                keyChangedAt = System.currentTimeMillis()
            )
            contactRepository.updateContact(updated)
            _state.update { ConfirmState.Saved }
        }
    }
}

// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
// CHAT VIEWMODEL
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val contact: Contact? = null,
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val sessionEstablished: Boolean = false,
    val defaultExpirySeconds: Int = 0,
    val isLoading: Boolean = true,
    val inputText: String = "",
    val selectedExpirySeconds: Int = 0,
    val serverRelayUrl: String = ""
)

sealed class ChatEvent {
    data class ShowError(val message: String) : ChatEvent()
    data class MessageRejected(val reason: String) : ChatEvent()
    object SessionSecured : ChatEvent()
    object Snackbar : ChatEvent()
    /** Emitted when re-queuing previously-FAILED messages on reconnect. */
    object RetrySucceeded : ChatEvent()
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val contactRepository: ContactRepository,
    private val identityRepository: IdentityRepository,
    private val identityKeyManager: IdentityKeyManager,
    private val sessionKeyManager: SessionKeyManager,
    private val handshakeManager: HandshakeManager,
    private val relayWebSocketClient: RelayWebSocketClient,
    private val relayApi: RelayApi,
    private val serverConfig: ServerConfig
) : ViewModel(), DefaultLifecycleObserver {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ChatEvent>()
    val events: SharedFlow<ChatEvent> = _events.asSharedFlow()

    // â”€â”€ Session state â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Volatile private var messageProcessor: MessageProcessor? = null
    @Volatile private var conversationId: String? = null
    @Volatile private var activeContact: Contact? = null
    @Volatile private var myIdentity: LocalIdentity? = null

    /** Our ephemeral X25519 keypair for the current handshake round. */
    @Volatile private var ourEphemeralPair: SessionKeyManager.EphemeralKeyPair? = null

    /**
     * The serialized 97-byte HANDSHAKE_OFFER we sent. Kept so we can re-send it to a peer
     * that connected after our initial offer (relay doesn't buffer — they missed it).
     */
    @Volatile private var ourOfferPacket: ByteArray? = null

    /** Job controlling the relay event-collection coroutine; cancelled before each reconnect. */
    private var relayJob: Job? = null

    /** In-memory de-duplication for relay messages processed this session. */
    private val seenMessageIds = mutableSetOf<String>()

    /** Hash ratchets keyed by conversationId â€” zeroized on ViewModel clear. */
    private val sendRatchets = mutableMapOf<String, HashRatchet>()
    private val recvRatchets = mutableMapOf<String, HashRatchet>()

    // â”€â”€ Initialisation â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    fun initConversation(contactId: String) {
        viewModelScope.launch {
            val contact = withContext(Dispatchers.IO) {
                contactRepository.getContact(contactId)
            } ?: return@launch

            val identity = withContext(Dispatchers.IO) {
                identityRepository.getLocalIdentity()
            } ?: return@launch

            conversationId = buildConversationId(identity.identityHex, contact.identityHex)
            activeContact = contact
            myIdentity = identity
            _uiState.update { it.copy(contact = contact, isLoading = false) }

            // Connect relay concurrently â€” does not block the message-collection Flow below
            connectRelay(conversationId!!, identity, contact)

            // Room Flow suspends here indefinitely, updating UI whenever DB changes
            messageRepository.getMessages(conversationId!!).collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }

    // â”€â”€ Relay connection â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Starts (or restarts) the WebSocket relay connection and the event-collection
     * coroutine.  Any previous relay session is cancelled first so we never accumulate
     * stale coroutines.
     */
    private fun connectRelay(convId: String, identity: LocalIdentity, contact: Contact) {
        // Cancel any existing relay session
        relayJob?.cancel()
        // Discard the existing session — fresh handshake required on every new connection
        messageProcessor = null
        ourEphemeralPair?.zeroizePrivate()
        ourEphemeralPair = null
        ourOfferPacket = null
        _uiState.update { it.copy(connectionState = ConnectionState.CONNECTING, sessionEstablished = false) }

        relayJob = viewModelScope.launch {
            try {
                // Store current server URL for troubleshooting UI
                _uiState.update { it.copy(serverRelayUrl = serverConfig.relayBaseUrl) }

                // Register presence and get auth token
                val connectionToken = UUID.randomUUID().toString()
                val presence = withContext(Dispatchers.IO) {
                    relayApi.registerPresence(
                        url = "${serverConfig.apiBaseUrl}/api/v1/presence",
                        request = PresenceRequest(
                            identity_hash = identity.identityHex,
                            connection_token = connectionToken
                        )
                    )
                }
                val authToken = presence.token ?: connectionToken

                // Request session ticket so server can authorise the conversation
                try {
                    val sortedIds = listOf(identity.identityHex, contact.identityHex).sorted()
                    withContext(Dispatchers.IO) {
                        relayApi.requestTicket(
                            url = "${serverConfig.apiBaseUrl}/api/v1/ticket",
                            request = TicketRequest(a_id = sortedIds[0], b_id = sortedIds[1])
                        )
                    }
                } catch (_: Exception) { /* ticket is advisory; failure does not block connection */ }

                relayWebSocketClient.connect(convId, authToken)

                // Collect relay events sequentially â€” each event is fully handled before
                // the next is dispatched, preventing handshake race conditions.
                relayWebSocketClient.events.collect { event ->
                    when (event) {
                        is RelayEvent.Connected -> {
                            // Generate ephemeral keypair and send HANDSHAKE_OFFER on
                            // Dispatchers.Default so we don't block the Main thread.
                            // Because we use withContext (not launch), the next event will
                            // NOT be dispatched until ourEphemeralPair is set â€” eliminating
                            // the race between sending and receiving the peer's offer.
                            val (offerPacket, ephemeralPair) = withContext(Dispatchers.Default) {
                                handshakeManager.createOffer()
                            }
                            ourEphemeralPair = ephemeralPair
                            ourOfferPacket = offerPacket   // keep a copy for late-joiner re-send
                            withContext(Dispatchers.IO) {
                                relayWebSocketClient.send(
                                    conversationId = convId,
                                    messageId = "hs_${UUID.randomUUID()}",
                                    packet = offerPacket
                                )
                            }
                            _uiState.update { it.copy(connectionState = ConnectionState.CONNECTED_RELAY) }
                        }

                        is RelayEvent.MessageReceived -> {
                            // Guard: only handle packets belonging to this conversation
                            if (event.message.conversationId == convId) {
                                handleIncomingPacket(event.message.messageId, event.message.packetBytes, contact, identity)
                            }
                        }

                        is RelayEvent.Disconnected -> {
                            _uiState.update { it.copy(connectionState = ConnectionState.DISCONNECTED) }
                        }

                        is RelayEvent.Error -> {
                            _uiState.update { it.copy(connectionState = ConnectionState.ERROR) }
                            _events.emit(ChatEvent.ShowError("Connection error â€” retryingâ€¦"))
                        }
                    }
                }
            } catch (_: CancellationException) {
                // Normal cancellation from relayJob?.cancel() â€” do nothing
            } catch (e: Exception) {
                _uiState.update { it.copy(connectionState = ConnectionState.ERROR) }
                _events.emit(ChatEvent.ShowError("Could not connect to relay server"))
            }
        }
    }

    // â”€â”€ App Lifecycle â€” reconnect when app returns from background â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    override fun onStart(owner: LifecycleOwner) {
        if (relayWebSocketClient.isConnected()) return
        val convId = conversationId ?: return
        val contact = activeContact ?: return

        viewModelScope.launch {
            val identity = myIdentity
                ?: withContext(Dispatchers.IO) { identityRepository.getLocalIdentity() }
                    ?.also { myIdentity = it }
                ?: return@launch
            connectRelay(convId, identity, contact)
        }
    }

    // â”€â”€ DH Handshake â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Discriminates between a HANDSHAKE_OFFER and an encrypted MESSAGE, then
     * routes accordingly.  Called for every packet received from the relay.
     */
    private fun handleIncomingPacket(
        messageId: String,
        packetBytes: ByteArray,
        contact: Contact,
        myIdentity: LocalIdentity
    ) {
        if (handshakeManager.isHandshakeOffer(packetBytes)) {
            completeHandshake(packetBytes, contact, myIdentity)
        } else {
            receiveMessage(messageId, packetBytes)
        }
    }

    /**
     * Processes a peer HANDSHAKE_OFFER: verifies the Ed25519 signature, performs
     * X25519 DH, and establishes the session.
     *
     * If the session is already established (e.g. duplicate offer), this is a no-op
     * so the ratchet state is not corrupted.
     */
    private fun completeHandshake(offerBytes: ByteArray, contact: Contact, identity: LocalIdentity) {
        // Ignore duplicate offers once we have a live session
        if (messageProcessor != null) return

        // ── Late-joiner fix ───────────────────────────────────────────────────────────
        // The relay server does NOT buffer packets. If we sent our offer before the peer
        // connected, they never received it. When we receive the peer's offer, echo our
        // stored offer back so they can also complete the handshake.
        ourOfferPacket?.let { storedOffer ->
            val convId = conversationId ?: return
            viewModelScope.launch(Dispatchers.IO) {
                relayWebSocketClient.send(
                    conversationId = convId,
                    messageId = "hs_echo_${UUID.randomUUID()}",
                    packet = storedOffer
                )
            }
        }

        val ephemeralPair = ourEphemeralPair ?: run {
            // Our own offer hasn't been prepared yet — extremely rare timing edge-case;
            // the peer's offer will be ignored and the session established on the next
            // connection round (relay backoff reconnect).
            return
        }

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val sessionKey = handshakeManager.deriveSessionKey(
                    offerBytes = offerBytes,
                    peerIdentityPublicKey = contact.publicKeyBytes,
                    ourEphemeralPair = ephemeralPair,   // private key is zeroized inside
                    myIdentityHash = identity.identityHash,
                    peerIdentityHash = contact.identityHash
                )
                // Clear reference â€” private key already zeroized inside deriveSessionKey
                ourEphemeralPair = null

                onSessionEstablished(sessionKey, contact, identity)
                // sessionKey is zeroized inside onSessionEstablished after creating ratchets
            } catch (e: CryptoError.SignatureInvalid) {
                _events.emit(ChatEvent.ShowError("Handshake rejected â€” invalid signature from peer"))
            } catch (e: Exception) {
                _events.emit(ChatEvent.ShowError("Failed to establish secure session"))
            }
        }
    }

    /**
     * Installs a fully-established session into the ViewModel.
     * Also callable externally (e.g. for testing or future manual key exchange flows).
     *
     * @param sessionKey 32-byte root ratchet key â€” ZEROIZED by this function after use
     */
    fun onSessionEstablished(sessionKey: ByteArray, contact: Contact, myIdentity: LocalIdentity) {
        viewModelScope.launch(Dispatchers.Default) {
            val convId = conversationId ?: return@launch

            val sendRatchet = HashRatchet(sessionKey.copyOf())
            val recvRatchet = HashRatchet(sessionKey.copyOf())
            sessionKey.fill(0) // zeroize root key after creating both ratchets

            sendRatchets[convId] = sendRatchet
            recvRatchets[convId] = recvRatchet

            messageProcessor = MessageProcessor(
                sendRatchet = sendRatchet,
                recvRatchet = recvRatchet,
                identityKeyManager = identityKeyManager,
                peerPublicKeyBytes = contact.publicKeyBytes,
                myIdentityHash = myIdentity.identityHash
            )

            _uiState.update { it.copy(sessionEstablished = true, connectionState = ConnectionState.CONNECTED_RELAY) }
            _events.emit(ChatEvent.SessionSecured)
        }
    }

    // â”€â”€ Input helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    fun updateInputText(text: String) = _uiState.update { it.copy(inputText = text) }

    fun setExpirySeconds(seconds: Int) = _uiState.update { it.copy(selectedExpirySeconds = seconds) }

    // â”€â”€ Send â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Encrypts and sends the current input text through the full cryptographic pipeline.
     *
     * Ordering (correct):
     *   1. Encrypt plaintext â†’ wirePacketBytes
     *   2. Save message to DB as SENDING
     *   3. Send wirePacketBytes over relay
     *   4. Update message state to SENT or FAILED based on relay result
     */
    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank()) return

        val convId = conversationId ?: run {
            viewModelScope.launch { _events.emit(ChatEvent.ShowError("No active conversation")) }
            return
        }

        val processor = messageProcessor
        val expirySeconds = _uiState.value.selectedExpirySeconds

        viewModelScope.launch {
            try {
                // Clear input optimistically on Main thread
                _uiState.update { it.copy(inputText = "") }

                val messageId = UUID.randomUUID().toString()
                val now = System.currentTimeMillis()
                val plaintextBytes = text.toByteArray(Charsets.UTF_8)

                val counter: Long
                val wirePacketBytes: ByteArray?

                if (processor != null) {
                    // Full encryption pipeline on Default dispatcher
                    val result = withContext(Dispatchers.Default) {
                        processor.send(plaintext = plaintextBytes, expirySeconds = expirySeconds)
                    }
                    wirePacketBytes = result.first
                    counter = result.second
                } else {
                    // Offline / session-not-yet-established mode
                    counter = withContext(Dispatchers.IO) {
                        val myPubKey = identityKeyManager.getPublicKeyBytes()
                        val myIdHex = IdentityHash.compute(myPubKey).toHexString()
                        messageRepository.getMaxCounter(convId, myIdHex) + 1
                    }
                    wirePacketBytes = null
                }

                val myPubKey = withContext(Dispatchers.Default) { identityKeyManager.getPublicKeyBytes() }
                val myIdHex = IdentityHash.compute(myPubKey).toHexString()

                // Always save as SENDING first, then update after network result
                val message = Message(
                    id = messageId,
                    conversationId = convId,
                    senderId = myIdHex,
                    content = text,
                    timestampMs = now,
                    counter = counter,
                    expirySeconds = expirySeconds,
                    expiryDeadlineMs = if (expirySeconds > 0) now + (expirySeconds * 1000L) else null,
                    isOutgoing = true,
                    state = MessageState.SENDING
                )

                // Step 2: persist to DB (row MUST exist before any state updates below)
                withContext(Dispatchers.IO) {
                    messageRepository.saveMessage(message, plaintextBytes)
                }

                // Step 3 & 4: send, then update state
                if (wirePacketBytes != null) {
                    val sendSuccess = withContext(Dispatchers.IO) {
                        relayWebSocketClient.send(
                            conversationId = convId,
                            messageId = messageId,
                            packet = wirePacketBytes
                        )
                    }
                    val finalState = if (sendSuccess) MessageState.SENT else MessageState.FAILED
                    withContext(Dispatchers.IO) {
                        messageRepository.updateMessageState(messageId, finalState)
                    }
                    if (!sendSuccess) {
                        _events.emit(ChatEvent.ShowError("Message queued â€” will retry when reconnected"))
                    }
                }
                // wirePacketBytes == null: no-op, message stays as SENDING until session is established

            } catch (e: CryptoError) {
                _events.emit(ChatEvent.ShowError("Encryption failed"))
            } catch (e: Exception) {
                _events.emit(ChatEvent.ShowError("Failed to send message"))
            }
        }
    }

    // â”€â”€ Receive â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Decrypts and persists an incoming wire-packet received from the relay.
     *
     * De-duplicates using the relay-level [messageId] so retransmits (network glitches)
     * are silently discarded.
     */
    private fun receiveMessage(messageId: String, packetBytes: ByteArray) {
        if (!seenMessageIds.add(messageId)) return // duplicate â€” discard

        val processor = messageProcessor ?: return
        val convId = conversationId ?: return

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val (plaintextBytes, header) = processor.receive(packetBytes)
                val text = plaintextBytes.toString(Charsets.UTF_8)
                val now = System.currentTimeMillis()
                val storedId = UUID.randomUUID().toString()

                val message = Message(
                    id = storedId,
                    conversationId = convId,
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

            } catch (e: CryptoError.SignatureInvalid) {
                _events.emit(ChatEvent.MessageRejected("signature_invalid"))
            } catch (e: CryptoError.TimestampStale) {
                _events.emit(ChatEvent.MessageRejected("timestamp_stale"))
            } catch (e: CryptoError.ReplayDetected) {
                _events.emit(ChatEvent.MessageRejected("replay_detected"))
            } catch (e: CryptoError.AEADAuthFailed) {
                _events.emit(ChatEvent.MessageRejected("aead_auth_failed"))
            } catch (e: CryptoError) {
                _events.emit(ChatEvent.MessageRejected("crypto_error"))
            } catch (e: CancellationException) {
                throw e  // never swallow coroutine cancellation
            } catch (e: Exception) {
                // Catches malformed wire data (e.g. BufferUnderflowException)
                // Prevents uncaught RuntimeExceptions from crashing the app.
                _events.emit(ChatEvent.MessageRejected("parse_error"))
            }
        }
    }

    // â”€â”€ Retry â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Re-queues all FAILED outgoing messages for the current conversation.
     * Called automatically in [ChatScreen] when [ConnectionState.CONNECTED_RELAY] fires.
     *
     * Note: because we don't persist wire bytes, re-encryption requires a live session.
     * This function marks FAILED â†’ SENDING so the UI shows them as pending rather than
     * stuck in a permanent error state. Full re-encryption retry is Phase 3 work.
     */
    fun retryFailedMessages() {
        val convId = conversationId ?: return
        if (!relayWebSocketClient.isConnected()) return

        viewModelScope.launch {
            val failedMessages = withContext(Dispatchers.IO) {
                messageRepository.getFailedMessages(convId)
            }
            if (failedMessages.isEmpty()) return@launch

            _events.emit(ChatEvent.ShowError("${failedMessages.size} message(s) failed â€” will resend when session is ready"))
            failedMessages.forEach { message ->
                withContext(Dispatchers.IO) {
                    messageRepository.updateMessageState(message.id, MessageState.SENDING)
                }
            }
            _events.emit(ChatEvent.RetrySucceeded)
        }
    }

    // â”€â”€ Cleanup â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    override fun onCleared() {
        super.onCleared()
        relayJob?.cancel()
        relayWebSocketClient.disconnect()
        ourEphemeralPair?.zeroizePrivate()
        ourEphemeralPair = null
        ourOfferPacket = null
        conversationId?.let { convId ->
            sendRatchets[convId]?.zeroizeAll()
            recvRatchets[convId]?.zeroizeAll()
        }
    }

    // ── Retry connection ───────────────────────────────────────────────────────

    /** Re-establish relay connection after a disconnection or URL change. */
    fun retryConnection() {
        val convId = conversationId ?: return
        val contact = activeContact ?: return
        val identity = myIdentity ?: return
        connectRelay(convId, identity, contact)
    }

    // â”€â”€ Helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private fun buildConversationId(a: String, b: String): String {
        val sorted = listOf(a, b).sorted()
        return "${sorted[0]}_${sorted[1]}"
    }

    private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
}

// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
// SETTINGS VIEWMODEL
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
data class SettingsUiState(
    val fingerprintHex: String = "",
    val screenshotBlockingEnabled: Boolean = true,
    val defaultExpirySeconds: Int = 0,
    val appVersion: String = com.hacksecure.messenger.domain.model.AppVersion.NAME,
    val showRegenerateConfirm: Boolean = false,
    val serverRelayUrl: String = "",
    val pingStatus: String = "",
    val isPinging: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val identityRepository: IdentityRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext private val appContext: android.content.Context,
    private val serverConfig: com.hacksecure.messenger.data.remote.ServerConfig
) : ViewModel() {

    private val prefs = appContext.getSharedPreferences("hacksecure_settings", android.content.Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            screenshotBlockingEnabled = prefs.getBoolean("screenshot_blocking", true),
            serverRelayUrl = serverConfig.relayBaseUrl
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val identity = identityRepository.getLocalIdentity() ?: return@launch
            _uiState.update {
                it.copy(fingerprintHex = identity.identityHex.chunked(8).joinToString(" "))
            }
        }
    }

    fun showRegenerateConfirm() = _uiState.update { it.copy(showRegenerateConfirm = true) }
    fun dismissRegenerateConfirm() = _uiState.update { it.copy(showRegenerateConfirm = false) }

    fun regenerateIdentity() {
        viewModelScope.launch(Dispatchers.IO) {
            identityRepository.deleteIdentity()
            val newIdentity = identityRepository.generateIdentity()
            _uiState.update {
                it.copy(
                    fingerprintHex = newIdentity.identityHex.chunked(8).joinToString(" "),
                    showRegenerateConfirm = false
                )
            }
        }
    }

    /**
     * Persists the screenshot blocking preference.
     * MainActivity reads this on onResume() and applies/clears FLAG_SECURE accordingly.
     */
    fun setScreenshotBlocking(enabled: Boolean) {
        prefs.edit().putBoolean("screenshot_blocking", enabled).apply()
        _uiState.update { it.copy(screenshotBlockingEnabled = enabled) }
    }

    fun setDefaultExpiry(seconds: Int) = _uiState.update { it.copy(defaultExpirySeconds = seconds) }

    fun updateServerUrl(url: String) {
        serverConfig.relayBaseUrl = url
        _uiState.update { it.copy(serverRelayUrl = url, pingStatus = "") }
    }

    fun pingServer() {
        val apiBase = serverConfig.apiBaseUrl
        viewModelScope.launch {
            _uiState.update { it.copy(isPinging = true, pingStatus = "") }
            try {
                val result = kotlinx.coroutines.withContext(Dispatchers.IO) {
                    val url = java.net.URL("$apiBase/health")
                    val conn = url.openConnection() as java.net.HttpURLConnection
                    conn.connectTimeout = 5000
                    conn.readTimeout = 5000
                    val start = System.currentTimeMillis()
                    val code = conn.responseCode
                    val ms = System.currentTimeMillis() - start
                    conn.disconnect()
                    if (code == 200) "Connected — ${ms}ms" else "Server returned HTTP $code"
                }
                _uiState.update { it.copy(pingStatus = result, isPinging = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        pingStatus = "Cannot reach server: ${e.message?.take(80)}",
                        isPinging = false
                    )
                }
            }
        }
    }
}
