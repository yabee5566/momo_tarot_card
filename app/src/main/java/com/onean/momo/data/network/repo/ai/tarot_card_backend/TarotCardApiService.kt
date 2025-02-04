package com.onean.momo.data.network.repo.ai.tarot_card_backend

import com.onean.momo.data.network.request.ReplyQuestionRequest
import com.onean.momo.data.network.request.SetupQuestionCategoryRequest
import com.onean.momo.data.network.response.StartChatResponse
import com.onean.momo.data.network.response.TarotTellerResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST

interface TarotCardApiService {
    @POST("/start_chat")
    suspend fun startChat(): StartChatResponse

    // FIXME: fill in session using http interceptor
    @POST("/setup_question_category")
    suspend fun setupQuestionCategory(
        @Header("session-id") sessionId: String,
        @Body request: SetupQuestionCategoryRequest
    ): TarotTellerResponse

    // FIXME: fill in session using http interceptor
    @POST("/reply_question")
    suspend fun replyQuestion(
        @Header("session-id") sessionId: String,
        @Body request: ReplyQuestionRequest
    ): TarotTellerResponse

    // FIXME: fill in session using http interceptor
    @DELETE("/end_session")
    suspend fun endSession(
        @Header("session-id") sessionId: String,
    )
}
