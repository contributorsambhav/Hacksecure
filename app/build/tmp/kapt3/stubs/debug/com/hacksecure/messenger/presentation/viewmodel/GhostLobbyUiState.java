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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0000\n\u0002\u0018\u0002\n\u0002\b!\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0093\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0005\u0012\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\n\u0012\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00030\u000f\u0012\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0014J\t\u0010#\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010$\u001a\u0004\u0018\u00010\u0011H\u00c6\u0003J\t\u0010%\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010&\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010\'\u001a\u00020\u0005H\u00c6\u0003J\t\u0010(\u001a\u00020\u0003H\u00c6\u0003J\t\u0010)\u001a\u00020\u0003H\u00c6\u0003J\t\u0010*\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00030\nH\u00c6\u0003J\t\u0010,\u001a\u00020\u0005H\u00c6\u0003J\u000f\u0010-\u001a\b\u0012\u0004\u0012\u00020\r0\nH\u00c6\u0003J\u000f\u0010.\u001a\b\u0012\u0004\u0012\u00020\u00030\u000fH\u00c6\u0003J\u0097\u0001\u0010/\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\n2\b\b\u0002\u0010\u000b\u001a\u00020\u00052\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\n2\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00030\u000f2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u00052\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u00100\u001a\u00020\u00052\b\u00101\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00102\u001a\u000203H\u00d6\u0001J\t\u00104\u001a\u00020\u0003H\u00d6\u0001R\u0013\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0013\u0010\u0013\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0018R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0018R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u001dR\u0011\u0010\u0012\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u001dR\u0011\u0010\u000b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u001dR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0018R\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0018R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\n\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001cR\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00030\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\"\u00a8\u00065"}, d2 = {"Lcom/hacksecure/messenger/presentation/viewmodel/GhostLobbyUiState;", "", "codename", "", "isRegistered", "", "ghostToken", "registeredCodename", "searchQuery", "searchResults", "", "isSearching", "incomingRequests", "Lcom/hacksecure/messenger/domain/model/GhostChatRequest;", "sentRequests", "", "acceptedChannel", "Lcom/hacksecure/messenger/domain/model/GhostChannel;", "isRegistering", "error", "(Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;ZLjava/util/List;Ljava/util/Set;Lcom/hacksecure/messenger/domain/model/GhostChannel;ZLjava/lang/String;)V", "getAcceptedChannel", "()Lcom/hacksecure/messenger/domain/model/GhostChannel;", "getCodename", "()Ljava/lang/String;", "getError", "getGhostToken", "getIncomingRequests", "()Ljava/util/List;", "()Z", "getRegisteredCodename", "getSearchQuery", "getSearchResults", "getSentRequests", "()Ljava/util/Set;", "component1", "component10", "component11", "component12", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "", "toString", "Hacksecure_debug"})
public final class GhostLobbyUiState {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String codename = null;
    private final boolean isRegistered = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String ghostToken = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String registeredCodename = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String searchQuery = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> searchResults = null;
    private final boolean isSearching = false;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.hacksecure.messenger.domain.model.GhostChatRequest> incomingRequests = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> sentRequests = null;
    @org.jetbrains.annotations.Nullable()
    private final com.hacksecure.messenger.domain.model.GhostChannel acceptedChannel = null;
    private final boolean isRegistering = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String error = null;
    
    public GhostLobbyUiState(@org.jetbrains.annotations.NotNull()
    java.lang.String codename, boolean isRegistered, @org.jetbrains.annotations.NotNull()
    java.lang.String ghostToken, @org.jetbrains.annotations.NotNull()
    java.lang.String registeredCodename, @org.jetbrains.annotations.NotNull()
    java.lang.String searchQuery, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> searchResults, boolean isSearching, @org.jetbrains.annotations.NotNull()
    java.util.List<com.hacksecure.messenger.domain.model.GhostChatRequest> incomingRequests, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> sentRequests, @org.jetbrains.annotations.Nullable()
    com.hacksecure.messenger.domain.model.GhostChannel acceptedChannel, boolean isRegistering, @org.jetbrains.annotations.Nullable()
    java.lang.String error) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCodename() {
        return null;
    }
    
    public final boolean isRegistered() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getGhostToken() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRegisteredCodename() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSearchQuery() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getSearchResults() {
        return null;
    }
    
    public final boolean isSearching() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.hacksecure.messenger.domain.model.GhostChatRequest> getIncomingRequests() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getSentRequests() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.hacksecure.messenger.domain.model.GhostChannel getAcceptedChannel() {
        return null;
    }
    
    public final boolean isRegistering() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getError() {
        return null;
    }
    
    public GhostLobbyUiState() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.hacksecure.messenger.domain.model.GhostChannel component10() {
        return null;
    }
    
    public final boolean component11() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component12() {
        return null;
    }
    
    public final boolean component2() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component6() {
        return null;
    }
    
    public final boolean component7() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.hacksecure.messenger.domain.model.GhostChatRequest> component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.hacksecure.messenger.presentation.viewmodel.GhostLobbyUiState copy(@org.jetbrains.annotations.NotNull()
    java.lang.String codename, boolean isRegistered, @org.jetbrains.annotations.NotNull()
    java.lang.String ghostToken, @org.jetbrains.annotations.NotNull()
    java.lang.String registeredCodename, @org.jetbrains.annotations.NotNull()
    java.lang.String searchQuery, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> searchResults, boolean isSearching, @org.jetbrains.annotations.NotNull()
    java.util.List<com.hacksecure.messenger.domain.model.GhostChatRequest> incomingRequests, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> sentRequests, @org.jetbrains.annotations.Nullable()
    com.hacksecure.messenger.domain.model.GhostChannel acceptedChannel, boolean isRegistering, @org.jetbrains.annotations.Nullable()
    java.lang.String error) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}