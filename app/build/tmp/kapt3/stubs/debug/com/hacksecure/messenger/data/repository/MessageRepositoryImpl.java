package com.hacksecure.messenger.data.repository;

import com.hacksecure.messenger.data.local.db.*;
import com.hacksecure.messenger.data.local.keystore.KeystoreManager;
import com.hacksecure.messenger.domain.crypto.IdentityHash;
import com.hacksecure.messenger.domain.crypto.IdentityKeyManager;
import com.hacksecure.messenger.domain.model.*;
import com.hacksecure.messenger.domain.repository.*;
import kotlinx.coroutines.flow.Flow;
import java.util.UUID;
import javax.inject.Inject;
import android.util.Base64;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0002J\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\bH\u0096@\u00a2\u0006\u0002\u0010\u000eJ\u000e\u0010\u000f\u001a\u00020\fH\u0096@\u00a2\u0006\u0002\u0010\u0010J\u001e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\r\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\bH\u0096@\u00a2\u0006\u0002\u0010\u0014J\u0018\u0010\u0015\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0016\u001a\u00020\bH\u0096@\u00a2\u0006\u0002\u0010\u000eJ\u001c\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00190\u00182\u0006\u0010\r\u001a\u00020\bH\u0016J\u001e\u0010\u001a\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u001b\u001a\u00020\u001cH\u0096@\u00a2\u0006\u0002\u0010\u001dJ\u0016\u0010\u001e\u001a\u00020\n*\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010\bH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006!"}, d2 = {"Lcom/hacksecure/messenger/data/repository/MessageRepositoryImpl;", "Lcom/hacksecure/messenger/domain/repository/MessageRepository;", "dao", "Lcom/hacksecure/messenger/data/local/db/MessageDao;", "keystoreManager", "Lcom/hacksecure/messenger/data/local/keystore/KeystoreManager;", "(Lcom/hacksecure/messenger/data/local/db/MessageDao;Lcom/hacksecure/messenger/data/local/keystore/KeystoreManager;)V", "buildHeaderJson", "", "message", "Lcom/hacksecure/messenger/domain/model/Message;", "deleteConversationMessages", "", "conversationId", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteExpiredMessages", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getMaxCounter", "", "senderHex", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getMessage", "id", "getMessages", "Lkotlinx/coroutines/flow/Flow;", "", "saveMessage", "plaintextBytes", "", "(Lcom/hacksecure/messenger/domain/model/Message;[BLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toDomain", "Lcom/hacksecure/messenger/data/local/db/MessageEntity;", "plaintext", "app_debug"})
public final class MessageRepositoryImpl implements com.hacksecure.messenger.domain.repository.MessageRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.data.local.db.MessageDao dao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.data.local.keystore.KeystoreManager keystoreManager = null;
    
    @javax.inject.Inject()
    public MessageRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.data.local.db.MessageDao dao, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.data.local.keystore.KeystoreManager keystoreManager) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.hacksecure.messenger.domain.model.Message>> getMessages(@org.jetbrains.annotations.NotNull()
    java.lang.String conversationId) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object saveMessage(@org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.model.Message message, @org.jetbrains.annotations.NotNull()
    byte[] plaintextBytes, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getMessage(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.hacksecure.messenger.domain.model.Message> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object deleteExpiredMessages(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object deleteConversationMessages(@org.jetbrains.annotations.NotNull()
    java.lang.String conversationId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getMaxCounter(@org.jetbrains.annotations.NotNull()
    java.lang.String conversationId, @org.jetbrains.annotations.NotNull()
    java.lang.String senderHex, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    private final com.hacksecure.messenger.domain.model.Message toDomain(com.hacksecure.messenger.data.local.db.MessageEntity $this$toDomain, java.lang.String plaintext) {
        return null;
    }
    
    private final java.lang.String buildHeaderJson(com.hacksecure.messenger.domain.model.Message message) {
        return null;
    }
}