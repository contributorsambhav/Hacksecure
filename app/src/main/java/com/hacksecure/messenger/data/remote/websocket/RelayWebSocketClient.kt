// data/remote/websocket/RelayWebSocketClient.kt
// Networking Layer — WebSocket relay client
package com.hacksecure.messenger.data.remote.websocket

import com.hacksecure.messenger.data.remote.ServerConfig
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.*
import okio.ByteString
import okio.ByteString.Companion.toByteString
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.min
import kotlin.math.pow

data class RawRelayMessage(
    val conversationId: String,
    val messageId: String,
    val packetBytes: ByteArray
)

sealed class RelayEvent {
    data class MessageReceived(val message: RawRelayMessage) : RelayEvent()
    object Connected : RelayEvent()
    object Disconnected : RelayEvent()
    data class Error(val throwable: Throwable) : RelayEvent()
}

/**
 * WebSocket client for the blind relay server.
 *
 * The relay only routes encrypted binary packets — it never inspects content.
 * Connection uses binary frames exclusively.
 *
 * Reconnect strategy: exponential backoff starting at 1s, max 30s.
 */
class RelayWebSocketClient(
    private val okHttpClient: OkHttpClient,
    private val serverConfig: ServerConfig
) {
    companion object {
        private const val TAG = "RelayWebSocket"
        private const val MAX_BACKOFF_MS = 30_000L
        private const val INITIAL_BACKOFF_MS = 1_000L
    }

    private val _events = MutableSharedFlow<RelayEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<RelayEvent> = _events

    @Volatile private var webSocket: WebSocket? = null
    @Volatile private var isConnected = false
    @Volatile private var shouldReconnect = true
    private var reconnectAttempts = 0
    private var currentConversationId: String? = null
    private var currentAuthToken: String? = null

    fun connect(conversationId: String, authToken: String) {
        currentConversationId = conversationId
        currentAuthToken = authToken
        shouldReconnect = true
        connectInternal(conversationId, authToken)
    }

    fun disconnect() {
        shouldReconnect = false
        webSocket?.close(1000, "User disconnect")
        webSocket = null
        isConnected = false
    }

    /**
     * Connect to a ghost channel. Ghost connections do NOT auto-reconnect —
     * when the channel is destroyed, the WebSocket is closed permanently.
     */
    fun connectGhost(ghostChannelId: String) {
        currentConversationId = ghostChannelId
        currentAuthToken = ""
        shouldReconnect = false  // Ghost channels never reconnect
        connectInternalGhost(ghostChannelId)
    }

    fun isConnected(): Boolean = isConnected

    /**
     * Sends raw wire packet bytes over the WebSocket.
     * The relay envelope wraps the packet with routing metadata.
     *
     * Envelope format (binary):
     *   [2 bytes] conversationId length
     *   [n bytes] conversationId UTF-8
     *   [2 bytes] messageId length
     *   [m bytes] messageId UTF-8
     *   [remaining] packet bytes
     */
    suspend fun send(conversationId: String, messageId: String, packet: ByteArray): Boolean {
        val ws = webSocket ?: return false
        if (!isConnected) return false

        val convBytes = conversationId.toByteArray(Charsets.UTF_8)
        val msgIdBytes = messageId.toByteArray(Charsets.UTF_8)

        val totalSize = 2 + convBytes.size + 2 + msgIdBytes.size + packet.size
        val envelope = java.nio.ByteBuffer.allocate(totalSize).apply {
            putShort(convBytes.size.toShort())
            put(convBytes)
            putShort(msgIdBytes.size.toShort())
            put(msgIdBytes)
            put(packet)
        }.array()

        return ws.send(envelope.toByteString())
    }

    private fun connectInternal(conversationId: String, authToken: String) {
        val url = "${serverConfig.relayBaseUrl}/ws?conv=$conversationId"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        // Capture the stale reference BEFORE overwriting — closed after new WS is created.
        val staleWs = webSocket

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            // Guard: ignore callbacks from WebSockets that are no longer the active one.
            // This prevents stale close/failure/message events from a previous connection
            // triggering spurious reconnects or duplicate handshakes.
            private fun WebSocket.isActive() = this === this@RelayWebSocketClient.webSocket

            override fun onOpen(webSocket: WebSocket, response: Response) {
                if (!webSocket.isActive()) return
                Log.d(TAG, "WebSocket connected")
                isConnected = true
                reconnectAttempts = 0
                _events.tryEmit(RelayEvent.Connected)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                if (!webSocket.isActive()) return
                try {
                    val data = bytes.toByteArray()
                    // Guard: minimum viable envelope is 2+1+2+1 = 6 bytes
                    if (data.size < 6) {
                        Log.w(TAG, "Relay message too short (${data.size} bytes), discarding")
                        return
                    }
                    val buf = java.nio.ByteBuffer.wrap(data)
                    val convLen = buf.short.toInt()
                    if (convLen <= 0 || convLen > buf.remaining()) {
                        Log.w(TAG, "Invalid convLen=$convLen, discarding")
                        return
                    }
                    val convId = ByteArray(convLen).also { buf.get(it) }.toString(Charsets.UTF_8)
                    val msgIdLen = buf.short.toInt()
                    if (msgIdLen <= 0 || msgIdLen > buf.remaining()) {
                        Log.w(TAG, "Invalid msgIdLen=$msgIdLen, discarding")
                        return
                    }
                    val msgId = ByteArray(msgIdLen).also { buf.get(it) }.toString(Charsets.UTF_8)
                    val packetBytes = ByteArray(buf.remaining()).also { buf.get(it) }

                    _events.tryEmit(RelayEvent.MessageReceived(
                        RawRelayMessage(convId, msgId, packetBytes)
                    ))
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse incoming relay message", e)
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                if (!webSocket.isActive()) return
                // We use binary frames only — ignore text frames
                Log.w(TAG, "Unexpected text frame from relay, ignoring")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                if (!webSocket.isActive()) return
                webSocket.close(1000, null)
                isConnected = false
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                if (!webSocket.isActive()) return
                Log.d(TAG, "WebSocket closed: $code $reason")
                isConnected = false
                _events.tryEmit(RelayEvent.Disconnected)
                if (shouldReconnect) scheduleReconnect(conversationId, authToken)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                if (!webSocket.isActive()) return
                Log.e(TAG, "WebSocket failure", t)
                isConnected = false
                _events.tryEmit(RelayEvent.Error(t))
                if (shouldReconnect) scheduleReconnect(conversationId, authToken)
            }
        })

        // Close the stale WebSocket AFTER the new one is assigned, so `isActive()` checks
        // from the stale WS's callbacks correctly detect it as inactive and bail out early.
        staleWs?.close(1000, "Reconnecting")
    }

    /**
     * Internal connection method for ghost channels — connects to /ws?ghost=<channelId>.
     * Ghost channels do NOT auto-reconnect.
     */
    private fun connectInternalGhost(ghostChannelId: String) {
        val url = "${serverConfig.relayBaseUrl}/ws?ghost=$ghostChannelId"
        val request = Request.Builder().url(url).build()

        val staleWs = webSocket

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            private fun WebSocket.isActive() = this === this@RelayWebSocketClient.webSocket

            override fun onOpen(webSocket: WebSocket, response: Response) {
                if (!webSocket.isActive()) return
                Log.d(TAG, "Ghost WebSocket connected to channel ${ghostChannelId.take(8)}...")
                isConnected = true
                reconnectAttempts = 0
                _events.tryEmit(RelayEvent.Connected)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                if (!webSocket.isActive()) return
                try {
                    val data = bytes.toByteArray()
                    if (data.size < 6) {
                        Log.w(TAG, "Ghost relay message too short (${data.size} bytes), discarding")
                        return
                    }
                    val buf = java.nio.ByteBuffer.wrap(data)
                    val convLen = buf.short.toInt()
                    if (convLen <= 0 || convLen > buf.remaining()) return
                    val convId = ByteArray(convLen).also { buf.get(it) }.toString(Charsets.UTF_8)
                    val msgIdLen = buf.short.toInt()
                    if (msgIdLen <= 0 || msgIdLen > buf.remaining()) return
                    val msgId = ByteArray(msgIdLen).also { buf.get(it) }.toString(Charsets.UTF_8)
                    val packetBytes = ByteArray(buf.remaining()).also { buf.get(it) }

                    _events.tryEmit(RelayEvent.MessageReceived(
                        RawRelayMessage(convId, msgId, packetBytes)
                    ))
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse ghost relay message", e)
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                if (!webSocket.isActive()) return
                Log.w(TAG, "Unexpected text frame on ghost channel, ignoring")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                if (!webSocket.isActive()) return
                webSocket.close(1000, null)
                isConnected = false
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                if (!webSocket.isActive()) return
                Log.d(TAG, "Ghost WebSocket closed: $code $reason")
                isConnected = false
                _events.tryEmit(RelayEvent.Disconnected)
                // NO reconnect for ghost channels — channel is permanently destroyed
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                if (!webSocket.isActive()) return
                Log.e(TAG, "Ghost WebSocket failure", t)
                isConnected = false
                _events.tryEmit(RelayEvent.Error(t))
                // NO reconnect for ghost channels
            }
        })

        staleWs?.close(1000, "Reconnecting ghost")
    }

    private fun scheduleReconnect(conversationId: String, authToken: String) {
        reconnectAttempts++
        val backoffMs = min(
            INITIAL_BACKOFF_MS * (2.0.pow(reconnectAttempts - 1)).toLong(),
            MAX_BACKOFF_MS
        )
        Log.d(TAG, "Reconnecting in ${backoffMs}ms (attempt $reconnectAttempts)")
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (shouldReconnect) connectInternal(conversationId, authToken)
        }, backoffMs)
    }
}
