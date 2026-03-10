// Navigation.kt + MainActivity.kt
package com.hacksecure.messenger

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.hacksecure.messenger.presentation.ui.screens.*
import com.hacksecure.messenger.presentation.ui.theme.HackSecureTheme
import com.hacksecure.messenger.worker.MessageExpiryWorker
import dagger.hilt.android.AndroidEntryPoint

// ══════════════════════════════════════════════════════════════════════════════
// NAVIGATION ROUTES
// ══════════════════════════════════════════════════════════════════════════════
object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val QR_DISPLAY = "qr_display"
    const val QR_SCAN = "qr_scan"
    const val CONTACT_CONFIRM = "contact_confirm/{pubKeyB64}"
    const val CHAT = "chat/{contactId}"
    const val SETTINGS = "settings"
    const val GHOST_LOBBY = "ghost_lobby"
    const val GHOST_CHAT = "ghost_chat/{channelId}/{peerCodename}/{anonymousId}"

    fun contactConfirm(pubKeyB64: String) = "contact_confirm/$pubKeyB64"
    fun chat(contactId: String) = "chat/$contactId"
    fun ghostChat(channelId: String, peerCodename: String, anonymousId: String) =
        "ghost_chat/${java.net.URLEncoder.encode(channelId, "UTF-8")}/${java.net.URLEncoder.encode(peerCodename, "UTF-8")}/${java.net.URLEncoder.encode(anonymousId, "UTF-8")}"
}

// ══════════════════════════════════════════════════════════════════════════════
// MAIN ACTIVITY
// ══════════════════════════════════════════════════════════════════════════════
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        applyScreenshotBlockingPreference()
        MessageExpiryWorker.schedule(this)
        MessageExpiryWorker.runOnce(this)

        setContent {
            HackSecureTheme {
                HackSecureNavGraph()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-apply screenshot blocking each time activity resumes so settings changes take effect
        applyScreenshotBlockingPreference()
    }

    private fun applyScreenshotBlockingPreference() {
        val prefs = getSharedPreferences("hacksecure_settings", MODE_PRIVATE)
        val blockingEnabled = prefs.getBoolean("screenshot_blocking", true)
        if (blockingEnabled) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}

@Composable
fun HackSecureNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        // ── Splash ──────────────────────────────────────────────────────────
        composable(Routes.SPLASH) {
            SplashScreen(
                onReady = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ── Home ─────────────────────────────────────────────────────────────
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToQrDisplay = { navController.navigate(Routes.QR_DISPLAY) },
                onNavigateToQrScan = { navController.navigate(Routes.QR_SCAN) },
                onNavigateToChat = { contactId -> navController.navigate(Routes.chat(contactId)) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                onNavigateToGhostMode = { navController.navigate(Routes.GHOST_LOBBY) }
            )
        }

        // ── QR Display ───────────────────────────────────────────────────────
        composable(Routes.QR_DISPLAY) {
            QrDisplayScreen(onBack = { navController.popBackStack() })
        }

        // ── QR Scan ──────────────────────────────────────────────────────────
        composable(Routes.QR_SCAN) {
            QrScanScreen(
                onBack = { navController.popBackStack() },
                onScanSuccess = { pubKeyB64 ->
                    navController.navigate(Routes.contactConfirm(pubKeyB64))
                }
            )
        }

        // ── Contact Confirmation ─────────────────────────────────────────────
        composable(
            Routes.CONTACT_CONFIRM,
            arguments = listOf(navArgument("pubKeyB64") { type = NavType.StringType })
        ) { backStackEntry ->
            val pubKeyB64 = backStackEntry.arguments?.getString("pubKeyB64") ?: ""
            ContactConfirmScreen(
                publicKeyB64 = pubKeyB64,
                onBack = { navController.popBackStack() },
                onConfirmed = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        // ── Chat ─────────────────────────────────────────────────────────────
        composable(
            Routes.CHAT,
            arguments = listOf(navArgument("contactId") { type = NavType.StringType })
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getString("contactId") ?: ""
            ChatScreen(
                contactId = contactId,
                onBack = { navController.popBackStack() }
            )
        }

        // ── Settings ─────────────────────────────────────────────────────────
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToQrDisplay = { navController.navigate(Routes.QR_DISPLAY) }
            )
        }

        // ── Ghost Mode ──────────────────────────────────────────────────────
        composable(Routes.GHOST_LOBBY) {
            GhostLobbyScreen(
                onBack = { navController.popBackStack() },
                onNavigateToGhostChat = { channelId, peerCodename, anonymousId ->
                    navController.navigate(Routes.ghostChat(channelId, peerCodename, anonymousId)) {
                        popUpTo(Routes.GHOST_LOBBY) { inclusive = true }
                    }
                }
            )
        }

        composable(
            Routes.GHOST_CHAT,
            arguments = listOf(
                navArgument("channelId") { type = NavType.StringType },
                navArgument("peerCodename") { type = NavType.StringType },
                navArgument("anonymousId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val channelId = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("channelId") ?: "", "UTF-8"
            )
            val peerCodename = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("peerCodename") ?: "", "UTF-8"
            )
            val anonymousId = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("anonymousId") ?: "", "UTF-8"
            )
            GhostChatScreen(
                channelId = channelId,
                peerCodename = peerCodename,
                anonymousId = anonymousId,
                onBack = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}
