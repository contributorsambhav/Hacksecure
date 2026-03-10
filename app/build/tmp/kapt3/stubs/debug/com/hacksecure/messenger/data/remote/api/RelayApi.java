package com.hacksecure.messenger.data.remote.api;

import retrofit2.http.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J8\u0010\u0002\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u0006\u001a\u00020\u00052\u0014\b\u0003\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\"\u0010\n\u001a\u00020\u000b2\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u0006\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\"\u0010\r\u001a\u00020\u000e2\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u000f\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0011J,\u0010\u0012\u001a\u00020\u00132\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u0006\u001a\u00020\u00052\b\b\u0001\u0010\u000f\u001a\u00020\u0014H\u00a7@\u00a2\u0006\u0002\u0010\u0015J,\u0010\u0016\u001a\u00020\u00172\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u0006\u001a\u00020\u00052\b\b\u0001\u0010\u000f\u001a\u00020\u0018H\u00a7@\u00a2\u0006\u0002\u0010\u0019J,\u0010\u001a\u001a\u00020\u001b2\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u0006\u001a\u00020\u00052\b\b\u0001\u0010\u001c\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u001dJ\"\u0010\u001e\u001a\u00020\u001f2\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u000f\u001a\u00020 H\u00a7@\u00a2\u0006\u0002\u0010!J\"\u0010\"\u001a\u00020#2\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u000f\u001a\u00020$H\u00a7@\u00a2\u0006\u0002\u0010%\u00a8\u0006&"}, d2 = {"Lcom/hacksecure/messenger/data/remote/api/RelayApi;", "", "ghostLeave", "", "url", "", "token", "body", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "ghostPoll", "Lcom/hacksecure/messenger/data/remote/api/GhostPollResponse;", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "ghostRegister", "Lcom/hacksecure/messenger/data/remote/api/GhostRegisterResponse;", "request", "Lcom/hacksecure/messenger/data/remote/api/GhostRegisterRequest;", "(Ljava/lang/String;Lcom/hacksecure/messenger/data/remote/api/GhostRegisterRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "ghostRequest", "Lcom/hacksecure/messenger/data/remote/api/GhostSendResponse;", "Lcom/hacksecure/messenger/data/remote/api/GhostSendRequest;", "(Ljava/lang/String;Ljava/lang/String;Lcom/hacksecure/messenger/data/remote/api/GhostSendRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "ghostRespond", "Lcom/hacksecure/messenger/data/remote/api/GhostRespondResponse;", "Lcom/hacksecure/messenger/data/remote/api/GhostRespondRequest;", "(Ljava/lang/String;Ljava/lang/String;Lcom/hacksecure/messenger/data/remote/api/GhostRespondRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "ghostSearch", "Lcom/hacksecure/messenger/data/remote/api/GhostSearchResponse;", "query", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "registerPresence", "Lcom/hacksecure/messenger/data/remote/api/PresenceResponse;", "Lcom/hacksecure/messenger/data/remote/api/PresenceRequest;", "(Ljava/lang/String;Lcom/hacksecure/messenger/data/remote/api/PresenceRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "requestTicket", "Lcom/hacksecure/messenger/data/remote/api/TicketResponse;", "Lcom/hacksecure/messenger/data/remote/api/TicketRequest;", "(Ljava/lang/String;Lcom/hacksecure/messenger/data/remote/api/TicketRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Hacksecure_debug"})
public abstract interface RelayApi {
    
    @retrofit2.http.POST()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object requestTicket(@retrofit2.http.Url()
    @org.jetbrains.annotations.NotNull()
    java.lang.String url, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.data.remote.api.TicketRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.hacksecure.messenger.data.remote.api.TicketResponse> $completion);
    
    @retrofit2.http.POST()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object registerPresence(@retrofit2.http.Url()
    @org.jetbrains.annotations.NotNull()
    java.lang.String url, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.data.remote.api.PresenceRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.hacksecure.messenger.data.remote.api.PresenceResponse> $completion);
    
    @retrofit2.http.POST()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object ghostRegister(@retrofit2.http.Url()
    @org.jetbrains.annotations.NotNull()
    java.lang.String url, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.data.remote.api.GhostRegisterRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.hacksecure.messenger.data.remote.api.GhostRegisterResponse> $completion);
    
    @retrofit2.http.GET()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object ghostSearch(@retrofit2.http.Url()
    @org.jetbrains.annotations.NotNull()
    java.lang.String url, @retrofit2.http.Header(value = "X-Ghost-Token")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Query(value = "q")
    @org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.hacksecure.messenger.data.remote.api.GhostSearchResponse> $completion);
    
    @retrofit2.http.POST()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object ghostRequest(@retrofit2.http.Url()
    @org.jetbrains.annotations.NotNull()
    java.lang.String url, @retrofit2.http.Header(value = "X-Ghost-Token")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.data.remote.api.GhostSendRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.hacksecure.messenger.data.remote.api.GhostSendResponse> $completion);
    
    @retrofit2.http.POST()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object ghostRespond(@retrofit2.http.Url()
    @org.jetbrains.annotations.NotNull()
    java.lang.String url, @retrofit2.http.Header(value = "X-Ghost-Token")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.data.remote.api.GhostRespondRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.hacksecure.messenger.data.remote.api.GhostRespondResponse> $completion);
    
    @retrofit2.http.GET()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object ghostPoll(@retrofit2.http.Url()
    @org.jetbrains.annotations.NotNull()
    java.lang.String url, @retrofit2.http.Header(value = "X-Ghost-Token")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.hacksecure.messenger.data.remote.api.GhostPollResponse> $completion);
    
    @retrofit2.http.POST()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object ghostLeave(@retrofit2.http.Url()
    @org.jetbrains.annotations.NotNull()
    java.lang.String url, @retrofit2.http.Header(value = "X-Ghost-Token")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}