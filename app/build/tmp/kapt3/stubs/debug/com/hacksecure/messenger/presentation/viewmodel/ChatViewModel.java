package com.hacksecure.messenger.presentation.viewmodel;

import android.util.Base64;
import androidx.lifecycle.ViewModel;
import com.hacksecure.messenger.domain.crypto.*;
import com.hacksecure.messenger.domain.model.*;
import com.hacksecure.messenger.domain.repository.*;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.flow.*;
import java.util.UUID;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u008a\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B/\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ\u0018\u0010#\u001a\u00020\u00142\u0006\u0010$\u001a\u00020\u00142\u0006\u0010%\u001a\u00020\u0014H\u0002J\u000e\u0010&\u001a\u00020\'2\u0006\u0010(\u001a\u00020\u0014J\b\u0010)\u001a\u00020\'H\u0014J\u001e\u0010*\u001a\u00020\'2\u0006\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020.2\u0006\u0010/\u001a\u000200J\u0016\u00101\u001a\u00020\'2\u0006\u00102\u001a\u00020,2\u0006\u0010-\u001a\u00020.J\u0006\u00103\u001a\u00020\'J\u000e\u00104\u001a\u00020\'2\u0006\u00105\u001a\u000206J\u000e\u00107\u001a\u00020\'2\u0006\u00108\u001a\u00020\u0014J\f\u00109\u001a\u00020\u0014*\u00020,H\u0002R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0019\u001a\u0004\u0018\u00010\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u001d0\u001cX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u001e\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u001d0\u001cX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00120 \u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\"\u00a8\u0006:"}, d2 = {"Lcom/hacksecure/messenger/presentation/viewmodel/ChatViewModel;", "Landroidx/lifecycle/ViewModel;", "messageRepository", "Lcom/hacksecure/messenger/domain/repository/MessageRepository;", "contactRepository", "Lcom/hacksecure/messenger/domain/repository/ContactRepository;", "identityRepository", "Lcom/hacksecure/messenger/domain/repository/IdentityRepository;", "identityKeyManager", "Lcom/hacksecure/messenger/domain/crypto/IdentityKeyManager;", "sessionKeyManager", "Lcom/hacksecure/messenger/domain/crypto/SessionKeyManager;", "(Lcom/hacksecure/messenger/domain/repository/MessageRepository;Lcom/hacksecure/messenger/domain/repository/ContactRepository;Lcom/hacksecure/messenger/domain/repository/IdentityRepository;Lcom/hacksecure/messenger/domain/crypto/IdentityKeyManager;Lcom/hacksecure/messenger/domain/crypto/SessionKeyManager;)V", "_events", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/hacksecure/messenger/presentation/viewmodel/ChatEvent;", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/hacksecure/messenger/presentation/viewmodel/ChatUiState;", "conversationId", "", "events", "Lkotlinx/coroutines/flow/SharedFlow;", "getEvents", "()Lkotlinx/coroutines/flow/SharedFlow;", "messageProcessor", "Lcom/hacksecure/messenger/domain/crypto/MessageProcessor;", "recvRatchets", "", "Lcom/hacksecure/messenger/domain/crypto/HashRatchet;", "sendRatchets", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "buildConversationId", "a", "b", "initConversation", "", "contactId", "onCleared", "onSessionEstablished", "sessionKey", "", "contact", "Lcom/hacksecure/messenger/domain/model/Contact;", "myIdentity", "Lcom/hacksecure/messenger/domain/model/LocalIdentity;", "receiveRawPacket", "packetBytes", "sendMessage", "setExpirySeconds", "seconds", "", "updateInputText", "text", "toHexString", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class ChatViewModel extends androidx.lifecycle.ViewModel {
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
    com.hacksecure.messenger.domain.crypto.SessionKeyManager sessionKeyManager) {
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
     * Call this when the DH handshake completes and session key is derived.
     * Creates the send/receive ratchets from the session root key.
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
     * Sends a text message through the full cryptographic pipeline.
     */
    public final void sendMessage() {
    }
    
    /**
     * Processes an incoming raw wire packet through the full receive pipeline.
     * Called from WebSocket/WebRTC data channel.
     */
    public final void receiveRawPacket(@org.jetbrains.annotations.NotNull()
    byte[] packetBytes, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.model.Contact contact) {
    }
    
    @java.lang.Override()
    protected void onCleared() {
    }
    
    private final java.lang.String buildConversationId(java.lang.String a, java.lang.String b) {
        return null;
    }
    
    private final java.lang.String toHexString(byte[] $this$toHexString) {
        return null;
    }
}