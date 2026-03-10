// presentation/viewmodel/GhostViewModels.kt
// ViewModels for Ghost Mode — all state is ephemeral (RAM only)
package com.hacksecure.messenger.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hacksecure.messenger.data.remote.GhostConnectionManager
import com.hacksecure.messenger.data.remote.GhostEvent
import com.hacksecure.messenger.domain.crypto.IdentityKeyManager
import com.hacksecure.messenger.domain.model.*
import com.hacksecure.messenger.domain.repository.GhostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID
import javax.inject.Inject

// ══════════════════════════════════════════════════════════════════════════════
// GHOST LOBBY VIEWMODEL
// ══════════════════════════════════════════════════════════════════════════════

data class GhostLobbyUiState(
    val codename: String = "",
    val isRegistered: Boolean = false,
    val ghostToken: String = "",
    val registeredCodename: String = "",
    val searchQuery: String = "",
    val searchResults: List<String> = emptyList(),
    val isSearching: Boolean = false,
    val incomingRequests: List<GhostChatRequest> = emptyList(),
    val sentRequests: Set<String> = emptySet(),       // codenames we've sent requests to
    val acceptedChannel: GhostChannel? = null,
    val isRegistering: Boolean = false,
    val error: String? = null
)

sealed class GhostLobbyEvent {
    data class NavigateToChat(val channelId: String, val peerCodename: String, val anonymousId: String) : GhostLobbyEvent()
    data class ShowError(val message: String) : GhostLobbyEvent()
}

