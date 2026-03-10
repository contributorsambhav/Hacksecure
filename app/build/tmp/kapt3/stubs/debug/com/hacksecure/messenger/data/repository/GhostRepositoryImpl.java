package com.hacksecure.messenger.data.repository;

import com.hacksecure.messenger.data.remote.ServerConfig;
import com.hacksecure.messenger.data.remote.api.*;
import com.hacksecure.messenger.domain.model.*;
import com.hacksecure.messenger.domain.repository.GhostRepository;
import java.security.MessageDigest;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J \u0010\u000b\u001a\u0004\u0018\u00010\f2\u0006\u0010\r\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\bH\u0096@\u00a2\u0006\u0002\u0010\u000fJ\u0018\u0010\u0010\u001a\u00020\b2\u0006\u0010\u0011\u001a\u00020\b2\u0006\u0010\u0012\u001a\u00020\bH\u0002J\u0016\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u000e\u001a\u00020\bH\u0096@\u00a2\u0006\u0002\u0010\u0015J.\u0010\u0016\u001a\u001a\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00190\u0018\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u00180\u00172\u0006\u0010\u000e\u001a\u00020\bH\u0096@\u00a2\u0006\u0002\u0010\u0015J\u0016\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\bH\u0096@\u00a2\u0006\u0002\u0010\u0015J\u001e\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\r\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\bH\u0096@\u00a2\u0006\u0002\u0010\u000fJ$\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\b0\u00182\u0006\u0010 \u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\bH\u0096@\u00a2\u0006\u0002\u0010\u000fJ \u0010!\u001a\u0004\u0018\u00010\b2\u0006\u0010\"\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\bH\u0096@\u00a2\u0006\u0002\u0010\u000fR\u0014\u0010\u0007\u001a\u00020\b8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\t\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006#"}, d2 = {"Lcom/hacksecure/messenger/data/repository/GhostRepositoryImpl;", "Lcom/hacksecure/messenger/domain/repository/GhostRepository;", "relayApi", "Lcom/hacksecure/messenger/data/remote/api/RelayApi;", "serverConfig", "Lcom/hacksecure/messenger/data/remote/ServerConfig;", "(Lcom/hacksecure/messenger/data/remote/api/RelayApi;Lcom/hacksecure/messenger/data/remote/ServerConfig;)V", "apiBase", "", "getApiBase", "()Ljava/lang/String;", "acceptRequest", "Lcom/hacksecure/messenger/domain/model/GhostChannel;", "requestId", "ghostToken", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "generateAnonymousId", "channelId", "peerCodename", "leave", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "pollRequests", "Lkotlin/Pair;", "", "Lcom/hacksecure/messenger/domain/model/GhostChatRequest;", "register", "Lcom/hacksecure/messenger/domain/model/GhostIdentity;", "codename", "rejectRequest", "", "searchOnlineUsers", "query", "sendChatRequest", "targetCodename", "Hacksecure_debug"})
public final class GhostRepositoryImpl implements com.hacksecure.messenger.domain.repository.GhostRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.data.remote.api.RelayApi relayApi = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.data.remote.ServerConfig serverConfig = null;
    
    @javax.inject.Inject()
    public GhostRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.data.remote.api.RelayApi relayApi, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.data.remote.ServerConfig serverConfig) {
        super();
    }
    
    private final java.lang.String getApiBase() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object register(@org.jetbrains.annotations.NotNull()
    java.lang.String codename, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.hacksecure.messenger.domain.model.GhostIdentity> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object searchOnlineUsers(@org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    java.lang.String ghostToken, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object sendChatRequest(@org.jetbrains.annotations.NotNull()
    java.lang.String targetCodename, @org.jetbrains.annotations.NotNull()
    java.lang.String ghostToken, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object pollRequests(@org.jetbrains.annotations.NotNull()
    java.lang.String ghostToken, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Pair<? extends java.util.List<com.hacksecure.messenger.domain.model.GhostChatRequest>, ? extends java.util.List<com.hacksecure.messenger.domain.model.GhostChannel>>> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object acceptRequest(@org.jetbrains.annotations.NotNull()
    java.lang.String requestId, @org.jetbrains.annotations.NotNull()
    java.lang.String ghostToken, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.hacksecure.messenger.domain.model.GhostChannel> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object rejectRequest(@org.jetbrains.annotations.NotNull()
    java.lang.String requestId, @org.jetbrains.annotations.NotNull()
    java.lang.String ghostToken, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object leave(@org.jetbrains.annotations.NotNull()
    java.lang.String ghostToken, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Generates a deterministic anonymous ID from channel + peer info.
     */
    private final java.lang.String generateAnonymousId(java.lang.String channelId, java.lang.String peerCodename) {
        return null;
    }
}