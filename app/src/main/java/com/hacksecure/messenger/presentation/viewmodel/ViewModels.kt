// presentation/viewmodel/ViewModels.kt
package com.hacksecure.messenger.presentation.viewmodel

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hacksecure.messenger.domain.crypto.*
import com.hacksecure.messenger.domain.model.*
import com.hacksecure.messenger.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

// ══════════════════════════════════════════════════════════════════════════════
// SPLASH / ONBOARDING VIEWMODEL
// ══════════════════════════════════════════════════════════════════════════════
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

// ══════════════════════════════════════════════════════════════════════════════
// HOME VIEWMODEL
// ══════════════════════════════════════════════════════════════════════════════
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
    private val contactRepository: ContactRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            contactRepository.getContacts().collect { contacts ->
                val items = contacts.map { contact ->
                    val convId = buildConversationId(contact.identityHex, contact.identityHex)
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

// ══════════════════════════════════════════════════════════════════════════════
// QR DISPLAY VIEWMODEL
// ══════════════════════════════════════════════════════════════════════════════
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

// ══════════════════════════════════════════════════════════════════════════════
// QR SCAN VIEWMODEL
// ══════════════════════════════════════════════════════════════════════════════
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

// ══════════════════════════════════════════════════════════════════════════════
// CONTACT CONFIRMATION VIEWMODEL
// ══════════════════════════════════════════════════════════════════════════════
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
                // Key changed — warn user
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

// ══════════════════════════════════════════════════════════════════════════════
// CHAT VIEWMODEL
// ══════════════════════════════════════════════════════════════════════════════
data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val contact: Contact? = null,
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val sessionEstablished: Boolean = false,
    val defaultExpirySeconds: Int = 0,
    val isLoading: Boolean = true,
    val inputText: String = "",
    val selectedExpirySeconds: Int = 0
)

sealed class ChatEvent {
    data class ShowError(val message: String) : ChatEvent()
    data class MessageRejected(val reason: String) : ChatEvent()
    object SessionSecured : ChatEvent()
    object Snackbar : ChatEvent()
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val contactRepository: ContactRepository,
    private val identityRepository: IdentityRepository,
    private val identityKeyManager: IdentityKeyManager,
    private val sessionKeyManager: SessionKeyManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ChatEvent>()
    val events: SharedFlow<ChatEvent> = _events.asSharedFlow()

    // Active session state (per conversation)
    @Volatile private var messageProcessor: MessageProcessor? = null
    @Volatile private var conversationId: String? = null

    // In-memory message send/receive processors keyed by conversationId
    private val sendRatchets = mutableMapOf<String, HashRatchet>()
    private val recvRatchets = mutableMapOf<String, HashRatchet>()

    fun initConversation(contactId: String) {
        viewModelScope.launch {
            val contact = withContext(Dispatchers.IO) {
                contactRepository.getContact(contactId)
            } ?: return@launch

            val myIdentity = withContext(Dispatchers.IO) {
                identityRepository.getLocalIdentity()
            } ?: return@launch

            val convId = buildConversationId(
                myIdentity.identityHex,
                contact.identityHex
            )
            conversationId = convId
            _uiState.update { it.copy(contact = contact, isLoading = false) }

            // Collect messages in this same coroutine after conversationId is set.
            // Room Flow suspends here indefinitely (correct — it observes DB changes).
            // The init sequence above has already completed before we reach this point.
            messageRepository.getMessages(convId).collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }

    /**
     * Call this when the DH handshake completes and session key is derived.
     * Creates the send/receive ratchets from the session root key.
     */
    fun onSessionEstablished(sessionKey: ByteArray, contact: Contact, myIdentity: LocalIdentity) {
        viewModelScope.launch(Dispatchers.Default) {
            val convId = conversationId ?: return@launch

            // Create ratchets — advance once from root (K₀ → K₁)
            val sendRatchet = HashRatchet(sessionKey.copyOf())
            val recvRatchet = HashRatchet(sessionKey.copyOf())
            sessionKey.fill(0)  // Zeroize root key after creating ratchets

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

    fun updateInputText(text: String) = _uiState.update { it.copy(inputText = text) }

    fun setExpirySeconds(seconds: Int) = _uiState.update { it.copy(selectedExpirySeconds = seconds) }

    /**
     * Sends a text message through the full cryptographic pipeline.
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
                // Clear input immediately (optimistic UI) — on Main dispatcher
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
                    // TODO: Send wirePacketBytes over WebSocket relay
                } else {
                    // Demo/offline mode — no encryption pipeline
                    counter = withContext(Dispatchers.IO) {
                        val myPubKey = identityKeyManager.getPublicKeyBytes()
                        val myIdHex = IdentityHash.compute(myPubKey).joinToString("") { "%02x".format(it) }
                        messageRepository.getMaxCounter(convId, myIdHex) + 1
                    }
                    wirePacketBytes = null
                }

                val myPubKey = withContext(Dispatchers.Default) { identityKeyManager.getPublicKeyBytes() }
                val myIdHex = IdentityHash.compute(myPubKey).joinToString("") { "%02x".format(it) }

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
                    state = if (wirePacketBytes != null) MessageState.SENT else MessageState.SENDING
                )

                withContext(Dispatchers.IO) {
                    messageRepository.saveMessage(message, plaintextBytes)
                }

            } catch (e: CryptoError) {
                _events.emit(ChatEvent.ShowError("Encryption failed"))
            } catch (e: Exception) {
                _events.emit(ChatEvent.ShowError("Failed to send message"))
            }
        }
    }

    /**
     * Processes an incoming raw wire packet through the full receive pipeline.
     * Called from WebSocket/WebRTC data channel.
     */
    fun receiveRawPacket(packetBytes: ByteArray, contact: Contact) {
        val processor = messageProcessor ?: return
        val convId = conversationId ?: return

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val (plaintextBytes, header) = processor.receive(packetBytes)
                val text = plaintextBytes.toString(Charsets.UTF_8)
                val now = System.currentTimeMillis()
                val messageId = UUID.randomUUID().toString()

                val message = Message(
                    id = messageId,
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

                // Room requires IO dispatcher — switch context for DB write
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
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up ratchets
        conversationId?.let { convId ->
            sendRatchets[convId]?.zeroizeAll()
            recvRatchets[convId]?.zeroizeAll()
        }
    }

    private fun buildConversationId(a: String, b: String): String {
        val sorted = listOf(a, b).sorted()
        return "${sorted[0]}_${sorted[1]}"
    }

    private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
}

// ══════════════════════════════════════════════════════════════════════════════
// SETTINGS VIEWMODEL
// ══════════════════════════════════════════════════════════════════════════════
data class SettingsUiState(
    val fingerprintHex: String = "",
    val screenshotBlockingEnabled: Boolean = true,
    val defaultExpirySeconds: Int = 0,
    val appVersion: String = com.hacksecure.messenger.domain.model.AppVersion.NAME,
    val showRegenerateConfirm: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val identityRepository: IdentityRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext private val appContext: android.content.Context
) : ViewModel() {

    private val prefs = appContext.getSharedPreferences("hacksecure_settings", android.content.Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            screenshotBlockingEnabled = prefs.getBoolean("screenshot_blocking", true)
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
}
