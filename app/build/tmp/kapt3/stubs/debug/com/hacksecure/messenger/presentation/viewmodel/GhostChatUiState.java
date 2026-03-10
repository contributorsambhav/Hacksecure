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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0016\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BG\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0006\u0012\b\b\u0002\u0010\f\u001a\u00020\n\u00a2\u0006\u0002\u0010\rJ\u000f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001a\u001a\u00020\nH\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\nH\u00c6\u0003JK\u0010\u001d\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\nH\u00c6\u0001J\u0013\u0010\u001e\u001a\u00020\n2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010 \u001a\u00020!H\u00d6\u0001J\t\u0010\"\u001a\u00020\u0006H\u00d6\u0001R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u000b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\f\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0012R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0011R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0012\u00a8\u0006#"}, d2 = {"Lcom/hacksecure/messenger/presentation/viewmodel/GhostChatUiState;", "", "messages", "", "Lcom/hacksecure/messenger/domain/model/GhostMessage;", "peerAnonymousId", "", "connectionState", "Lcom/hacksecure/messenger/domain/model/ConnectionState;", "sessionEstablished", "", "inputText", "isChannelDestroyed", "(Ljava/util/List;Ljava/lang/String;Lcom/hacksecure/messenger/domain/model/ConnectionState;ZLjava/lang/String;Z)V", "getConnectionState", "()Lcom/hacksecure/messenger/domain/model/ConnectionState;", "getInputText", "()Ljava/lang/String;", "()Z", "getMessages", "()Ljava/util/List;", "getPeerAnonymousId", "getSessionEstablished", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "", "toString", "Hacksecure_debug"})
public final class GhostChatUiState {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.hacksecure.messenger.domain.model.GhostMessage> messages = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String peerAnonymousId = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.model.ConnectionState connectionState = null;
    private final boolean sessionEstablished = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String inputText = null;
    private final boolean isChannelDestroyed = false;
    
    public GhostChatUiState(@org.jetbrains.annotations.NotNull()
    java.util.List<com.hacksecure.messenger.domain.model.GhostMessage> messages, @org.jetbrains.annotations.NotNull()
    java.lang.String peerAnonymousId, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.model.ConnectionState connectionState, boolean sessionEstablished, @org.jetbrains.annotations.NotNull()
    java.lang.String inputText, boolean isChannelDestroyed) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.hacksecure.messenger.domain.model.GhostMessage> getMessages() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPeerAnonymousId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.hacksecure.messenger.domain.model.ConnectionState getConnectionState() {
        return null;
    }
    
    public final boolean getSessionEstablished() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getInputText() {
        return null;
    }
    
    public final boolean isChannelDestroyed() {
        return false;
    }
    
    public GhostChatUiState() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.hacksecure.messenger.domain.model.GhostMessage> component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.hacksecure.messenger.domain.model.ConnectionState component3() {
        return null;
    }
    
    public final boolean component4() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    public final boolean component6() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.hacksecure.messenger.presentation.viewmodel.GhostChatUiState copy(@org.jetbrains.annotations.NotNull()
    java.util.List<com.hacksecure.messenger.domain.model.GhostMessage> messages, @org.jetbrains.annotations.NotNull()
    java.lang.String peerAnonymousId, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.model.ConnectionState connectionState, boolean sessionEstablished, @org.jetbrains.annotations.NotNull()
    java.lang.String inputText, boolean isChannelDestroyed) {
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