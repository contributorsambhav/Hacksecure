package com.hacksecure.messenger.data.remote.api;

import retrofit2.http.Body;
import retrofit2.http.POST;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u0007\u001a\u00020\b2\b\b\u0001\u0010\u0004\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\n\u00a8\u0006\u000b"}, d2 = {"Lcom/hacksecure/messenger/data/remote/api/RelayApi;", "", "registerPresence", "Lcom/hacksecure/messenger/data/remote/api/PresenceResponse;", "request", "Lcom/hacksecure/messenger/data/remote/api/PresenceRequest;", "(Lcom/hacksecure/messenger/data/remote/api/PresenceRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "requestTicket", "Lcom/hacksecure/messenger/data/remote/api/TicketResponse;", "Lcom/hacksecure/messenger/data/remote/api/TicketRequest;", "(Lcom/hacksecure/messenger/data/remote/api/TicketRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface RelayApi {
    
    @retrofit2.http.POST(value = "/api/v1/ticket")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object requestTicket(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.data.remote.api.TicketRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.hacksecure.messenger.data.remote.api.TicketResponse> $completion);
    
    @retrofit2.http.POST(value = "/api/v1/presence")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object registerPresence(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.data.remote.api.PresenceRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.hacksecure.messenger.data.remote.api.PresenceResponse> $completion);
}