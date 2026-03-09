package com.hacksecure.messenger.presentation.ui.screens;

import androidx.compose.animation.*;
import androidx.compose.foundation.*;
import androidx.compose.foundation.layout.*;
import androidx.compose.foundation.lazy.*;
import androidx.compose.foundation.shape.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.*;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.unit.*;
import com.hacksecure.messenger.domain.model.*;
import com.hacksecure.messenger.presentation.ui.theme.*;
import com.hacksecure.messenger.presentation.viewmodel.*;
import java.text.SimpleDateFormat;
import java.util.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000V\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001aP\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00012\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00060\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00060\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00060\u000bH\u0003\u001a(\u0010\u0011\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\u00012\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00060\u000b2\b\b\u0002\u0010\u0014\u001a\u00020\u0015H\u0007\u001a0\u0010\u0016\u001a\u00020\u00062\b\u0010\u0017\u001a\u0004\u0018\u00010\u00182\u0006\u0010\u0019\u001a\u00020\u00022\u0006\u0010\f\u001a\u00020\r2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00060\u000bH\u0003\u001a\u0010\u0010\u001a\u001a\u00020\u00062\u0006\u0010\u001b\u001a\u00020\u0002H\u0003\u001a&\u0010\u001c\u001a\u00020\u00062\u0006\u0010\u0019\u001a\u00020\u00022\u0006\u0010\u001d\u001a\u00020\u00012\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00060\u000bH\u0003\u001a2\u0010\u001f\u001a\u00020\u00062\u0006\u0010 \u001a\u00020\u000f2\u0012\u0010!\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00060\t2\f\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00060\u000bH\u0003\u001a\u0010\u0010#\u001a\u00020\u00062\u0006\u0010$\u001a\u00020%H\u0003\u001a\b\u0010&\u001a\u00020\u0006H\u0003\u001a\u0010\u0010\'\u001a\u00020\u00012\u0006\u0010(\u001a\u00020)H\u0002\u001a\u0011\u0010*\u001a\u00020+*\u00020\u0002H\u0003\u00a2\u0006\u0002\u0010,\"\u0018\u0010\u0000\u001a\u00020\u0001*\u00020\u00028BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004\u00a8\u0006-"}, d2 = {"label", "", "Lcom/hacksecure/messenger/domain/model/ConnectionState;", "getLabel", "(Lcom/hacksecure/messenger/domain/model/ConnectionState;)Ljava/lang/String;", "ChatInputBar", "", "text", "onTextChange", "Lkotlin/Function1;", "onSend", "Lkotlin/Function0;", "sessionEstablished", "", "expirySeconds", "", "onTimerClick", "ChatScreen", "contactId", "onBack", "viewModel", "Lcom/hacksecure/messenger/presentation/viewmodel/ChatViewModel;", "ChatTopBar", "contact", "Lcom/hacksecure/messenger/domain/model/Contact;", "connectionState", "ConnectionIndicator", "state", "ConnectionTroubleshootCard", "serverUrl", "onRetry", "ExpiryPickerSheet", "selectedSeconds", "onSelect", "onDismiss", "MessageBubble", "message", "Lcom/hacksecure/messenger/domain/model/Message;", "SessionStatusBanner", "formatCountdown", "seconds", "", "indicatorColor", "Landroidx/compose/ui/graphics/Color;", "(Lcom/hacksecure/messenger/domain/model/ConnectionState;)J", "app_debug"})
public final class ChatScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void ChatScreen(@org.jetbrains.annotations.NotNull()
    java.lang.String contactId, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.presentation.viewmodel.ChatViewModel viewModel) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    private static final void ChatTopBar(com.hacksecure.messenger.domain.model.Contact contact, com.hacksecure.messenger.domain.model.ConnectionState connectionState, boolean sessionEstablished, kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ConnectionIndicator(com.hacksecure.messenger.domain.model.ConnectionState state) {
    }
    
    private static final java.lang.String getLabel(com.hacksecure.messenger.domain.model.ConnectionState $this$label) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    private static final long indicatorColor(com.hacksecure.messenger.domain.model.ConnectionState $this$indicatorColor) {
        return 0L;
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SessionStatusBanner() {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void MessageBubble(com.hacksecure.messenger.domain.model.Message message) {
    }
    
    private static final java.lang.String formatCountdown(long seconds) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ChatInputBar(java.lang.String text, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onTextChange, kotlin.jvm.functions.Function0<kotlin.Unit> onSend, boolean sessionEstablished, int expirySeconds, kotlin.jvm.functions.Function0<kotlin.Unit> onTimerClick) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    private static final void ExpiryPickerSheet(int selectedSeconds, kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onSelect, kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ConnectionTroubleshootCard(com.hacksecure.messenger.domain.model.ConnectionState connectionState, java.lang.String serverUrl, kotlin.jvm.functions.Function0<kotlin.Unit> onRetry) {
    }
}