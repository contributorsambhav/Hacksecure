package com.hacksecure.messenger.domain.repository;

import com.hacksecure.messenger.domain.model.*;
import kotlinx.coroutines.flow.Flow;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\u00020\u0003H\u00a6@\u00a2\u0006\u0002\u0010\bJ\u001e\u0010\t\u001a\u00020\n2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\fJ\u0018\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u000f\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u00120\u00112\u0006\u0010\u0004\u001a\u00020\u0005H&J\u001e\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\u0015\u001a\u00020\u0016H\u00a6@\u00a2\u0006\u0002\u0010\u0017\u00a8\u0006\u0018"}, d2 = {"Lcom/hacksecure/messenger/domain/repository/MessageRepository;", "", "deleteConversationMessages", "", "conversationId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteExpiredMessages", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getMaxCounter", "", "senderHex", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getMessage", "Lcom/hacksecure/messenger/domain/model/Message;", "id", "getMessages", "Lkotlinx/coroutines/flow/Flow;", "", "saveMessage", "message", "plaintextBytes", "", "(Lcom/hacksecure/messenger/domain/model/Message;[BLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface MessageRepository {
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.hacksecure.messenger.domain.model.Message>> getMessages(@org.jetbrains.annotations.NotNull()
    java.lang.String conversationId);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveMessage(@org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.model.Message message, @org.jetbrains.annotations.NotNull()
    byte[] plaintextBytes, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getMessage(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.hacksecure.messenger.domain.model.Message> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteExpiredMessages(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteConversationMessages(@org.jetbrains.annotations.NotNull()
    java.lang.String conversationId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getMaxCounter(@org.jetbrains.annotations.NotNull()
    java.lang.String conversationId, @org.jetbrains.annotations.NotNull()
    java.lang.String senderHex, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
}