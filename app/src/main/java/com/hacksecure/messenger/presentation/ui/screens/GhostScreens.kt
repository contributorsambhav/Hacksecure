// presentation/ui/screens/GhostScreens.kt
// Ghost Mode UI — Intelligence-grade zero-trace chat
package com.hacksecure.messenger.presentation.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.hacksecure.messenger.domain.model.ConnectionState
import com.hacksecure.messenger.domain.model.GhostChatRequest
import com.hacksecure.messenger.domain.model.GhostMessage
import com.hacksecure.messenger.presentation.viewmodel.*
import kotlinx.coroutines.flow.collectLatest

// ── Ghost Theme Colors ──────────────────────────────────────────────────────
private val GhostGreen = Color(0xFF00FF41)
private val GhostGreenDark = Color(0xFF00CC33)
private val GhostGreenDim = Color(0xFF003311)
private val GhostBackground = Color(0xFF0A0A0A)
private val GhostSurface = Color(0xFF111111)
private val GhostSurfaceLight = Color(0xFF1A1A1A)
private val GhostRed = Color(0xFFFF3333)
private val GhostAmber = Color(0xFFFFAA00)

// ══════════════════════════════════════════════════════════════════════════════
// GHOST LOBBY SCREEN
// ══════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GhostLobbyScreen(
    onBack: () -> Unit,
    onNavigateToGhostChat: (channelId: String, peerCodename: String, anonymousId: String) -> Unit,
    viewModel: GhostLobbyViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is GhostLobbyEvent.NavigateToChat -> {
                    onNavigateToGhostChat(event.channelId, event.peerCodename, event.anonymousId)
                }
                is GhostLobbyEvent.ShowError -> { /* snackbar TODO */ }
            }
        }
    }

    Scaffold(
        containerColor = GhostBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("👻", fontSize = 20.sp)
                        Text("GHOST MODE", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = GhostGreen)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.leaveGhostMode()
                        onBack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = GhostGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GhostSurface,
                    titleContentColor = GhostGreen
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(GhostBackground, Color(0xFF050A05))
                    )
                )
        ) {
            if (!state.isRegistered) {
                CodenameEntryView(
                    codename = state.codename,
                    isRegistering = state.isRegistering,
                    error = state.error,
                    onCodenameChange = viewModel::updateCodename,
                    onRegister = viewModel::register
                )
            } else {
                GhostLobbyContent(
                    state = state,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onSendRequest = viewModel::sendRequest,
                    onAcceptRequest = viewModel::acceptRequest,
                    onRejectRequest = viewModel::rejectRequest,
                    onLeave = {
                        viewModel.leaveGhostMode()
                        onBack()
                    }
                )
            }
        }
    }
}

