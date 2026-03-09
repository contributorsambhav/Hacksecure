package com.hacksecure.messenger.data.remote;

import com.hacksecure.messenger.data.remote.api.PresenceRequest;
import com.hacksecure.messenger.data.remote.api.RelayApi;
import com.hacksecure.messenger.data.remote.api.TicketRequest;
import com.hacksecure.messenger.data.remote.websocket.RelayEvent;
import com.hacksecure.messenger.data.remote.websocket.RelayWebSocketClient;
import com.hacksecure.messenger.domain.crypto.*;
import com.hacksecure.messenger.domain.model.*;
import com.hacksecure.messenger.domain.repository.ContactRepository;
import com.hacksecure.messenger.domain.repository.IdentityRepository;
import com.hacksecure.messenger.domain.repository.MessageRepository;
import kotlinx.coroutines.*;
import kotlinx.coroutines.flow.StateFlow;
import okhttp3.OkHttpClient;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;

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
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0094\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0011\b\u0007\u0018\u00002\u00020\u0001:\u0001JBG\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\u000f\u0012\u0006\u0010\u0010\u001a\u00020\u0011\u00a2\u0006\u0002\u0010\u0012J\u0018\u0010(\u001a\u00020\u00152\u0006\u0010)\u001a\u00020\u00152\u0006\u0010*\u001a\u00020\u0015H\u0002J\u001e\u0010+\u001a\u00020\u001d2\u0006\u0010,\u001a\u00020\"2\u0006\u0010-\u001a\u00020\u001aH\u0082@\u00a2\u0006\u0002\u0010.J\u0016\u0010/\u001a\u00020\u001d2\u0006\u0010,\u001a\u00020\"H\u0082@\u00a2\u0006\u0002\u00100J\u0016\u00101\u001a\u00020\u001d2\u0006\u00102\u001a\u0002032\u0006\u00104\u001a\u000205J\u0016\u00106\u001a\u00020\u001d2\u0006\u00102\u001a\u0002032\u0006\u00104\u001a\u000205J\u0010\u00107\u001a\u0004\u0018\u00010&2\u0006\u00108\u001a\u00020\u0015J\u000e\u00109\u001a\u00020:2\u0006\u00108\u001a\u00020\u0015J&\u0010;\u001a\u00020\u001d2\u0006\u0010,\u001a\u00020\"2\u0006\u0010<\u001a\u00020\u00152\u0006\u0010=\u001a\u00020\u001aH\u0082@\u00a2\u0006\u0002\u0010>J\u00a3\u0001\u0010?\u001a\u00020\u001d2\u0006\u00108\u001a\u00020\u00152F\u0010@\u001aB\b\u0001\u0012\u0013\u0012\u00110\u0015\u00a2\u0006\f\b\u0017\u0012\b\b\u0018\u0012\u0004\b\b(\u0019\u0012\u0013\u0012\u00110\u001a\u00a2\u0006\f\b\u0017\u0012\b\b\u0018\u0012\u0004\b\b(\u001b\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0\u001c\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u00162\"\u0010A\u001a\u001e\b\u0001\u0012\u0004\u0012\u00020&\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0\u001c\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u001f2\"\u0010B\u001a\u001e\b\u0001\u0012\u0004\u0012\u00020 \u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0\u001c\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u001f\u00a2\u0006\u0002\u0010CJ&\u0010D\u001a\u00020:2\u0006\u00108\u001a\u00020\u00152\u0006\u0010<\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u001aH\u0086@\u00a2\u0006\u0002\u0010EJ\u0006\u0010F\u001a\u00020\u001dJ\u0006\u0010G\u001a\u00020\u001dJ\u000e\u0010H\u001a\u00020\u001d2\u0006\u00108\u001a\u00020\u0015J\f\u0010I\u001a\u00020\u0015*\u00020\u001aH\u0002RZ\u0010\u0013\u001aN\u0012\u0004\u0012\u00020\u0015\u0012D\u0012B\b\u0001\u0012\u0013\u0012\u00110\u0015\u00a2\u0006\f\b\u0017\u0012\b\b\u0018\u0012\u0004\b\b(\u0019\u0012\u0013\u0012\u00110\u001a\u00a2\u0006\f\b\u0017\u0012\b\b\u0018\u0012\u0004\b\b(\u001b\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0\u001c\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u00160\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000R6\u0010\u001e\u001a*\u0012\u0004\u0012\u00020\u0015\u0012 \u0012\u001e\b\u0001\u0012\u0004\u0012\u00020 \u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0\u001c\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u001f0\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010!\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\"0\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020$X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000RE\u0010%\u001a9\u0012\u0004\u0012\u00020\u0015\u0012/\u0012-\b\u0001\u0012\u0013\u0012\u00110&\u00a2\u0006\f\b\u0017\u0012\b\b\u0018\u0012\u0004\b\b(\'\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0\u001c\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u001f0\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006K"}, d2 = {"Lcom/hacksecure/messenger/data/remote/BackgroundConnectionManager;", "", "contactRepository", "Lcom/hacksecure/messenger/domain/repository/ContactRepository;", "identityRepository", "Lcom/hacksecure/messenger/domain/repository/IdentityRepository;", "messageRepository", "Lcom/hacksecure/messenger/domain/repository/MessageRepository;", "handshakeManager", "Lcom/hacksecure/messenger/domain/crypto/HandshakeManager;", "identityKeyManager", "Lcom/hacksecure/messenger/domain/crypto/IdentityKeyManager;", "relayApi", "Lcom/hacksecure/messenger/data/remote/api/RelayApi;", "serverConfig", "Lcom/hacksecure/messenger/data/remote/ServerConfig;", "okHttpClient", "Lokhttp3/OkHttpClient;", "(Lcom/hacksecure/messenger/domain/repository/ContactRepository;Lcom/hacksecure/messenger/domain/repository/IdentityRepository;Lcom/hacksecure/messenger/domain/repository/MessageRepository;Lcom/hacksecure/messenger/domain/crypto/HandshakeManager;Lcom/hacksecure/messenger/domain/crypto/IdentityKeyManager;Lcom/hacksecure/messenger/data/remote/api/RelayApi;Lcom/hacksecure/messenger/data/remote/ServerConfig;Lokhttp3/OkHttpClient;)V", "chatPacketHandlers", "Ljava/util/concurrent/ConcurrentHashMap;", "", "Lkotlin/Function3;", "Lkotlin/ParameterName;", "name", "msgId", "", "packet", "Lkotlin/coroutines/Continuation;", "", "connStateCallbacks", "Lkotlin/Function2;", "Lcom/hacksecure/messenger/domain/model/ConnectionState;", "convStates", "Lcom/hacksecure/messenger/data/remote/BackgroundConnectionManager$ConvState;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "sessionReadyCallbacks", "Lcom/hacksecure/messenger/domain/crypto/MessageProcessor;", "processor", "buildConvId", "a", "b", "completeHandshake", "state", "offerBytes", "(Lcom/hacksecure/messenger/data/remote/BackgroundConnectionManager$ConvState;[BLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "connectConversation", "(Lcom/hacksecure/messenger/data/remote/BackgroundConnectionManager$ConvState;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "ensureConnected", "identity", "Lcom/hacksecure/messenger/domain/model/LocalIdentity;", "contact", "Lcom/hacksecure/messenger/domain/model/Contact;", "forceReconnect", "getMessageProcessor", "convId", "isConnected", "", "receiveInBackground", "messageId", "packetBytes", "(Lcom/hacksecure/messenger/data/remote/BackgroundConnectionManager$ConvState;Ljava/lang/String;[BLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "registerChatHandler", "packetHandler", "onSessionReady", "onConnectionStateChange", "(Ljava/lang/String;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function2;Lkotlin/jvm/functions/Function2;)V", "sendPacket", "(Ljava/lang/String;Ljava/lang/String;[BLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "startAllConnections", "stopAll", "unregisterChatHandler", "toHexString", "ConvState", "app_debug"})
public final class BackgroundConnectionManager {
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.repository.ContactRepository contactRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.repository.IdentityRepository identityRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.repository.MessageRepository messageRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.crypto.HandshakeManager handshakeManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.crypto.IdentityKeyManager identityKeyManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.data.remote.api.RelayApi relayApi = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.data.remote.ServerConfig serverConfig = null;
    @org.jetbrains.annotations.NotNull()
    private final okhttp3.OkHttpClient okHttpClient = null;
    
    /**
     * Long-lived supervisor scope that outlives any single ViewModel.
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.concurrent.ConcurrentHashMap<java.lang.String, com.hacksecure.messenger.data.remote.BackgroundConnectionManager.ConvState> convStates = null;
    
    /**
     * Raw MESSAGE packet handler set by the active ChatViewModel.
     */
    @org.jetbrains.annotations.NotNull()
    private final java.util.concurrent.ConcurrentHashMap<java.lang.String, kotlin.jvm.functions.Function3<java.lang.String, byte[], kotlin.coroutines.Continuation<? super kotlin.Unit>, java.lang.Object>> chatPacketHandlers = null;
    
    /**
     * Notified when the DH handshake completes for a conversation.
     */
    @org.jetbrains.annotations.NotNull()
    private final java.util.concurrent.ConcurrentHashMap<java.lang.String, kotlin.jvm.functions.Function2<com.hacksecure.messenger.domain.crypto.MessageProcessor, kotlin.coroutines.Continuation<? super kotlin.Unit>, java.lang.Object>> sessionReadyCallbacks = null;
    
    /**
     * Notified on connection state changes.
     */
    @org.jetbrains.annotations.NotNull()
    private final java.util.concurrent.ConcurrentHashMap<java.lang.String, kotlin.jvm.functions.Function2<com.hacksecure.messenger.domain.model.ConnectionState, kotlin.coroutines.Continuation<? super kotlin.Unit>, java.lang.Object>> connStateCallbacks = null;
    
    @javax.inject.Inject()
    public BackgroundConnectionManager(@org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.repository.ContactRepository contactRepository, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.repository.IdentityRepository identityRepository, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.repository.MessageRepository messageRepository, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.crypto.HandshakeManager handshakeManager, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.crypto.IdentityKeyManager identityKeyManager, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.data.remote.api.RelayApi relayApi, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.data.remote.ServerConfig serverConfig, @org.jetbrains.annotations.NotNull()
    okhttp3.OkHttpClient okHttpClient) {
        super();
    }
    
    /**
     * Called by ChatViewModel when the chat screen opens.
     * - Routes incoming MESSAGE packets to [packetHandler].
     * - Calls [onSessionReady] immediately if a session is already established,
     *  or later when the handshake completes.
     */
    public final void registerChatHandler(@org.jetbrains.annotations.NotNull()
    java.lang.String convId, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function3<? super java.lang.String, ? super byte[], ? super kotlin.coroutines.Continuation<? super kotlin.Unit>, ? extends java.lang.Object> packetHandler, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super com.hacksecure.messenger.domain.crypto.MessageProcessor, ? super kotlin.coroutines.Continuation<? super kotlin.Unit>, ? extends java.lang.Object> onSessionReady, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super com.hacksecure.messenger.domain.model.ConnectionState, ? super kotlin.coroutines.Continuation<? super kotlin.Unit>, ? extends java.lang.Object> onConnectionStateChange) {
    }
    
    public final void unregisterChatHandler(@org.jetbrains.annotations.NotNull()
    java.lang.String convId) {
    }
    
    /**
     * Returns the established [MessageProcessor] for [convId], or null.
     */
    @org.jetbrains.annotations.Nullable()
    public final com.hacksecure.messenger.domain.crypto.MessageProcessor getMessageProcessor(@org.jetbrains.annotations.NotNull()
    java.lang.String convId) {
        return null;
    }
    
    /**
     * Transmits a wire-packet for [convId] via this manager's WebSocket.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object sendPacket(@org.jetbrains.annotations.NotNull()
    java.lang.String convId, @org.jetbrains.annotations.NotNull()
    java.lang.String messageId, @org.jetbrains.annotations.NotNull()
    byte[] packet, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    public final boolean isConnected(@org.jetbrains.annotations.NotNull()
    java.lang.String convId) {
        return false;
    }
    
    /**
     * Starts background connections for all known contacts.
     * Idempotent — safe to call multiple times.
     * Called from [HackSecureApp.onCreate].
     */
    public final void startAllConnections() {
    }
    
    /**
     * Ensures a background connection exists for the given conversation.
     * Idempotent — no-op if already connected.
     */
    public final void ensureConnected(@org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.model.LocalIdentity identity, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.model.Contact contact) {
    }
    
    /**
     * Tears down and re-establishes the connection for a conversation.
     * Called by ChatViewModel's "Retry Connection" button.
     */
    public final void forceReconnect(@org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.model.LocalIdentity identity, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.model.Contact contact) {
    }
    
    private final java.lang.Object connectConversation(com.hacksecure.messenger.data.remote.BackgroundConnectionManager.ConvState state, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object completeHandshake(com.hacksecure.messenger.data.remote.BackgroundConnectionManager.ConvState state, byte[] offerBytes, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object receiveInBackground(com.hacksecure.messenger.data.remote.BackgroundConnectionManager.ConvState state, java.lang.String messageId, byte[] packetBytes, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void stopAll() {
    }
    
    private final java.lang.String buildConvId(java.lang.String a, java.lang.String b) {
        return null;
    }
    
    private final java.lang.String toHexString(byte[] $this$toHexString) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0012\n\u0002\b\u0005\n\u0002\u0010#\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u000e\u00106\u001a\u0002072\u0006\u00108\u001a\u00020\rR\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\r0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u001c\u0010\u001c\u001a\u0004\u0018\u00010\u001dX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u001f\"\u0004\b \u0010!R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u001c\u0010$\u001a\u0004\u0018\u00010%X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b&\u0010\'\"\u0004\b(\u0010)R\u001c\u0010*\u001a\u0004\u0018\u00010+X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b,\u0010-\"\u0004\b.\u0010/R\u0017\u00100\u001a\b\u0012\u0004\u0012\u00020\u000301\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u00103R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b4\u00105\u00a8\u00069"}, d2 = {"Lcom/hacksecure/messenger/data/remote/BackgroundConnectionManager$ConvState;", "", "conversationId", "", "contact", "Lcom/hacksecure/messenger/domain/model/Contact;", "myIdentity", "Lcom/hacksecure/messenger/domain/model/LocalIdentity;", "wsClient", "Lcom/hacksecure/messenger/data/remote/websocket/RelayWebSocketClient;", "(Ljava/lang/String;Lcom/hacksecure/messenger/domain/model/Contact;Lcom/hacksecure/messenger/domain/model/LocalIdentity;Lcom/hacksecure/messenger/data/remote/websocket/RelayWebSocketClient;)V", "_connState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/hacksecure/messenger/domain/model/ConnectionState;", "collectionJob", "Lkotlinx/coroutines/Job;", "getCollectionJob", "()Lkotlinx/coroutines/Job;", "setCollectionJob", "(Lkotlinx/coroutines/Job;)V", "connectionState", "Lkotlinx/coroutines/flow/StateFlow;", "getConnectionState", "()Lkotlinx/coroutines/flow/StateFlow;", "getContact", "()Lcom/hacksecure/messenger/domain/model/Contact;", "getConversationId", "()Ljava/lang/String;", "messageProcessor", "Lcom/hacksecure/messenger/domain/crypto/MessageProcessor;", "getMessageProcessor", "()Lcom/hacksecure/messenger/domain/crypto/MessageProcessor;", "setMessageProcessor", "(Lcom/hacksecure/messenger/domain/crypto/MessageProcessor;)V", "getMyIdentity", "()Lcom/hacksecure/messenger/domain/model/LocalIdentity;", "ourEphemeralPair", "Lcom/hacksecure/messenger/domain/crypto/SessionKeyManager$EphemeralKeyPair;", "getOurEphemeralPair", "()Lcom/hacksecure/messenger/domain/crypto/SessionKeyManager$EphemeralKeyPair;", "setOurEphemeralPair", "(Lcom/hacksecure/messenger/domain/crypto/SessionKeyManager$EphemeralKeyPair;)V", "ourOfferPacket", "", "getOurOfferPacket", "()[B", "setOurOfferPacket", "([B)V", "seenMessageIds", "", "getSeenMessageIds", "()Ljava/util/Set;", "getWsClient", "()Lcom/hacksecure/messenger/data/remote/websocket/RelayWebSocketClient;", "updateConnectionState", "", "s", "app_debug"})
    public static final class ConvState {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String conversationId = null;
        @org.jetbrains.annotations.NotNull()
        private final com.hacksecure.messenger.domain.model.Contact contact = null;
        @org.jetbrains.annotations.NotNull()
        private final com.hacksecure.messenger.domain.model.LocalIdentity myIdentity = null;
        @org.jetbrains.annotations.NotNull()
        private final com.hacksecure.messenger.data.remote.websocket.RelayWebSocketClient wsClient = null;
        @kotlin.jvm.Volatile()
        @org.jetbrains.annotations.Nullable()
        private volatile com.hacksecure.messenger.domain.crypto.MessageProcessor messageProcessor;
        @kotlin.jvm.Volatile()
        @org.jetbrains.annotations.Nullable()
        private volatile com.hacksecure.messenger.domain.crypto.SessionKeyManager.EphemeralKeyPair ourEphemeralPair;
        @kotlin.jvm.Volatile()
        @org.jetbrains.annotations.Nullable()
        private volatile byte[] ourOfferPacket;
        @org.jetbrains.annotations.NotNull()
        private final java.util.Set<java.lang.String> seenMessageIds = null;
        @org.jetbrains.annotations.Nullable()
        private kotlinx.coroutines.Job collectionJob;
        @org.jetbrains.annotations.NotNull()
        private final kotlinx.coroutines.flow.MutableStateFlow<com.hacksecure.messenger.domain.model.ConnectionState> _connState = null;
        @org.jetbrains.annotations.NotNull()
        private final kotlinx.coroutines.flow.StateFlow<com.hacksecure.messenger.domain.model.ConnectionState> connectionState = null;
        
        public ConvState(@org.jetbrains.annotations.NotNull()
        java.lang.String conversationId, @org.jetbrains.annotations.NotNull()
        com.hacksecure.messenger.domain.model.Contact contact, @org.jetbrains.annotations.NotNull()
        com.hacksecure.messenger.domain.model.LocalIdentity myIdentity, @org.jetbrains.annotations.NotNull()
        com.hacksecure.messenger.data.remote.websocket.RelayWebSocketClient wsClient) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getConversationId() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.hacksecure.messenger.domain.model.Contact getContact() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.hacksecure.messenger.domain.model.LocalIdentity getMyIdentity() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.hacksecure.messenger.data.remote.websocket.RelayWebSocketClient getWsClient() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final com.hacksecure.messenger.domain.crypto.MessageProcessor getMessageProcessor() {
            return null;
        }
        
        public final void setMessageProcessor(@org.jetbrains.annotations.Nullable()
        com.hacksecure.messenger.domain.crypto.MessageProcessor p0) {
        }
        
        @org.jetbrains.annotations.Nullable()
        public final com.hacksecure.messenger.domain.crypto.SessionKeyManager.EphemeralKeyPair getOurEphemeralPair() {
            return null;
        }
        
        public final void setOurEphemeralPair(@org.jetbrains.annotations.Nullable()
        com.hacksecure.messenger.domain.crypto.SessionKeyManager.EphemeralKeyPair p0) {
        }
        
        @org.jetbrains.annotations.Nullable()
        public final byte[] getOurOfferPacket() {
            return null;
        }
        
        public final void setOurOfferPacket(@org.jetbrains.annotations.Nullable()
        byte[] p0) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getSeenMessageIds() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final kotlinx.coroutines.Job getCollectionJob() {
            return null;
        }
        
        public final void setCollectionJob(@org.jetbrains.annotations.Nullable()
        kotlinx.coroutines.Job p0) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final kotlinx.coroutines.flow.StateFlow<com.hacksecure.messenger.domain.model.ConnectionState> getConnectionState() {
            return null;
        }
        
        public final void updateConnectionState(@org.jetbrains.annotations.NotNull()
        com.hacksecure.messenger.domain.model.ConnectionState s) {
        }
    }
}