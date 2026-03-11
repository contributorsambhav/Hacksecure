// data/remote/GhostConnectionManager.kt
// Ephemeral WebSocket manager for Ghost Mode — NO database writes, NO message persistence.
package com.hacksecure.messenger.data.remote

import com.hacksecure.messenger.data.remote.websocket.RelayEvent
import com.hacksecure.messenger.data.remote.websocket.RelayWebSocketClient
import com.hacksecure.messenger.domain.crypto.*
import com.hacksecure.messenger.domain.model.ConnectionState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

sealed class GhostEvent {
    data class MessageReceived(val messageId: String, val packetBytes: ByteArray) : GhostEvent()
    object SessionReady : GhostEvent()
    object ChannelDestroyed : GhostEvent()
    data class Error(val message: String) : GhostEvent()
}

/**
 * Lightweight, ephemeral WebSocket manager for Ghost Mode channels.
 *
 * Key differences from [BackgroundConnectionManager]:
 * - Connects to `ws://<host>/ws?ghost=<channelId>` (ghost query param)
 * - NEVER writes messages to DB — data lives only in ViewModel memory
 * - On disconnect → emits [GhostEvent.ChannelDestroyed] (peer left or error)
 * - Only supports ONE active ghost channel at a time
 * - Uses the same DH handshake protocol as regular chat
 */
@Singleton
class GhostConnectionManager @Inject constructor(
    private val handshakeManager: HandshakeManager,
    private val identityKeyManager: IdentityKeyManager,
    private val serverConfig: ServerConfig,
    private val okHttpClient: OkHttpClient
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _events = MutableSharedFlow<GhostEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<GhostEvent> = _events

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    @Volatile private var wsClient: RelayWebSocketClient? = null
    @Volatile private var messageProcessor: MessageProcessor? = null
    @Volatile private var ephemeralPair: SessionKeyManager.EphemeralKeyPair? = null
    @Volatile private var offerPacket: ByteArray? = null
    @Volatile private var activeChannelId: String? = null
    private var collectionJob: Job? = null

    /**
     * Connect to a ghost channel and begin the DH handshake.
     */
    fun connect(channelId: String) {
        disconnect() // clean up any previous session

        activeChannelId = channelId
        _connectionState.value = ConnectionState.CONNECTING

        val client = RelayWebSocketClient(okHttpClient, serverConfig)
        wsClient = client

        // Connect using ghost channel parameter
        // The RelayWebSocketClient.connect expects conversationId and authToken
        // We pass channelId as the conversationId but override the URL query param
        client.connectGhost(channelId)

        collectionJob = scope.launch {
            client.events.collect { event ->
                when (event) {
                    is RelayEvent.Connected -> {
                        _connectionState.value = ConnectionState.CONNECTED_RELAY

                        // Start DH handshake — create ephemeral keypair + offer
                        val (offer, pair) = withContext(Dispatchers.Default) {
                            handshakeManager.createOffer()
                        }
                        ephemeralPair = pair
                        offerPacket = offer

                        // Send handshake offer through the ghost channel
                        client.send(channelId, "hs_ghost_${UUID.randomUUID()}", offer)
                    }

                    is RelayEvent.MessageReceived -> {
                        val packetBytes = event.message.packetBytes
                        if (handshakeManager.isHandshakeOffer(packetBytes)) {
                            handlePeerOffer(packetBytes)
                        } else if (messageProcessor != null) {
                            // Forward encrypted message to ViewModel for decryption
                            _events.tryEmit(GhostEvent.MessageReceived(event.message.messageId, packetBytes))
                        }
                    }

                    is RelayEvent.Disconnected -> {
                        _connectionState.value = ConnectionState.DISCONNECTED
                        _events.tryEmit(GhostEvent.ChannelDestroyed)
                        cleanupSession()
                    }

                    is RelayEvent.Error -> {
                        _connectionState.value = ConnectionState.ERROR
                        _events.tryEmit(GhostEvent.ChannelDestroyed)
                        cleanupSession()
                    }
                }
            }
        }
    }

    /**
     * Handles receiving the peer's handshake offer.
     * Derives the shared session key using the same protocol as regular chat.
     */
    private suspend fun handlePeerOffer(peerOfferBytes: ByteArray) {
        if (messageProcessor != null) return // session already established

        val pair = ephemeralPair ?: return
        val client = wsClient ?: return
        val channelId = activeChannelId ?: return

        // Send our offer to the peer (they may not have received it yet)
        offerPacket?.let { myOffer ->
            client.send(channelId, "hs_echo_${UUID.randomUUID()}", myOffer)
        }

        try {
            // Extract peer's ephemeral public key from the offer (bytes 1..32)
            // The offer format is: [1 magic][32 eph_pub][64 signature]
            val peerEphPubKey = peerOfferBytes.copyOfRange(1, 33)
            val myEphPubKey = pair.publicKeyBytes

            // For ghost mode, we use both peers' ephemeral keys as "identity" for
            // key derivation. This prevents exposing permanent identities and
            // ensures both sides compute identical session keys.
            val myIdentityHash = IdentityHash.compute(myEphPubKey)
            val peerIdentityHash = IdentityHash.compute(peerEphPubKey)

            val sessionKey = withContext(Dispatchers.Default) {
                handshakeManager.deriveSessionKey(
                    offerBytes = peerOfferBytes,
                    peerIdentityPublicKey = peerEphPubKey,  // Ignored in ghost mode
                    ourEphemeralPair = pair,
                    myIdentityHash = myIdentityHash,
                    peerIdentityHash = peerIdentityHash,
                    skipSignatureVerification = true
                )
            }
            ephemeralPair = null  // private key has been zeroized in computeSharedSecret

            // Create hash ratchets from the session key
            val sendRatchet = HashRatchet(sessionKey.copyOf())
            val recvRatchet = HashRatchet(sessionKey.copyOf())
            sessionKey.fill(0)  // zero the original

            val processor = MessageProcessor(
                sendRatchet = sendRatchet,
                recvRatchet = recvRatchet,
                identityKeyManager = identityKeyManager,
                peerPublicKeyBytes = peerEphPubKey,  // Ignored in ghost mode
                myIdentityHash = myIdentityHash,
                skipSignatureVerification = true
            )
            messageProcessor = processor
            _events.tryEmit(GhostEvent.SessionReady)
        } catch (e: Exception) {
            // Handshake failed — will be retried on next offer
            _events.tryEmit(GhostEvent.Error("Key exchange failed: ${e.message}"))
        }
    }

    /** Send an encrypted packet through the ghost channel. */
    suspend fun sendPacket(messageId: String, packetBytes: ByteArray): Boolean {
        val client = wsClient ?: return false
        val channelId = activeChannelId ?: return false
        return client.send(channelId, messageId, packetBytes)
    }

    /** Get the established MessageProcessor for encrypt/decrypt. */
    fun getMessageProcessor(): MessageProcessor? = messageProcessor

    /** Disconnect and destroy all crypto state. */
    fun disconnect() {
        wsClient?.disconnect()
        cleanupSession()
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    private fun cleanupSession() {
        collectionJob?.cancel()
        collectionJob = null
        ephemeralPair?.zeroizePrivate()
        ephemeralPair = null
        offerPacket = null
        messageProcessor = null
        wsClient = null
        activeChannelId = null
    }
}