@HiltViewModel
class GhostLobbyViewModel @Inject constructor(
    private val ghostRepository: GhostRepository,
    private val ghostConnectionManager: GhostConnectionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(GhostLobbyUiState())
    val uiState: StateFlow<GhostLobbyUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<GhostLobbyEvent>()
    val events: SharedFlow<GhostLobbyEvent> = _events.asSharedFlow()

    private var pollJob: Job? = null

    fun updateCodename(name: String) {
        _uiState.update { it.copy(codename = name, error = null) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.length >= 2) searchUsers(query) else _uiState.update { it.copy(searchResults = emptyList()) }
    }

    fun register() {
        val codename = _uiState.value.codename.trim()
        if (codename.length < 2) {
            _uiState.update { it.copy(error = "Codename must be at least 2 characters") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isRegistering = true, error = null) }
            try {
                val identity = withContext(Dispatchers.IO) {
                    ghostRepository.register(codename)
                }
                _uiState.update {
                    it.copy(
                        isRegistered = true,
                        isRegistering = false,
                        ghostToken = identity.ghostToken,
                        registeredCodename = identity.codename
                    )
                }
                startPolling()
            } catch (e: Exception) {
                val msg = if (e.message?.contains("409") == true) "Codename already taken"
                         else "Registration failed: ${e.message}"
                _uiState.update { it.copy(isRegistering = false, error = msg) }
            }
        }
    }

    private fun searchUsers(query: String) {
        val token = _uiState.value.ghostToken
        if (token.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            try {
                val results = withContext(Dispatchers.IO) {
                    ghostRepository.searchOnlineUsers(query, token)
                }
                _uiState.update { it.copy(searchResults = results, isSearching = false) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isSearching = false) }
            }
        }
    }

    fun sendRequest(targetCodename: String) {
        val token = _uiState.value.ghostToken
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    ghostRepository.sendChatRequest(targetCodename, token)
                }
                _uiState.update { it.copy(sentRequests = it.sentRequests + targetCodename) }
            } catch (e: Exception) {
                _events.emit(GhostLobbyEvent.ShowError("Failed to send request"))
            }
        }
    }

    fun acceptRequest(requestId: String) {
        val token = _uiState.value.ghostToken
        viewModelScope.launch {
            try {
                val channel = withContext(Dispatchers.IO) {
                    ghostRepository.acceptRequest(requestId, token)
                }
                if (channel != null) {
                    _uiState.update { it.copy(acceptedChannel = channel) }
                    _events.emit(GhostLobbyEvent.NavigateToChat(
                        channelId = channel.channelId,
                        peerCodename = channel.peerCodename,
                        anonymousId = channel.anonymousId
                    ))
                }
            } catch (e: Exception) {
                _events.emit(GhostLobbyEvent.ShowError("Failed to accept request"))
            }
        }
    }

    fun rejectRequest(requestId: String) {
        val token = _uiState.value.ghostToken
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    ghostRepository.rejectRequest(requestId, token)
                }
                _uiState.update { state ->
                    state.copy(incomingRequests = state.incomingRequests.filter { it.requestId != requestId })
                }
            } catch (_: Exception) {}
        }
    }

    private fun startPolling() {
        pollJob?.cancel()
        pollJob = viewModelScope.launch {
            while (isActive) {
                val token = _uiState.value.ghostToken
                if (token.isBlank()) break
                try {
                    val (requests, channels) = withContext(Dispatchers.IO) {
                        ghostRepository.pollRequests(token)
                    }
                    _uiState.update { it.copy(incomingRequests = requests) }
                    // If a channel has been created (request we sent was accepted), navigate
                    if (channels.isNotEmpty()) {
                        val channel = channels.first()
                        _events.emit(GhostLobbyEvent.NavigateToChat(
                            channelId = channel.channelId,
                            peerCodename = channel.peerCodename,
                            anonymousId = channel.anonymousId
                        ))
                        break
                    }
                } catch (_: Exception) {}
                delay(3000)
            }
        }
    }

    fun leaveGhostMode() {
        val token = _uiState.value.ghostToken
        pollJob?.cancel()
        viewModelScope.launch(Dispatchers.IO) {
            try { ghostRepository.leave(token) } catch (_: Exception) {}
        }
        _uiState.update { GhostLobbyUiState() }
    }

    override fun onCleared() {
        super.onCleared()
        pollJob?.cancel()
        val token = _uiState.value.ghostToken
        if (token.isNotBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                try { ghostRepository.leave(token) } catch (_: Exception) {}
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// GHOST CHAT VIEWMODEL
// ══════════════════════════════════════════════════════════════════════════════

data class GhostChatUiState(
    val messages: List<GhostMessage> = emptyList(),
    val peerAnonymousId: String = "",
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val sessionEstablished: Boolean = false,
    val inputText: String = "",
    val isChannelDestroyed: Boolean = false
)

sealed class GhostChatEvent {
    object ChannelDestroyed : GhostChatEvent()
    data class ShowError(val message: String) : GhostChatEvent()
}

@HiltViewModel
class GhostChatViewModel @Inject constructor(
    private val ghostConnectionManager: GhostConnectionManager,
    private val identityKeyManager: IdentityKeyManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(GhostChatUiState())
    val uiState: StateFlow<GhostChatUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<GhostChatEvent>()
    val events: SharedFlow<GhostChatEvent> = _events.asSharedFlow()

    private val seenMessageIds = mutableSetOf<String>()

    fun initChannel(channelId: String, peerCodename: String, anonymousId: String) {
        _uiState.update { it.copy(peerAnonymousId = anonymousId) }

        // Connect to ghost channel
        ghostConnectionManager.connect(channelId)

        // Observe connection state
        viewModelScope.launch {
            ghostConnectionManager.connectionState.collect { state ->
                _uiState.update { it.copy(connectionState = state) }
            }
        }

        // Observe ghost events
        viewModelScope.launch {
            ghostConnectionManager.events.collect { event ->
                when (event) {
                    is GhostEvent.SessionReady -> {
                        _uiState.update { it.copy(sessionEstablished = true) }
                    }
                    is GhostEvent.MessageReceived -> {
                        receiveMessage(event.messageId, event.packetBytes)
                    }
                    is GhostEvent.ChannelDestroyed -> {
                        _uiState.update { it.copy(isChannelDestroyed = true, messages = emptyList()) }
                        _events.emit(GhostChatEvent.ChannelDestroyed)
                    }
                    is GhostEvent.Error -> {
                        _events.emit(GhostChatEvent.ShowError(event.message))
                    }
                }
            }
        }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank()) return

        val processor = ghostConnectionManager.getMessageProcessor() ?: run {
            viewModelScope.launch { _events.emit(GhostChatEvent.ShowError("Secure session not yet established")) }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(inputText = "") }
                val messageId = UUID.randomUUID().toString()
                val now = System.currentTimeMillis()

                // Encrypt
                val (wirePacket, counter) = withContext(Dispatchers.Default) {
                    processor.send(plaintext = text.toByteArray(Charsets.UTF_8), expirySeconds = 0)
                }

                // Add to local in-memory list (NOT saved to DB)
                val ghostMsg = GhostMessage(
                    id = messageId,
                    content = text,
                    isOutgoing = true,
                    timestampMs = now
                )
                _uiState.update { it.copy(messages = it.messages + ghostMsg) }

                // Send over WebSocket
                val sent = withContext(Dispatchers.IO) {
                    ghostConnectionManager.sendPacket(messageId, wirePacket)
                }
                if (!sent) {
                    _events.emit(GhostChatEvent.ShowError("Failed to send — peer may have disconnected"))
                }
            } catch (e: Exception) {
                _events.emit(GhostChatEvent.ShowError("Encryption failed"))
            }
        }
    }

    private suspend fun receiveMessage(messageId: String, packetBytes: ByteArray) {
        if (!seenMessageIds.add(messageId)) return
        val processor = ghostConnectionManager.getMessageProcessor() ?: return

        try {
            val (plaintextBytes, header) = withContext(Dispatchers.Default) {
                processor.receive(packetBytes)
            }
            val text = plaintextBytes.toString(Charsets.UTF_8)
            val ghostMsg = GhostMessage(
                id = UUID.randomUUID().toString(),
                content = text,
                isOutgoing = false,
                timestampMs = header.timestampMs
            )
            _uiState.update { it.copy(messages = it.messages + ghostMsg) }
        } catch (_: Exception) {
            // Silently discard messages that fail crypto
        }
    }

    fun disconnect() {
        ghostConnectionManager.disconnect()
        _uiState.update { it.copy(messages = emptyList(), isChannelDestroyed = true) }
    }

    override fun onCleared() {
        super.onCleared()
        ghostConnectionManager.disconnect()
        // All messages are gone — they only lived in _uiState
    }
}
