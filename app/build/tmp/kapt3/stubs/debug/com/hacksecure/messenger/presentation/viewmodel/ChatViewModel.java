package com.hacksecure.messenger.presentation.viewmodel;

import android.util.Base64;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import com.hacksecure.messenger.data.remote.api.PresenceRequest;
import com.hacksecure.messenger.data.remote.api.RelayApi;
import com.hacksecure.messenger.data.remote.ServerConfig;
import com.hacksecure.messenger.data.remote.api.TicketRequest;
import com.hacksecure.messenger.data.remote.websocket.RelayEvent;
import com.hacksecure.messenger.data.remote.websocket.RelayWebSocketClient;
import com.hacksecure.messenger.domain.crypto.*;
import com.hacksecure.messenger.domain.model.*;
import com.hacksecure.messenger.domain.repository.*;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.flow.*;
import java.util.UUID;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00be\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010#\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u00012\u00020\u0002BO\b\u0007\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u0010\u0012\u0006\u0010\u0011\u001a\u00020\u0012\u0012\u0006\u0010\u0013\u001a\u00020\u0014\u00a2\u0006\u0002\u0010\u0015J\u0018\u00108\u001a\u00020\u001f2\u0006\u00109\u001a\u00020\u001f2\u0006\u0010:\u001a\u00020\u001fH\u0002J \u0010;\u001a\u00020<2\u0006\u0010=\u001a\u00020+2\u0006\u0010>\u001a\u00020\u001d2\u0006\u0010?\u001a\u00020\'H\u0002J \u0010@\u001a\u00020<2\u0006\u0010A\u001a\u00020\u001f2\u0006\u0010?\u001a\u00020\'2\u0006\u0010>\u001a\u00020\u001dH\u0002J(\u0010B\u001a\u00020<2\u0006\u0010C\u001a\u00020\u001f2\u0006\u0010D\u001a\u00020+2\u0006\u0010>\u001a\u00020\u001d2\u0006\u0010&\u001a\u00020\'H\u0002J\u000e\u0010E\u001a\u00020<2\u0006\u0010F\u001a\u00020\u001fJ\b\u0010G\u001a\u00020<H\u0014J\u001e\u0010H\u001a\u00020<2\u0006\u0010I\u001a\u00020+2\u0006\u0010>\u001a\u00020\u001d2\u0006\u0010&\u001a\u00020\'J\u0010\u0010J\u001a\u00020<2\u0006\u0010K\u001a\u00020LH\u0016J\u0018\u0010M\u001a\u00020<2\u0006\u0010C\u001a\u00020\u001f2\u0006\u0010D\u001a\u00020+H\u0002J\u0006\u0010N\u001a\u00020<J\u0006\u0010O\u001a\u00020<J\u0006\u0010P\u001a\u00020<J\u000e\u0010Q\u001a\u00020<2\u0006\u0010R\u001a\u00020SJ\u000e\u0010T\u001a\u00020<2\u0006\u0010U\u001a\u00020\u001fJ\f\u0010V\u001a\u00020\u001f*\u00020+H\u0002R\u0014\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001b0\u001aX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u001dX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001e\u001a\u0004\u0018\u00010\u001fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00180!\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010$\u001a\u0004\u0018\u00010%X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010&\u001a\u0004\u0018\u00010\'X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010(\u001a\u0004\u0018\u00010)X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010*\u001a\u0004\u0018\u00010+X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010,\u001a\u000e\u0012\u0004\u0012\u00020\u001f\u0012\u0004\u0012\u00020.0-X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010/\u001a\u0004\u0018\u000100X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u00101\u001a\b\u0012\u0004\u0012\u00020\u001f02X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u00103\u001a\u000e\u0012\u0004\u0012\u00020\u001f\u0012\u0004\u0012\u00020.0-X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u00104\u001a\b\u0012\u0004\u0012\u00020\u001b05\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u00107\u00a8\u0006W"}, d2 = {"Lcom/hacksecure/messenger/presentation/viewmodel/ChatViewModel;", "Landroidx/lifecycle/ViewModel;", "Landroidx/lifecycle/DefaultLifecycleObserver;", "messageRepository", "Lcom/hacksecure/messenger/domain/repository/MessageRepository;", "contactRepository", "Lcom/hacksecure/messenger/domain/repository/ContactRepository;", "identityRepository", "Lcom/hacksecure/messenger/domain/repository/IdentityRepository;", "identityKeyManager", "Lcom/hacksecure/messenger/domain/crypto/IdentityKeyManager;", "sessionKeyManager", "Lcom/hacksecure/messenger/domain/crypto/SessionKeyManager;", "handshakeManager", "Lcom/hacksecure/messenger/domain/crypto/HandshakeManager;", "relayWebSocketClient", "Lcom/hacksecure/messenger/data/remote/websocket/RelayWebSocketClient;", "relayApi", "Lcom/hacksecure/messenger/data/remote/api/RelayApi;", "serverConfig", "Lcom/hacksecure/messenger/data/remote/ServerConfig;", "(Lcom/hacksecure/messenger/domain/repository/MessageRepository;Lcom/hacksecure/messenger/domain/repository/ContactRepository;Lcom/hacksecure/messenger/domain/repository/IdentityRepository;Lcom/hacksecure/messenger/domain/crypto/IdentityKeyManager;Lcom/hacksecure/messenger/domain/crypto/SessionKeyManager;Lcom/hacksecure/messenger/domain/crypto/HandshakeManager;Lcom/hacksecure/messenger/data/remote/websocket/RelayWebSocketClient;Lcom/hacksecure/messenger/data/remote/api/RelayApi;Lcom/hacksecure/messenger/data/remote/ServerConfig;)V", "_events", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/hacksecure/messenger/presentation/viewmodel/ChatEvent;", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/hacksecure/messenger/presentation/viewmodel/ChatUiState;", "activeContact", "Lcom/hacksecure/messenger/domain/model/Contact;", "conversationId", "", "events", "Lkotlinx/coroutines/flow/SharedFlow;", "getEvents", "()Lkotlinx/coroutines/flow/SharedFlow;", "messageProcessor", "Lcom/hacksecure/messenger/domain/crypto/MessageProcessor;", "myIdentity", "Lcom/hacksecure/messenger/domain/model/LocalIdentity;", "ourEphemeralPair", "Lcom/hacksecure/messenger/domain/crypto/SessionKeyManager$EphemeralKeyPair;", "ourOfferPacket", "", "recvRatchets", "", "Lcom/hacksecure/messenger/domain/crypto/HashRatchet;", "relayJob", "Lkotlinx/coroutines/Job;", "seenMessageIds", "", "sendRatchets", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "buildConversationId", "a", "b", "completeHandshake", "", "offerBytes", "contact", "identity", "connectRelay", "convId", "handleIncomingPacket", "messageId", "packetBytes", "initConversation", "contactId", "onCleared", "onSessionEstablished", "sessionKey", "onStart", "owner", "Landroidx/lifecycle/LifecycleOwner;", "receiveMessage", "retryConnection", "retryFailedMessages", "sendMessage", "setExpirySeconds", "seconds", "", "updateInputText", "text", "toHexString", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class ChatViewModel extends androidx.lifecycle.ViewModel implements androidx.lifecycle.DefaultLifecycleObserver {
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.repository.MessageRepository messageRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.repository.ContactRepository contactRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.repository.IdentityRepository identityRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.crypto.IdentityKeyManager identityKeyManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.crypto.SessionKeyManager sessionKeyManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.crypto.HandshakeManager handshakeManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.data.remote.websocket.RelayWebSocketClient relayWebSocketClient = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.data.remote.api.RelayApi relayApi = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.data.remote.ServerConfig serverConfig = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.hacksecure.messenger.presentation.viewmodel.ChatUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.hacksecure.messenger.presentation.viewmodel.ChatUiState> uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableSharedFlow<com.hacksecure.messenger.presentation.viewmodel.ChatEvent> _events = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.SharedFlow<com.hacksecure.messenger.presentation.viewmodel.ChatEvent> events = null;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private volatile com.hacksecure.messenger.domain.crypto.MessageProcessor messageProcessor;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private volatile java.lang.String conversationId;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private volatile com.hacksecure.messenger.domain.model.Contact activeContact;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private volatile com.hacksecure.messenger.domain.model.LocalIdentity myIdentity;
    
    /**
     * Our ephemeral X25519 keypair for the current handshake round.
     */
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private volatile com.hacksecure.messenger.domain.crypto.SessionKeyManager.EphemeralKeyPair ourEphemeralPair;
    
    /**
     * The serialized 97-byte HANDSHAKE_OFFER we sent. Kept so we can re-send it to a peer
     * that connected after our initial offer (relay doesn't buffer — they missed it).
     */
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private volatile byte[] ourOfferPacket;
    
    /**
     * Job controlling the relay event-collection coroutine; cancelled before each reconnect.
     */
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job relayJob;
    
    /**
     * In-memory de-duplication for relay messages processed this session.
     */
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> seenMessageIds = null;
    
    /**
     * Hash ratchets keyed by conversationId â€” zeroized on ViewModel clear.
     */
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, com.hacksecure.messenger.domain.crypto.HashRatchet> sendRatchets = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, com.hacksecure.messenger.domain.crypto.HashRatchet> recvRatchets = null;
    
    @javax.inject.Inject()
    public ChatViewModel(@org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.repository.MessageRepository messageRepository, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.repository.ContactRepository contactRepository, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.repository.IdentityRepository identityRepository, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.crypto.IdentityKeyManager identityKeyManager, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.crypto.SessionKeyManager sessionKeyManager, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.crypto.HandshakeManager handshakeManager, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.data.remote.websocket.RelayWebSocketClient relayWebSocketClient, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.data.remote.api.RelayApi relayApi, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.data.remote.ServerConfig serverConfig) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.hacksecure.messenger.presentation.viewmodel.ChatUiState> getUiState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<com.hacksecure.messenger.presentation.viewmodel.ChatEvent> getEvents() {
        return null;
    }
    
    public final void initConversation(@org.jetbrains.annotations.NotNull()
    java.lang.String contactId) {
    }
    
    /**
     * Starts (or restarts) the WebSocket relay connection and the event-collection
     * coroutine.  Any previous relay session is cancelled first so we never accumulate
     * stale coroutines.
     */
    private final void connectRelay(java.lang.String convId, com.hacksecure.messenger.domain.model.LocalIdentity identity, com.hacksecure.messenger.domain.model.Contact contact) {
    }
    
    @java.lang.Override()
    public void onStart(@org.jetbrains.annotations.NotNull()
    androidx.lifecycle.LifecycleOwner owner) {
    }
    
    /**
     * Discriminates between a HANDSHAKE_OFFER and an encrypted MESSAGE, then
     * routes accordingly.  Called for every packet received from the relay.
     */
    private final void handleIncomingPacket(java.lang.String messageId, byte[] packetBytes, com.hacksecure.messenger.domain.model.Contact contact, com.hacksecure.messenger.domain.model.LocalIdentity myIdentity) {
    }
    
    /**
     * Processes a peer HANDSHAKE_OFFER: verifies the Ed25519 signature, performs
     * X25519 DH, and establishes the session.
     *
     * If the session is already established (e.g. duplicate offer), this is a no-op
     * so the ratchet state is not corrupted.
     */
    private final void completeHandshake(byte[] offerBytes, com.hacksecure.messenger.domain.model.Contact contact, com.hacksecure.messenger.domain.model.LocalIdentity identity) {
    }
    
    /**
     * Installs a fully-established session into the ViewModel.
     * Also callable externally (e.g. for testing or future manual key exchange flows).
     *
     * @param sessionKey 32-byte root ratchet key â€” ZEROIZED by this function after use
     */
    public final void onSessionEstablished(@org.jetbrains.annotations.NotNull()
    byte[] sessionKey, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.model.Contact contact, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.model.LocalIdentity myIdentity) {
    }
    
    public final void updateInputText(@org.jetbrains.annotations.NotNull()
    java.lang.String text) {
    }
    
    public final void setExpirySeconds(int seconds) {
    }
    
    /**
     * Encrypts and sends the current input text through the full cryptographic pipeline.
     *
     * Ordering (correct):
     *  1. Encrypt plaintext â†’ wirePacketBytes
     *  2. Save message to DB as SENDING
     *  3. Send wirePacketBytes over relay
     *  4. Update message state to SENT or FAILED based on relay result
     */
    public final void sendMessage() {
    }
    
    /**
     * Decrypts and persists an incoming wire-packet received from the relay.
     *
     * De-duplicates using the relay-level [messageId] so retransmits (network glitches)
     * are silently discarded.
     */
    private final void receiveMessage(java.lang.String messageId, byte[] packetBytes) {
    }
    
    /**
     * Re-queues all FAILED outgoing messages for the current conversation.
     * Called automatically in [ChatScreen] when [ConnectionState.CONNECTED_RELAY] fires.
     *
     * Note: because we don't persist wire bytes, re-encryption requires a live session.
     * This function marks FAILED â†’ SENDING so the UI shows them as pending rather than
     * stuck in a permanent error state. Full re-encryption retry is Phase 3 work.
     */
    public final void retryFailedMessages() {
    }
    
    @java.lang.Override()
    protected void onCleared() {
    }
    
    /**
     * Re-establish relay connection after a disconnection or URL change.
     */
    public final void retryConnection() {
    }
    
    private final java.lang.String buildConversationId(java.lang.String a, java.lang.String b) {
        return null;
    }
    
    private final java.lang.String toHexString(byte[] $this$toHexString) {
        return null;
    }
}