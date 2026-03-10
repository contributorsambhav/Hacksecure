// data/repository/GhostRepositoryImpl.kt
// Implements GhostRepository using REST API calls — NO database persistence.
package com.hacksecure.messenger.data.repository

import com.hacksecure.messenger.data.remote.ServerConfig
import com.hacksecure.messenger.data.remote.api.*
import com.hacksecure.messenger.domain.model.*
import com.hacksecure.messenger.domain.repository.GhostRepository
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GhostRepositoryImpl @Inject constructor(
    private val relayApi: RelayApi,
    private val serverConfig: ServerConfig
) : GhostRepository {

    private val apiBase get() = serverConfig.apiBaseUrl

    override suspend fun register(codename: String): GhostIdentity {
        val response = relayApi.ghostRegister(
            url = "$apiBase/api/v1/ghost/register",
            request = GhostRegisterRequest(codename)
        )
        return GhostIdentity(
            codename = response.codename,
            ghostToken = response.ghostToken
        )
    }

    override suspend fun searchOnlineUsers(query: String, ghostToken: String): List<String> {
        val response = relayApi.ghostSearch(
            url = "$apiBase/api/v1/ghost/search",
            token = ghostToken,
            query = query
        )
        return response.results
    }

    override suspend fun sendChatRequest(targetCodename: String, ghostToken: String): String? {
        val response = relayApi.ghostRequest(
            url = "$apiBase/api/v1/ghost/request",
            token = ghostToken,
            request = GhostSendRequest(targetCodename)
        )
        return if (response.sent) response.requestId else null
    }

    override suspend fun pollRequests(ghostToken: String): Pair<List<GhostChatRequest>, List<GhostChannel>> {
        val response = relayApi.ghostPoll(
            url = "$apiBase/api/v1/ghost/poll",
            token = ghostToken
        )
        val requests = response.requests.map { item ->
            GhostChatRequest(
                requestId = item.requestId,
                fromCodename = item.fromCodename,
                timestamp = item.timestamp
            )
        }
        val channels = response.channels.map { item ->
            val anonymousId = generateAnonymousId(item.channelId, item.peerCodename)
            GhostChannel(
                channelId = item.channelId,
                peerCodename = item.peerCodename,
                anonymousId = anonymousId
            )
        }
        return requests to channels
    }

    override suspend fun acceptRequest(requestId: String, ghostToken: String): GhostChannel? {
        val response = relayApi.ghostRespond(
            url = "$apiBase/api/v1/ghost/respond",
            token = ghostToken,
            request = GhostRespondRequest(requestId = requestId, accept = true)
        )
        if (!response.accepted || response.channelId == null) return null
        val anonymousId = generateAnonymousId(response.channelId, response.peerCodename ?: "UNKNOWN")
        return GhostChannel(
            channelId = response.channelId,
            peerCodename = response.peerCodename ?: "UNKNOWN",
            anonymousId = anonymousId
        )
    }

    override suspend fun rejectRequest(requestId: String, ghostToken: String): Boolean {
        val response = relayApi.ghostRespond(
            url = "$apiBase/api/v1/ghost/respond",
            token = ghostToken,
            request = GhostRespondRequest(requestId = requestId, accept = false)
        )
        return !response.accepted
    }

    override suspend fun leave(ghostToken: String) {
        try {
            relayApi.ghostLeave(
                url = "$apiBase/api/v1/ghost/leave",
                token = ghostToken
            )
        } catch (_: Exception) { /* best-effort cleanup */ }
    }

    /** Generates a deterministic anonymous ID from channel + peer info. */
    private fun generateAnonymousId(channelId: String, peerCodename: String): String {
        val seed = "$channelId:$peerCodename:${System.currentTimeMillis() / 60_000}"
        val hash = MessageDigest.getInstance("SHA-256").digest(seed.toByteArray())
        return "GHOST-" + hash.take(4).joinToString("") { "%02X".format(it) }
    }
}
