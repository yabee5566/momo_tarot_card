package com.onean.momo.data.network.repo.ai

import android.content.Context
import com.onean.momo.data.network.exception.SessionNotFoundError
import com.onean.momo.data.network.repo.ai.tarot_card_backend.TarotCardApiService
import com.onean.momo.data.network.request.ProvideDetailRequest
import com.onean.momo.data.network.request.SetupTopicRequest
import com.onean.momo.data.network.request.StartChatRequest
import com.onean.momo.data.network.response.TarotCardDetail
import com.onean.momo.data.network.response.TarotTellerResponse
import com.onean.momo.ext.fetchUntil
import com.onean.momo.ext.localeString
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

// FIXME: use enum and data class rather than all String going around
interface TarotAiRepo {
    suspend fun startChat()
    suspend fun setTopic(topic: String): TarotTellerResponse
    suspend fun provideDetail(chat: String): TarotTellerResponse
    suspend fun endSession()

    companion object {
        const val SESSION_NOT_FOUND_CODE = 440
    }
}

class TarotAiOnCloudRepoImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tarotCardApiService: TarotCardApiService
) : TarotAiRepo {
    private val sessionIdFlow = MutableStateFlow<String?>(null)

    override suspend fun startChat() {
        val localeString = context.localeString()
        val request = StartChatRequest(locale = localeString)
        sessionIdFlow.value = tarotCardApiService.startChat(request = request).sessionId
        Timber.d("startChat sessionId: ${sessionIdFlow.value}")
    }

    override suspend fun setTopic(topic: String): TarotTellerResponse {
        val sessionId = fetchAndValidateSessionId()
        val request = SetupTopicRequest(topic = topic)
        return tarotCardApiService.setTopic(
            sessionId = sessionId,
            request = request
        )
    }

    override suspend fun provideDetail(chat: String): TarotTellerResponse {
        val sessionId = fetchAndValidateSessionId()
        val request = ProvideDetailRequest(chat = chat)
        return tarotCardApiService.provideDetail(sessionId = sessionId, request = request)
    }

    override suspend fun endSession() {
        val sessionId = sessionIdFlow.value
        if (sessionId == null) {
            Timber.w("endSession but sessionId is null ")
            return
        }
        tarotCardApiService.endSession(sessionId = sessionId)
        sessionIdFlow.value = null
    }

    private suspend fun fetchAndValidateSessionId(): String {
        val sessionId = fetchUntil { sessionIdFlow.value }
        if (sessionId == null) {
            throw SessionNotFoundError()
        }

        return sessionId
    }
}

enum class TarotSessionTellerAction(val action: String) {
    ASK_DETAIL("ask_detail"),
    EXPLAIN_ALL_CARDS_AND_ASK_TO_END_GAME("explain_all_cards_and_ask_to_end_game"),
}

class OffLineDummyTarotAiRepoImpl @Inject constructor() : TarotAiRepo {
    private fun emptyResponse() = TarotTellerResponse(
        action = null,
        chat = null,
        code = null,
        error = null,
        drawnTarotCardList = null
    )

    override suspend fun setTopic(topic: String): TarotTellerResponse {
        return emptyResponse().copy(
            action = TarotSessionTellerAction.ASK_DETAIL.action,
            chat = "請問您的問題是什麼？",
            drawnTarotCardList = null,
        )
    }

    override suspend fun provideDetail(chat: String): TarotTellerResponse {
        return emptyResponse().copy(
            action = TarotSessionTellerAction.EXPLAIN_ALL_CARDS_AND_ASK_TO_END_GAME.action,
            chat = "",
            drawnTarotCardList = listOf(
                TarotCardDetail(
                    cardNameWithDirection = "The Fool (Upright)",
                    tarotCardNumber = 0,
                    isTarotCardUpRight = true,
                    answerFromCard = "愚者代表新的開始，無憂無慮，充滿信心，但也可能是不切實際的夢想。"
                ),
                TarotCardDetail(
                    cardNameWithDirection = "The Magician (Upright)",
                    tarotCardNumber = 1,
                    isTarotCardUpRight = true,
                    answerFromCard = "魔術師代表創造力，意志力，自信心，但也可能是欺騙，自大。"
                ),
                TarotCardDetail(
                    cardNameWithDirection = "The High Priestess (Upright)",
                    tarotCardNumber = 2,
                    isTarotCardUpRight = true,
                    answerFromCard = "女教皇代表直覺，神秘，隱藏的知識，但也可能是虛幻，不切實際。"
                )
            )
        )
    }

    override suspend fun startChat() {}

    override suspend fun endSession() {}
}
