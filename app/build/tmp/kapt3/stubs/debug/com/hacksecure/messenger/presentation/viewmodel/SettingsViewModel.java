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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0019\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0006\u0010\u0011\u001a\u00020\u0012J\u0006\u0010\u0013\u001a\u00020\u0012J\u000e\u0010\u0014\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u0016J\u000e\u0010\u0017\u001a\u00020\u00122\u0006\u0010\u0018\u001a\u00020\u0019J\u0006\u0010\u001a\u001a\u00020\u0012R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\n\u001a\n \f*\u0004\u0018\u00010\u000b0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001b"}, d2 = {"Lcom/hacksecure/messenger/presentation/viewmodel/SettingsViewModel;", "Landroidx/lifecycle/ViewModel;", "identityRepository", "Lcom/hacksecure/messenger/domain/repository/IdentityRepository;", "appContext", "Landroid/content/Context;", "(Lcom/hacksecure/messenger/domain/repository/IdentityRepository;Landroid/content/Context;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/hacksecure/messenger/presentation/viewmodel/SettingsUiState;", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "dismissRegenerateConfirm", "", "regenerateIdentity", "setDefaultExpiry", "seconds", "", "setScreenshotBlocking", "enabled", "", "showRegenerateConfirm", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class SettingsViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.repository.IdentityRepository identityRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context appContext = null;
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.hacksecure.messenger.presentation.viewmodel.SettingsUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.hacksecure.messenger.presentation.viewmodel.SettingsUiState> uiState = null;
    
    @javax.inject.Inject()
    public SettingsViewModel(@org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.repository.IdentityRepository identityRepository, @dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context appContext) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.hacksecure.messenger.presentation.viewmodel.SettingsUiState> getUiState() {
        return null;
    }
    
    public final void showRegenerateConfirm() {
    }
    
    public final void dismissRegenerateConfirm() {
    }
    
    public final void regenerateIdentity() {
    }
    
    /**
     * Persists the screenshot blocking preference.
     * MainActivity reads this on onResume() and applies/clears FLAG_SECURE accordingly.
     */
    public final void setScreenshotBlocking(boolean enabled) {
    }
    
    public final void setDefaultExpiry(int seconds) {
    }
}