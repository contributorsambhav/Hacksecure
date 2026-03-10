// data/remote/api/RelayApi.kt
package com.hacksecure.messenger.data.remote.api

import retrofit2.http.*

data class TicketRequest(val a_id: String, val b_id: String)
data class TicketResponse(val ticket_b64: String, val server_public_key_b64: String)
data class PresenceRequest(val identity_hash: String, val connection_token: String)
data class PresenceResponse(val online: Boolean, val token: String?)

// Ghost Mode DTOs
data class GhostRegisterRequest(val codename: String)
data class GhostRegisterResponse(val ghostToken: String, val codename: String)
data class GhostSendRequest(val targetCodename: String)
data class GhostSendResponse(val requestId: String?, val sent: Boolean)
data class GhostRespondRequest(val requestId: String, val accept: Boolean)
data class GhostRespondResponse(
    val accepted: Boolean,
    val channelId: String? = null,
    val peerCodename: String? = null
)
data class GhostPollRequestItem(val requestId: String, val fromCodename: String, val timestamp: Long)
data class GhostPollChannelItem(val channelId: String, val peerCodename: String)
data class GhostPollResponse(
    val requests: List<GhostPollRequestItem> = emptyList(),
    val channels: List<GhostPollChannelItem> = emptyList()
)
data class GhostSearchResponse(val results: List<String> = emptyList())

interface RelayApi {
    // Full URL supplied at call-time so the server address can change at runtime.
    @POST
    suspend fun requestTicket(@Url url: String, @Body request: TicketRequest): TicketResponse

    @POST
    suspend fun registerPresence(@Url url: String, @Body request: PresenceRequest): PresenceResponse

    // Ghost Mode API
    @POST
    suspend fun ghostRegister(@Url url: String, @Body request: GhostRegisterRequest): GhostRegisterResponse

    @GET
    suspend fun ghostSearch(@Url url: String, @Header("X-Ghost-Token") token: String, @Query("q") query: String): GhostSearchResponse

    @POST
    suspend fun ghostRequest(@Url url: String, @Header("X-Ghost-Token") token: String, @Body request: GhostSendRequest): GhostSendResponse

    @POST
    suspend fun ghostRespond(@Url url: String, @Header("X-Ghost-Token") token: String, @Body request: GhostRespondRequest): GhostRespondResponse

    @GET
    suspend fun ghostPoll(@Url url: String, @Header("X-Ghost-Token") token: String): GhostPollResponse

    @POST
    suspend fun ghostLeave(@Url url: String, @Header("X-Ghost-Token") token: String, @Body body: Map<String, String> = emptyMap()): Unit
}
