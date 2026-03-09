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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u000fH\u0002R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u00a8\u0006\u0012"}, d2 = {"Lcom/hacksecure/messenger/presentation/viewmodel/HomeViewModel;", "Landroidx/lifecycle/ViewModel;", "contactRepository", "Lcom/hacksecure/messenger/domain/repository/ContactRepository;", "identityRepository", "Lcom/hacksecure/messenger/domain/repository/IdentityRepository;", "(Lcom/hacksecure/messenger/domain/repository/ContactRepository;Lcom/hacksecure/messenger/domain/repository/IdentityRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/hacksecure/messenger/presentation/viewmodel/HomeUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "buildConversationId", "", "a", "b", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class HomeViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.repository.ContactRepository contactRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.repository.IdentityRepository identityRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.hacksecure.messenger.presentation.viewmodel.HomeUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.hacksecure.messenger.presentation.viewmodel.HomeUiState> uiState = null;
    
    @javax.inject.Inject()
    public HomeViewModel(@org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.repository.ContactRepository contactRepository, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.repository.IdentityRepository identityRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.hacksecure.messenger.presentation.viewmodel.HomeUiState> getUiState() {
        return null;
    }
    
    private final java.lang.String buildConversationId(java.lang.String a, java.lang.String b) {
        return null;
    }
}