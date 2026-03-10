package com.hacksecure.messenger.presentation.viewmodel;

import androidx.lifecycle.ViewModel;
import com.hacksecure.messenger.data.remote.GhostConnectionManager;
import com.hacksecure.messenger.data.remote.GhostEvent;
import com.hacksecure.messenger.domain.crypto.IdentityKeyManager;
import com.hacksecure.messenger.domain.model.*;
import com.hacksecure.messenger.domain.repository.GhostRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.*;
import kotlinx.coroutines.flow.*;
import java.util.UUID;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010#\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0010\u0012\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0006\u0010\u0018\u001a\u00020\u0019J\u001e\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u00132\u0006\u0010\u001c\u001a\u00020\u00132\u0006\u0010\u001d\u001a\u00020\u0013J\b\u0010\u001e\u001a\u00020\u0019H\u0014J\u001e\u0010\u001f\u001a\u00020\u00192\u0006\u0010 \u001a\u00020\u00132\u0006\u0010!\u001a\u00020\"H\u0082@\u00a2\u0006\u0002\u0010#J\u0006\u0010$\u001a\u00020\u0019J\u000e\u0010%\u001a\u00020\u00192\u0006\u0010&\u001a\u00020\u0013R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\f0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017\u00a8\u0006\'"}, d2 = {"Lcom/hacksecure/messenger/presentation/viewmodel/GhostChatViewModel;", "Landroidx/lifecycle/ViewModel;", "ghostConnectionManager", "Lcom/hacksecure/messenger/data/remote/GhostConnectionManager;", "identityKeyManager", "Lcom/hacksecure/messenger/domain/crypto/IdentityKeyManager;", "(Lcom/hacksecure/messenger/data/remote/GhostConnectionManager;Lcom/hacksecure/messenger/domain/crypto/IdentityKeyManager;)V", "_events", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/hacksecure/messenger/presentation/viewmodel/GhostChatEvent;", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/hacksecure/messenger/presentation/viewmodel/GhostChatUiState;", "events", "Lkotlinx/coroutines/flow/SharedFlow;", "getEvents", "()Lkotlinx/coroutines/flow/SharedFlow;", "seenMessageIds", "", "", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "disconnect", "", "initChannel", "channelId", "peerCodename", "anonymousId", "onCleared", "receiveMessage", "messageId", "packetBytes", "", "(Ljava/lang/String;[BLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sendMessage", "updateInputText", "text", "Hacksecure_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class GhostChatViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.data.remote.GhostConnectionManager ghostConnectionManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.crypto.IdentityKeyManager identityKeyManager = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.hacksecure.messenger.presentation.viewmodel.GhostChatUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.hacksecure.messenger.presentation.viewmodel.GhostChatUiState> uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableSharedFlow<com.hacksecure.messenger.presentation.viewmodel.GhostChatEvent> _events = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.SharedFlow<com.hacksecure.messenger.presentation.viewmodel.GhostChatEvent> events = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> seenMessageIds = null;
    
    @javax.inject.Inject()
    public GhostChatViewModel(@org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.data.remote.GhostConnectionManager ghostConnectionManager, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.crypto.IdentityKeyManager identityKeyManager) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.hacksecure.messenger.presentation.viewmodel.GhostChatUiState> getUiState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<com.hacksecure.messenger.presentation.viewmodel.GhostChatEvent> getEvents() {
        return null;
    }
    
    public final void initChannel(@org.jetbrains.annotations.NotNull()
    java.lang.String channelId, @org.jetbrains.annotations.NotNull()
    java.lang.String peerCodename, @org.jetbrains.annotations.NotNull()
    java.lang.String anonymousId) {
    }
    
    public final void updateInputText(@org.jetbrains.annotations.NotNull()
    java.lang.String text) {
    }
    
    public final void sendMessage() {
    }
    
    private final java.lang.Object receiveMessage(java.lang.String messageId, byte[] packetBytes, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void disconnect() {
    }
    
    @java.lang.Override()
    protected void onCleared() {
    }
}