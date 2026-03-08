package com.hacksecure.messenger.data.local.db;

import android.content.Context;
import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b$\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001Bk\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\u000b\u0012\b\u0010\r\u001a\u0004\u0018\u00010\u000b\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\u0006\u0010\u0010\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0012J\t\u0010#\u001a\u00020\u0003H\u00c6\u0003J\t\u0010$\u001a\u00020\u000fH\u00c6\u0003J\t\u0010%\u001a\u00020\u000fH\u00c6\u0003J\t\u0010&\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\'\u001a\u00020\u0003H\u00c6\u0003J\t\u0010(\u001a\u00020\u0003H\u00c6\u0003J\t\u0010)\u001a\u00020\u0007H\u00c6\u0003J\t\u0010*\u001a\u00020\u0003H\u00c6\u0003J\t\u0010+\u001a\u00020\u0003H\u00c6\u0003J\t\u0010,\u001a\u00020\u000bH\u00c6\u0003J\t\u0010-\u001a\u00020\u000bH\u00c6\u0003J\u0010\u0010.\u001a\u0004\u0018\u00010\u000bH\u00c6\u0003\u00a2\u0006\u0002\u0010\u001aJ\u0088\u0001\u0010/\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u000b2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000b2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u000f2\b\b\u0002\u0010\u0011\u001a\u00020\u0003H\u00c6\u0001\u00a2\u0006\u0002\u00100J\u0013\u00101\u001a\u00020\u000f2\b\u00102\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u00103\u001a\u000204H\u0016J\t\u00105\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\f\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0015\u0010\r\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\t\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0016R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0016R\u0011\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u001eR\u0011\u0010\u0010\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u001eR\u0011\u0010\u0011\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0016R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u0016R\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0016R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0018\u00a8\u00066"}, d2 = {"Lcom/hacksecure/messenger/data/local/db/MessageEntity;", "", "id", "", "conversationId", "senderIdHex", "ciphertextBlob", "", "storageKeyAlias", "headerJson", "timestampMs", "", "counter", "expiryMs", "isDecryptable", "", "isOutgoing", "messageType", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[BLjava/lang/String;Ljava/lang/String;JJLjava/lang/Long;ZZLjava/lang/String;)V", "getCiphertextBlob", "()[B", "getConversationId", "()Ljava/lang/String;", "getCounter", "()J", "getExpiryMs", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getHeaderJson", "getId", "()Z", "getMessageType", "getSenderIdHex", "getStorageKeyAlias", "getTimestampMs", "component1", "component10", "component11", "component12", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[BLjava/lang/String;Ljava/lang/String;JJLjava/lang/Long;ZZLjava/lang/String;)Lcom/hacksecure/messenger/data/local/db/MessageEntity;", "equals", "other", "hashCode", "", "toString", "app_debug"})
@androidx.room.Entity(tableName = "messages", indices = {@androidx.room.Index(value = {"conversationId"}), @androidx.room.Index(value = {"expiryMs"}), @androidx.room.Index(value = {"counter"})})
public final class MessageEntity {
    @androidx.room.PrimaryKey()
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String conversationId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String senderIdHex = null;
    @org.jetbrains.annotations.NotNull()
    private final byte[] ciphertextBlob = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String storageKeyAlias = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String headerJson = null;
    private final long timestampMs = 0L;
    private final long counter = 0L;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long expiryMs = null;
    private final boolean isDecryptable = false;
    private final boolean isOutgoing = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String messageType = null;
    
    public MessageEntity(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String conversationId, @org.jetbrains.annotations.NotNull()
    java.lang.String senderIdHex, @org.jetbrains.annotations.NotNull()
    byte[] ciphertextBlob, @org.jetbrains.annotations.NotNull()
    java.lang.String storageKeyAlias, @org.jetbrains.annotations.NotNull()
    java.lang.String headerJson, long timestampMs, long counter, @org.jetbrains.annotations.Nullable()
    java.lang.Long expiryMs, boolean isDecryptable, boolean isOutgoing, @org.jetbrains.annotations.NotNull()
    java.lang.String messageType) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getConversationId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSenderIdHex() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final byte[] getCiphertextBlob() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getStorageKeyAlias() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getHeaderJson() {
        return null;
    }
    
    public final long getTimestampMs() {
        return 0L;
    }
    
    public final long getCounter() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getExpiryMs() {
        return null;
    }
    
    public final boolean isDecryptable() {
        return false;
    }
    
    public final boolean isOutgoing() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getMessageType() {
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
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    public final boolean component10() {
        return false;
    }
    
    public final boolean component11() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component12() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final byte[] component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    public final long component7() {
        return 0L;
    }
    
    public final long component8() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.hacksecure.messenger.data.local.db.MessageEntity copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String conversationId, @org.jetbrains.annotations.NotNull()
    java.lang.String senderIdHex, @org.jetbrains.annotations.NotNull()
    byte[] ciphertextBlob, @org.jetbrains.annotations.NotNull()
    java.lang.String storageKeyAlias, @org.jetbrains.annotations.NotNull()
    java.lang.String headerJson, long timestampMs, long counter, @org.jetbrains.annotations.Nullable()
    java.lang.Long expiryMs, boolean isDecryptable, boolean isOutgoing, @org.jetbrains.annotations.NotNull()
    java.lang.String messageType) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}