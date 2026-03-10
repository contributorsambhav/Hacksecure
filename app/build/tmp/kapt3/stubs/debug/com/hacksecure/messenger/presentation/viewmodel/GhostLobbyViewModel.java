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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001aJ\u0006\u0010\u001b\u001a\u00020\u0018J\b\u0010\u001c\u001a\u00020\u0018H\u0014J\u0006\u0010\u001d\u001a\u00020\u0018J\u000e\u0010\u001e\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001aJ\u0010\u0010\u001f\u001a\u00020\u00182\u0006\u0010 \u001a\u00020\u001aH\u0002J\u000e\u0010!\u001a\u00020\u00182\u0006\u0010\"\u001a\u00020\u001aJ\b\u0010#\u001a\u00020\u0018H\u0002J\u000e\u0010$\u001a\u00020\u00182\u0006\u0010%\u001a\u00020\u001aJ\u000e\u0010&\u001a\u00020\u00182\u0006\u0010 \u001a\u00020\u001aR\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\f0\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016\u00a8\u0006\'"}, d2 = {"Lcom/hacksecure/messenger/presentation/viewmodel/GhostLobbyViewModel;", "Landroidx/lifecycle/ViewModel;", "ghostRepository", "Lcom/hacksecure/messenger/domain/repository/GhostRepository;", "ghostConnectionManager", "Lcom/hacksecure/messenger/data/remote/GhostConnectionManager;", "(Lcom/hacksecure/messenger/domain/repository/GhostRepository;Lcom/hacksecure/messenger/data/remote/GhostConnectionManager;)V", "_events", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/hacksecure/messenger/presentation/viewmodel/GhostLobbyEvent;", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/hacksecure/messenger/presentation/viewmodel/GhostLobbyUiState;", "events", "Lkotlinx/coroutines/flow/SharedFlow;", "getEvents", "()Lkotlinx/coroutines/flow/SharedFlow;", "pollJob", "Lkotlinx/coroutines/Job;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "acceptRequest", "", "requestId", "", "leaveGhostMode", "onCleared", "register", "rejectRequest", "searchUsers", "query", "sendRequest", "targetCodename", "startPolling", "updateCodename", "name", "updateSearchQuery", "Hacksecure_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class GhostLobbyViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.repository.GhostRepository ghostRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.data.remote.GhostConnectionManager ghostConnectionManager = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.hacksecure.messenger.presentation.viewmodel.GhostLobbyUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.hacksecure.messenger.presentation.viewmodel.GhostLobbyUiState> uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableSharedFlow<com.hacksecure.messenger.presentation.viewmodel.GhostLobbyEvent> _events = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.SharedFlow<com.hacksecure.messenger.presentation.viewmodel.GhostLobbyEvent> events = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job pollJob;
    
    @javax.inject.Inject()
    public GhostLobbyViewModel(@org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.repository.GhostRepository ghostRepository, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.data.remote.GhostConnectionManager ghostConnectionManager) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.hacksecure.messenger.presentation.viewmodel.GhostLobbyUiState> getUiState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<com.hacksecure.messenger.presentation.viewmodel.GhostLobbyEvent> getEvents() {
        return null;
    }
    
    public final void updateCodename(@org.jetbrains.annotations.NotNull()
    java.lang.String name) {
    }
    
    public final void updateSearchQuery(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
    }
    
    public final void register() {
    }
    
    private final void searchUsers(java.lang.String query) {
    }
    
    public final void sendRequest(@org.jetbrains.annotations.NotNull()
    java.lang.String targetCodename) {
    }
    
    public final void acceptRequest(@org.jetbrains.annotations.NotNull()
    java.lang.String requestId) {
    }
    
    public final void rejectRequest(@org.jetbrains.annotations.NotNull()
    java.lang.String requestId) {
    }
    
    private final void startPolling() {
    }
    
    public final void leaveGhostMode() {
    }
    
    @java.lang.Override()
    protected void onCleared() {
    }
}