// data/remote/api/RelayApi.kt
package com.hacksecure.messenger.data.remote.api

import retrofit2.http.Body
import retrofit2.http.POST

data class TicketRequest(val a_id: String, val b_id: String)
data class TicketResponse(val ticket_b64: String, val server_public_key_b64: String)
data class PresenceRequest(val identity_hash: String, val connection_token: String)
data class PresenceResponse(val online: Boolean, val token: String?)

interface RelayApi {
    @POST("/api/v1/ticket")
    suspend fun requestTicket(@Body request: TicketRequest): TicketResponse

    @POST("/api/v1/presence")
    suspend fun registerPresence(@Body request: PresenceRequest): PresenceResponse
}
