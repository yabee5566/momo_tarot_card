package com.onean.momo.data.network.repo.ai

import com.onean.momo.data.network.repo.ai.tarot_card_backend.TarotCardApiService
import com.onean.momo.data.network.request.ReplyQuestionRequest
import com.onean.momo.data.network.request.SetupQuestionCategoryRequest
import com.onean.momo.data.network.response.TarotCardDetail
import com.onean.momo.data.network.response.TarotTellerResponse
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

// FIXME: use enum and data class rather than all String going around
interface TarotAiRepo {
    suspend fun startChat()
    suspend fun setupQuestionCategory(category: String): TarotTellerResponse
    suspend fun replyQuestion(reply: String): TarotTellerResponse
    suspend fun endSession()
}

class TarotAiOnCloudRepoImpl @Inject constructor(
    private val tarotCardApiService: TarotCardApiService
) : TarotAiRepo {
    val sessionIdFlow = MutableStateFlow<String?>(null)

    override suspend fun startChat() {
        sessionIdFlow.value = tarotCardApiService.startChat().sessionId
        Timber.d("startChat sessionId: ${sessionIdFlow.value}")
    }

    override suspend fun setupQuestionCategory(category: String): TarotTellerResponse {
        val sessionId = sessionIdFlow.value
        check(sessionId != null) { "sessionId is null" }
        val request = SetupQuestionCategoryRequest(questionCategory = category)
        return tarotCardApiService.setupQuestionCategory(
            sessionId = sessionId,
            request = request
        )
    }

    override suspend fun replyQuestion(reply: String): TarotTellerResponse {
        val sessionId = sessionIdFlow.value
        check(sessionId != null) { "sessionId is null" }
        val request = ReplyQuestionRequest(chat = reply)
        return tarotCardApiService.replyQuestion(sessionId = sessionId, request = request)
    }

    override suspend fun endSession() {
        val sessionId = sessionIdFlow.value
        check(sessionId != null) { "sessionId is null" }
        tarotCardApiService.endSession(sessionId = sessionId)
        sessionIdFlow.value = null
    }
}

enum class TarotSessionTellerAction(val action: String) {
    ASK_FURTHER_QUESTION("ask_further_question"),
    EXPLAIN_ALL_CARDS_AND_ASK_TO_END_GAME("explain_all_cards_and_ask_to_end_game"),
    ABUSE("abuse"),
    ERROR("error"),
    TERMINATE("terminate")
}

class OffLineDummyTarotAiRepoImpl @Inject constructor() : TarotAiRepo {
    override suspend fun setupQuestionCategory(category: String): TarotTellerResponse {
        return TarotTellerResponse(
            action = TarotSessionTellerAction.ASK_FURTHER_QUESTION.action,
            chat = "請問你的問題是什麼？",
            drawnTarotCardList = null,
        )
    }

    override suspend fun replyQuestion(reply: String): TarotTellerResponse {
        return TarotTellerResponse(
            action = TarotSessionTellerAction.EXPLAIN_ALL_CARDS_AND_ASK_TO_END_GAME.action,
            chat = "",
            drawnTarotCardList = listOf(
                TarotCardDetail(
                    tarotCardNameZh = "愚者",
                    tarotCardNameEn = "The Fool",
                    tarotCardNumber = 0,
                    isTarotCardUpRight = true,
                    answerFromCard = "愚者代表新的開始，無憂無慮，充滿信心，但也可能是不切實際的夢想。"
                ),
                TarotCardDetail(
                    tarotCardNameZh = "魔術師",
                    tarotCardNameEn = "The Magician",
                    tarotCardNumber = 1,
                    isTarotCardUpRight = true,
                    answerFromCard = "魔術師代表創造力，意志力，自信心，但也可能是欺騙，自大。"
                ),
                TarotCardDetail(
                    tarotCardNameZh = "女教皇",
                    tarotCardNameEn = "The High Priestess",
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