@Composable
private fun CodenameEntryView(
    codename: String,
    isRegistering: Boolean,
    error: String?,
    onCodenameChange: (String) -> Unit,
    onRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Pulsing ghost icon
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.5f, targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "pulse_alpha"
        )

        Text("👻", fontSize = 72.sp, modifier = Modifier.alpha(alpha))

        Spacer(Modifier.height(24.dp))

        Text(
            "ENTER YOUR CODENAME",
            color = GhostGreen,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "This name is temporary.\nIt will be destroyed after connection.",
            color = GhostGreen.copy(alpha = 0.6f),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = codename,
            onValueChange = onCodenameChange,
            singleLine = true,
            placeholder = { Text("CODENAME", color = GhostGreen.copy(alpha = 0.3f), fontFamily = FontFamily.Monospace) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = GhostGreen,
                unfocusedTextColor = GhostGreen,
                cursorColor = GhostGreen,
                focusedBorderColor = GhostGreen,
                unfocusedBorderColor = GhostGreenDim,
                focusedContainerColor = GhostSurface,
                unfocusedContainerColor = GhostSurface
            ),
            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth()
        )

        error?.let { msg ->
            Spacer(Modifier.height(8.dp))
            Text(msg, color = GhostRed, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onRegister,
            enabled = codename.trim().length >= 2 && !isRegistering,
            colors = ButtonDefaults.buttonColors(
                containerColor = GhostGreenDark,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            if (isRegistering) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("GO LIVE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = GhostSurface.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Warning, null, tint = GhostAmber, modifier = Modifier.size(16.dp))
                Text(
                    "Zero-trace mode. No messages are stored.\nDisconnect = total evidence destruction.",
                    color = GhostAmber.copy(alpha = 0.8f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun GhostLobbyContent(
    state: GhostLobbyUiState,
    onSearchQueryChange: (String) -> Unit,
    onSendRequest: (String) -> Unit,
    onAcceptRequest: (String) -> Unit,
    onRejectRequest: (String) -> Unit,
    onLeave: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Live status bar
        Card(
            colors = CardDefaults.cardColors(containerColor = GhostSurface),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val pulse = rememberInfiniteTransition(label = "live_pulse")
                    val dotAlpha by pulse.animateFloat(
                        initialValue = 0.3f, targetValue = 1f,
                        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "dot"
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(GhostGreen.copy(alpha = dotAlpha))
                    )
                    Text(
                        "LIVE: ${state.registeredCodename}",
                        color = GhostGreen,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                TextButton(onClick = onLeave) {
                    Text("LEAVE", color = GhostRed, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Search bar
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchQueryChange,
            singleLine = true,
            placeholder = { Text("Search agents...", color = GhostGreen.copy(alpha = 0.3f), fontFamily = FontFamily.Monospace) },
            leadingIcon = { Icon(Icons.Filled.Search, null, tint = GhostGreen.copy(alpha = 0.5f)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = GhostGreen,
                unfocusedTextColor = GhostGreen,
                cursorColor = GhostGreen,
                focusedBorderColor = GhostGreen,
                unfocusedBorderColor = GhostGreenDim,
                focusedContainerColor = GhostSurface,
                unfocusedContainerColor = GhostSurface
            ),
            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            // Search results
            if (state.searchResults.isNotEmpty()) {
                item {
                    Text("ONLINE AGENTS", color = GhostGreen.copy(alpha = 0.5f), fontFamily = FontFamily.Monospace, fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 8.dp))
                }
                items(state.searchResults) { codename ->
                    val alreadySent = codename in state.sentRequests
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GhostSurfaceLight),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier.size(8.dp).clip(CircleShape).background(GhostGreen)
                                )
                                Text(codename, color = GhostGreen, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium)
                            }
                            Button(
                                onClick = { onSendRequest(codename) },
                                enabled = !alreadySent,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (alreadySent) GhostGreenDim else GhostGreenDark,
                                    contentColor = Color.Black,
                                    disabledContainerColor = GhostGreenDim,
                                    disabledContentColor = GhostGreen.copy(alpha = 0.5f)
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    if (alreadySent) "SENT" else "CONNECT",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Incoming requests
            if (state.incomingRequests.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(16.dp))
                    Text("INCOMING REQUESTS", color = GhostAmber.copy(alpha = 0.7f), fontFamily = FontFamily.Monospace, fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 8.dp))
                }
                items(state.incomingRequests) { request ->
                    IncomingRequestCard(
                        request = request,
                        onAccept = { onAcceptRequest(request.requestId) },
                        onReject = { onRejectRequest(request.requestId) }
                    )
                }
            }

            // Empty state
            if (state.searchResults.isEmpty() && state.incomingRequests.isEmpty() && state.searchQuery.isBlank()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("👻", fontSize = 48.sp, modifier = Modifier.alpha(0.3f))
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Search for agents or wait\nfor incoming connection requests",
                            color = GhostGreen.copy(alpha = 0.4f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IncomingRequestCard(
    request: GhostChatRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GhostSurfaceLight),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, GhostAmber.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(request.fromCodename, color = GhostAmber, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Text("wants to connect", color = GhostGreen.copy(alpha = 0.5f), fontFamily = FontFamily.Monospace, fontSize = 11.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GhostRed),
                    border = BorderStroke(1.dp, GhostRed.copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("✕", fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                }
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = GhostGreenDark, contentColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("ACCEPT", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// GHOST CHAT SCREEN
// ══════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GhostChatScreen(
    channelId: String,
    peerCodename: String,
    anonymousId: String,
    onBack: () -> Unit,
    viewModel: GhostChatViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(channelId) {
        viewModel.initChannel(channelId, peerCodename, anonymousId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is GhostChatEvent.ChannelDestroyed -> { /* handled by UI state */ }
                is GhostChatEvent.ShowError -> { /* snackbar TODO */ }
            }
        }
    }

    // Channel destroyed overlay
    if (state.isChannelDestroyed) {
        ChannelDestroyedOverlay(onDismiss = onBack)
        return
    }

    Scaffold(
        containerColor = GhostBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            state.peerAnonymousId.ifBlank { "CONNECTING..." },
                            color = GhostGreen,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            when (state.connectionState) {
                                ConnectionState.CONNECTING -> "Establishing secure channel..."
                                ConnectionState.CONNECTED_RELAY -> if (state.sessionEstablished) "🔒 E2E Encrypted" else "Key exchange in progress..."
                                ConnectionState.DISCONNECTED -> "Disconnected"
                                else -> "Connecting..."
                            },
                            color = GhostGreen.copy(alpha = 0.5f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.disconnect()
                        onBack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = GhostGreen)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.disconnect()
                        onBack()
                    }) {
                        Icon(Icons.Filled.Close, "End Channel", tint = GhostRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GhostSurface)
            )
        },
        bottomBar = {
            GhostMessageInput(
                text = state.inputText,
                enabled = state.sessionEstablished && !state.isChannelDestroyed,
                onTextChange = viewModel::updateInputText,
                onSend = viewModel::sendMessage
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(listOf(GhostBackground, Color(0xFF050A05)))
                )
        ) {
            // Zero-trace warning banner
            Card(
                colors = CardDefaults.cardColors(containerColor = GhostRed.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Warning, null, tint = GhostRed.copy(alpha = 0.7f), modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "ZERO-TRACE · Messages self-destruct on disconnect",
                        color = GhostRed.copy(alpha = 0.7f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Messages
            val listState = rememberLazyListState()
            LaunchedEffect(state.messages.size) {
                if (state.messages.isNotEmpty()) {
                    listState.animateScrollToItem(state.messages.size - 1)
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.messages, key = { it.id }) { message ->
                    GhostMessageBubble(message)
                }
            }
        }
    }
}

@Composable
private fun GhostMessageBubble(message: GhostMessage) {
    val bubbleColor = if (message.isOutgoing) GhostGreenDark.copy(alpha = 0.15f) else GhostSurfaceLight
    val alignment = if (message.isOutgoing) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            shape = RoundedCornerShape(
                topStart = 12.dp, topEnd = 12.dp,
                bottomStart = if (message.isOutgoing) 12.dp else 4.dp,
                bottomEnd = if (message.isOutgoing) 4.dp else 12.dp
            ),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    message.content,
                    color = GhostGreen,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    formatGhostTime(message.timestampMs),
                    color = GhostGreen.copy(alpha = 0.4f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun GhostMessageInput(
    text: String,
    enabled: Boolean,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(color = GhostSurface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                enabled = enabled,
                placeholder = {
                    Text(
                        if (enabled) "Type message..." else "Waiting for secure session...",
                        color = GhostGreen.copy(alpha = 0.3f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = GhostGreen,
                    unfocusedTextColor = GhostGreen,
                    disabledTextColor = GhostGreen.copy(alpha = 0.3f),
                    cursorColor = GhostGreen,
                    focusedBorderColor = GhostGreenDim,
                    unfocusedBorderColor = GhostGreenDim,
                    disabledBorderColor = GhostGreenDim.copy(alpha = 0.3f),
                    focusedContainerColor = GhostBackground,
                    unfocusedContainerColor = GhostBackground,
                    disabledContainerColor = GhostBackground
                ),
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                maxLines = 3
            )
            FilledIconButton(
                onClick = onSend,
                enabled = enabled && text.isNotBlank(),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = GhostGreenDark,
                    contentColor = Color.Black,
                    disabledContainerColor = GhostGreenDim,
                    disabledContentColor = GhostGreen.copy(alpha = 0.3f)
                ),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(Icons.Filled.Send, "Send", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun ChannelDestroyedOverlay(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GhostBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val pulseTransition = rememberInfiniteTransition(label = "destroy_pulse")
            val alpha by pulseTransition.animateFloat(
                initialValue = 0.3f, targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse), label = "d_alpha"
            )

            Text("💀", fontSize = 64.sp, modifier = Modifier.alpha(alpha))

            Text(
                "CHANNEL DESTROYED",
                color = GhostRed,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Text(
                "All messages have been purged.\nNo evidence remains.",
                color = GhostRed.copy(alpha = 0.6f),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GhostSurface,
                    contentColor = GhostGreen
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("RETURN TO BASE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun formatGhostTime(ms: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - ms
    return when {
        diff < 60_000 -> "now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        else -> {
            val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            sdf.format(java.util.Date(ms))
        }
    }
}
