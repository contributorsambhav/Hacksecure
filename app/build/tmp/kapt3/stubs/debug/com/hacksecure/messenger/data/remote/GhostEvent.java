package com.hacksecure.messenger.data.remote;

import com.hacksecure.messenger.data.remote.websocket.RelayEvent;
import com.hacksecure.messenger.data.remote.websocket.RelayWebSocketClient;
import com.hacksecure.messenger.domain.crypto.*;
import com.hacksecure.messenger.domain.model.ConnectionState;
import kotlinx.coroutines.*;
import kotlinx.coroutines.flow.SharedFlow;
import kotlinx.coroutines.flow.StateFlow;
import okhttp3.OkHttpClient;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0004\u0003\u0004\u0005\u0006B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0004\u0007\b\t\n\u00a8\u0006\u000b"}, d2 = {"Lcom/hacksecure/messenger/data/remote/GhostEvent;", "", "()V", "ChannelDestroyed", "Error", "MessageReceived", "SessionReady", "Lcom/hacksecure/messenger/data/remote/GhostEvent$ChannelDestroyed;", "Lcom/hacksecure/messenger/data/remote/GhostEvent$Error;", "Lcom/hacksecure/messenger/data/remote/GhostEvent$MessageReceived;", "Lcom/hacksecure/messenger/data/remote/GhostEvent$SessionReady;", "Hacksecure_debug"})
public abstract class GhostEvent {
    
    private GhostEvent() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/hacksecure/messenger/data/remote/GhostEvent$ChannelDestroyed;", "Lcom/hacksecure/messenger/data/remote/GhostEvent;", "()V", "Hacksecure_debug"})
    public static final class ChannelDestroyed extends com.hacksecure.messenger.data.remote.GhostEvent {
        @org.jetbrains.annotations.NotNull()
        public static final com.hacksecure.messenger.data.remote.GhostEvent.ChannelDestroyed INSTANCE = null;
        
        private ChannelDestroyed() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2 = {"Lcom/hacksecure/messenger/data/remote/GhostEvent$Error;", "Lcom/hacksecure/messenger/data/remote/GhostEvent;", "message", "", "(Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "Hacksecure_debug"})
    public static final class Error extends com.hacksecure.messenger.data.remote.GhostEvent {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String message = null;
        
        public Error(@org.jetbrains.annotations.NotNull()
        java.lang.String message) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getMessage() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.hacksecure.messenger.data.remote.GhostEvent.Error copy(@org.jetbrains.annotations.NotNull()
        java.lang.String message) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0012\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0015"}, d2 = {"Lcom/hacksecure/messenger/data/remote/GhostEvent$MessageReceived;", "Lcom/hacksecure/messenger/data/remote/GhostEvent;", "messageId", "", "packetBytes", "", "(Ljava/lang/String;[B)V", "getMessageId", "()Ljava/lang/String;", "getPacketBytes", "()[B", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "", "toString", "Hacksecure_debug"})
    public static final class MessageReceived extends com.hacksecure.messenger.data.remote.GhostEvent {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String messageId = null;
        @org.jetbrains.annotations.NotNull()
        private final byte[] packetBytes = null;
        
        public MessageReceived(@org.jetbrains.annotations.NotNull()
        java.lang.String messageId, @org.jetbrains.annotations.NotNull()
        byte[] packetBytes) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getMessageId() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] getPacketBytes() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.hacksecure.messenger.data.remote.GhostEvent.MessageReceived copy(@org.jetbrains.annotations.NotNull()
        java.lang.String messageId, @org.jetbrains.annotations.NotNull()
        byte[] packetBytes) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/hacksecure/messenger/data/remote/GhostEvent$SessionReady;", "Lcom/hacksecure/messenger/data/remote/GhostEvent;", "()V", "Hacksecure_debug"})
    public static final class SessionReady extends com.hacksecure.messenger.data.remote.GhostEvent {
        @org.jetbrains.annotations.NotNull()
        public static final com.hacksecure.messenger.data.remote.GhostEvent.SessionReady INSTANCE = null;
        
        private SessionReady() {
        }
    }
}