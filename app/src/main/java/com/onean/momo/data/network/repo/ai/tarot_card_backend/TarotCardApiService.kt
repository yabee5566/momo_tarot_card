package com.onean.momo.data.network.repo.ai.tarot_card_backend

import com.onean.momo.data.network.request.ProvideDetailRequest
import com.onean.momo.data.network.request.SetupTopicRequest
import com.onean.momo.data.network.request.StartChatRequest
import com.onean.momo.data.network.response.StartChatResponse
import com.onean.momo.data.network.response.TarotTellerResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST

interface TarotCardApiService {
    @POST("/start_chat")
    suspend fun startChat(@Body request: StartChatRequest): StartChatResponse

    // FIXME: fill in session using http interceptor
    @POST("/set_topic")
    suspend fun setTopic(
        @Header("session-id") sessionId: String,
        @Body request: SetupTopicRequest
    ): TarotTellerResponse

    // FIXME: fill in session using http interceptor
    @POST("/provide_detail")
    suspend fun provideDetail(
        @Header("session-id") sessionId: String,
        @Body request: ProvideDetailRequest
    ): TarotTellerResponse

    // FIXME: fill in session using http interceptor
    @DELETE("/end_session")
    suspend fun endSession(
        @Header("session-id") sessionId: String,
    )
}
