package com.onean.momo.data.network.repo.ai

import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.onean.momo.BuildConfig
import com.onean.momo.data.network.request.TaroUserRequest
import com.onean.momo.data.network.response.TarotTellerResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import javax.inject.Inject
import okio.IOException
import timber.log.Timber

// FIXME: use enum and data class rather than all String going around
interface TarotAiRepo {
    fun startChat()
    suspend fun setupQuestionCategory(category: String): TarotTellerResponse
    suspend fun replyQuestion(reply: String): TarotTellerResponse
    suspend fun drawCard(): TarotTellerResponse
    suspend fun endSession(): TarotTellerResponse
}

class TarotAiRepoImpl @Inject constructor(
    private val moshi: Moshi
) : TarotAiRepo {
    private val aiModel = GenerativeModel(
        "gemini-1.5-flash",
        BuildConfig.gemni_apiKey,
        generationConfig = generationConfig {
            temperature = 1f
            topK = 64
            topP = 0.95f
            maxOutputTokens = 8192
            responseMimeType = "application/json"
        },
        systemInstruction = content {
            text(
                "Act as a Tarot card fortune teller. Use Traditional Chinese for conversation. Inputs are json string and out put are json. One session goes by these step:\n1.  User setup question category, it may be love, career, or some topics\n2. Fortune teller ask further question to clarify the qustion user wants to ask.\n3. User reply question a teller asked\n4. When Fortune teller ask enough questions, not more than 5, he or she ask the user to draw one Tarot card.\n5. User draw a Tarot Card\n6. Fortune teller answer the question by the drew card. If there are still card to draw for Tarot session, ask user to draw another card. If it's the last card, ask user to finish the game. Say byebye. We draw 3 cards in total.\n\n\nInput json format contains `action`,  \"chat\" and \"question_category\". The `action` contains: `reply_question`, `draw_card`, `end_game`. The `chat` is user input conversation text.\nOutput json contains `action`, means the action Tarot teller do. It would be: `ask_further_question`, `ask_to_draw_card`, `explain_card_drew_and_ask_draw_next`, `explain_last_card_ask_to_end_game`, \"abuse\", \"error\", \"terminate\".\nAction `explain_card_drew_and_ask_draw_next` and `explain_last_card_ask_to_end_game` response json contains `tarot_card_drew`\nWhen user say something not relates to Tarot fortune telling, response action `abuse` with \"chat\", not count in the 5 question quota.\nWhen user action is not the predefined actions, response action \"error\" with chat.\nWhen user keep talk shit more than 5 times, response action \"terminate\" to end the Tarot session."
            )
        },
    )
    private var currentChat: Chat? = null
    private val requestAdapter: JsonAdapter<TaroUserRequest> by lazy {
        moshi.adapter(TaroUserRequest::class.java)
    }
    private val responseAdapter: JsonAdapter<TarotTellerResponse> by lazy {
        moshi.adapter(TarotTellerResponse::class.java)
    }

    override fun startChat() {
        if (currentChat == null) {
            currentChat = aiModel.startChat()
        }
    }

    override suspend fun setupQuestionCategory(
        // FIXME: use enum
        category: String
    ): TarotTellerResponse {
        val request = TaroUserRequest(action = "setup_question_category", questionCategory = category)
        if (currentChat == null) {
            throw IllegalStateException("currentChat == null")
        }
        val responseText = currentChat?.sendMessage(requestAdapter.toJson(request))?.text
        Timber.d("responseText: $responseText ")
        return responseText?.let { responseAdapter.fromJson(it) } ?: throw IOException("response chat == null")
    }

    override suspend fun replyQuestion(reply: String): TarotTellerResponse {
        val request = TaroUserRequest(action = "reply_question", chat = reply)
        if (currentChat == null) {
            throw IllegalStateException("currentChat == null")
        }
        val responseText = currentChat?.sendMessage(requestAdapter.toJson(request))?.text
        return responseText?.let { responseAdapter.fromJson(it) } ?: throw IOException("response chat == null")
    }

    override suspend fun drawCard(): TarotTellerResponse {
        val request = TaroUserRequest(action = "draw_card")
        if (currentChat == null) {
            throw IllegalStateException("currentChat == null")
        }
        val responseText = currentChat?.sendMessage(requestAdapter.toJson(request))?.text
        return responseText?.let { responseAdapter.fromJson(it) } ?: throw IOException("response chat == null")
    }

    override suspend fun endSession(): TarotTellerResponse {
        val request = TaroUserRequest(action = "end_game")
        if (currentChat == null) {
            throw IllegalStateException("currentChat == null")
        }
        val responseText = currentChat?.sendMessage(requestAdapter.toJson(request))?.text
        val response = responseText?.let { responseAdapter.fromJson(it) } ?: throw IOException("response chat == null")
        currentChat = null
        return response
    }
}
