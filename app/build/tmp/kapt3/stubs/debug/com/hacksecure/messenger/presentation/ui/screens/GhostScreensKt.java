package com.hacksecure.messenger.presentation.ui.screens;

import androidx.compose.animation.*;
import androidx.compose.animation.core.*;
import androidx.compose.foundation.*;
import androidx.compose.foundation.layout.*;
import androidx.compose.foundation.lazy.*;
import androidx.compose.foundation.shape.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.*;
import androidx.compose.ui.graphics.Brush;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.unit.*;
import com.hacksecure.messenger.domain.model.ConnectionState;
import com.hacksecure.messenger.domain.model.GhostChatRequest;
import com.hacksecure.messenger.domain.model.GhostMessage;
import com.hacksecure.messenger.presentation.viewmodel.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000f\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0000\u001a\u0016\u0010\n\u001a\u00020\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\rH\u0003\u001aD\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u00102\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u000b0\u00152\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u000b0\rH\u0003\u001a8\u0010\u0017\u001a\u00020\u000b2\u0006\u0010\u0018\u001a\u00020\u00102\u0006\u0010\u0019\u001a\u00020\u00102\u0006\u0010\u001a\u001a\u00020\u00102\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u000b0\r2\b\b\u0002\u0010\u001c\u001a\u00020\u001dH\u0007\u001an\u0010\u001e\u001a\u00020\u000b2\u0006\u0010\u001f\u001a\u00020 2\u0012\u0010!\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u000b0\u00152\u0012\u0010\"\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u000b0\u00152\u0012\u0010#\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u000b0\u00152\u0012\u0010$\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u000b0\u00152\f\u0010%\u001a\b\u0012\u0004\u0012\u00020\u000b0\rH\u0003\u001am\u0010&\u001a\u00020\u000b2\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u000b0\r2K\u0010\'\u001aG\u0012\u0013\u0012\u00110\u0010\u00a2\u0006\f\b)\u0012\b\b*\u0012\u0004\b\b(\u0018\u0012\u0013\u0012\u00110\u0010\u00a2\u0006\f\b)\u0012\b\b*\u0012\u0004\b\b(\u0019\u0012\u0013\u0012\u00110\u0010\u00a2\u0006\f\b)\u0012\b\b*\u0012\u0004\b\b(\u001a\u0012\u0004\u0012\u00020\u000b0(2\b\b\u0002\u0010\u001c\u001a\u00020+H\u0007\u001a\u0010\u0010,\u001a\u00020\u000b2\u0006\u0010-\u001a\u00020.H\u0003\u001a:\u0010/\u001a\u00020\u000b2\u0006\u00100\u001a\u00020\u00102\u0006\u00101\u001a\u00020\u00122\u0012\u00102\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u000b0\u00152\f\u00103\u001a\b\u0012\u0004\u0012\u00020\u000b0\rH\u0003\u001a,\u00104\u001a\u00020\u000b2\u0006\u00105\u001a\u0002062\f\u00107\u001a\b\u0012\u0004\u0012\u00020\u000b0\r2\f\u00108\u001a\b\u0012\u0004\u0012\u00020\u000b0\rH\u0003\u001a\u0010\u00109\u001a\u00020\u00102\u0006\u0010:\u001a\u00020;H\u0002\"\u0010\u0010\u0000\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\u0003\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\u0004\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\u0005\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\u0006\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\u0007\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\b\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\t\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\u00a8\u0006<"}, d2 = {"GhostAmber", "Landroidx/compose/ui/graphics/Color;", "J", "GhostBackground", "GhostGreen", "GhostGreenDark", "GhostGreenDim", "GhostRed", "GhostSurface", "GhostSurfaceLight", "ChannelDestroyedOverlay", "", "onDismiss", "Lkotlin/Function0;", "CodenameEntryView", "codename", "", "isRegistering", "", "error", "onCodenameChange", "Lkotlin/Function1;", "onRegister", "GhostChatScreen", "channelId", "peerCodename", "anonymousId", "onBack", "viewModel", "Lcom/hacksecure/messenger/presentation/viewmodel/GhostChatViewModel;", "GhostLobbyContent", "state", "Lcom/hacksecure/messenger/presentation/viewmodel/GhostLobbyUiState;", "onSearchQueryChange", "onSendRequest", "onAcceptRequest", "onRejectRequest", "onLeave", "GhostLobbyScreen", "onNavigateToGhostChat", "Lkotlin/Function3;", "Lkotlin/ParameterName;", "name", "Lcom/hacksecure/messenger/presentation/viewmodel/GhostLobbyViewModel;", "GhostMessageBubble", "message", "Lcom/hacksecure/messenger/domain/model/GhostMessage;", "GhostMessageInput", "text", "enabled", "onTextChange", "onSend", "IncomingRequestCard", "request", "Lcom/hacksecure/messenger/domain/model/GhostChatRequest;", "onAccept", "onReject", "formatGhostTime", "ms", "", "Hacksecure_debug"})
public final class GhostScreensKt {
    private static final long GhostGreen = 0L;
    private static final long GhostGreenDark = 0L;
    private static final long GhostGreenDim = 0L;
    private static final long GhostBackground = 0L;
    private static final long GhostSurface = 0L;
    private static final long GhostSurfaceLight = 0L;
    private static final long GhostRed = 0L;
    private static final long GhostAmber = 0L;
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void GhostLobbyScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function3<? super java.lang.String, ? super java.lang.String, ? super java.lang.String, kotlin.Unit> onNavigateToGhostChat, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.presentation.viewmodel.GhostLobbyViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CodenameEntryView(java.lang.String codename, boolean isRegistering, java.lang.String error, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onCodenameChange, kotlin.jvm.functions.Function0<kotlin.Unit> onRegister) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void GhostLobbyContent(com.hacksecure.messenger.presentation.viewmodel.GhostLobbyUiState state, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onSearchQueryChange, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onSendRequest, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onAcceptRequest, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onRejectRequest, kotlin.jvm.functions.Function0<kotlin.Unit> onLeave) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void IncomingRequestCard(com.hacksecure.messenger.domain.model.GhostChatRequest request, kotlin.jvm.functions.Function0<kotlin.Unit> onAccept, kotlin.jvm.functions.Function0<kotlin.Unit> onReject) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void GhostChatScreen(@org.jetbrains.annotations.NotNull()
    java.lang.String channelId, @org.jetbrains.annotations.NotNull()
    java.lang.String peerCodename, @org.jetbrains.annotations.NotNull()
    java.lang.String anonymousId, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.presentation.viewmodel.GhostChatViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void GhostMessageBubble(com.hacksecure.messenger.domain.model.GhostMessage message) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void GhostMessageInput(java.lang.String text, boolean enabled, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onTextChange, kotlin.jvm.functions.Function0<kotlin.Unit> onSend) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ChannelDestroyedOverlay(kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    private static final java.lang.String formatGhostTime(long ms) {
        return null;
    }
}