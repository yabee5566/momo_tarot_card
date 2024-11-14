package com.onean.momo.data.network.repo.ai

import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.FunctionType
import com.google.ai.client.generativeai.type.FunctionType.Companion.OBJECT
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.Schema
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
    suspend fun endSession()
}

class TarotAiRepoImpl @Inject constructor(
    private val moshi: Moshi
) : TarotAiRepo {
    private val aiModel = GenerativeModel(
        "gemini-1.5-flash",
        BuildConfig.gemni_apiKey,
        safetySettings = listOf(
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.ONLY_HIGH),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.ONLY_HIGH),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.ONLY_HIGH),
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH)
        ),
        generationConfig = generationConfig {
            temperature = 1f
            topK = 64
            topP = 0.95f
            maxOutputTokens = 8192
            responseMimeType = "application/json"
            responseSchema = Schema.obj(
                name = "",
                description = "outer most object",
                Schema.str("action", "action"),
                Schema(type = FunctionType.STRING, name = "chat", description = "chat"), // optional
                Schema(type = OBJECT,
                    name = "tarot_card_drew",
                    description = "tarot card drew",
                    nullable = true,
                    properties = mapOf(
                        "tarot_card_name_en" to Schema.str("tarot_card_name_en", "tarot_card_name_en"),
                        "tarot_card_id" to Schema.int("tarot_card_id", "tarot_card_id"),
                        "is_tarot_card_up_right" to Schema.bool("is_tarot_card_up_right", "is_tarot_card_up_right"),
                        "tarot_card_name_zh" to Schema.str("tarot_card_name_zh", "tarot_card_name_zh"),
                        "answer_from_card" to Schema.str("answer_from_card", "answer_from_card")
                    )
                ),
            )
        },
        systemInstruction = content {
            text(
                "Act as a Tarot card fortune teller. Inputs are JSON strings and output are JSON. \n\nAll tarot cards are indexed using JSON below:\n{\n  \"all_tarot_cards\": [\n    {\"id\": 0, \"name\": \"The Fool\"},\n    {\"id\": 1, \"name\": \"The Magician\"},\n    {\"id\": 2, \"name\": \"The High Priestess\"},\n    {\"id\": 3, \"name\": \"The Empress\"},\n    {\"id\": 4, \"name\": \"The Emperor\"},\n    {\"id\": 5, \"name\": \"The Hierophant\"},\n    {\"id\": 6, \"name\": \"The Lovers\"},\n    {\"id\": 7, \"name\": \"The Chariot\"},\n    {\"id\": 8, \"name\": \"Strength\"},\n    {\"id\": 9, \"name\": \"The Hermit\"},\n    {\"id\": 10, \"name\": \"Wheel of Fortune\"},\n    {\"id\": 11, \"name\": \"Justice\"},\n    {\"id\": 12, \"name\": \"The Hanged Man\"},\n    {\"id\": 13, \"name\": \"Death\"},\n    {\"id\": 14, \"name\": \"Temperance\"},\n    {\"id\": 15, \"name\": \"The Devil\"},\n    {\"id\": 16, \"name\": \"The Tower\"},\n    {\"id\": 17, \"name\": \"The Star\"},\n    {\"id\": 18, \"name\": \"The Moon\"},\n    {\"id\": 19, \"name\": \"The Sun\"},\n    {\"id\": 20, \"name\": \"Judgement\"},\n    {\"id\": 21, \"name\": \"The World\"},\n    {\"id\": 22, \"name\": \"Ace of Wands\"},\n    {\"id\": 23, \"name\": \"Two of Wands\"},\n    {\"id\": 24, \"name\": \"Three of Wands\"},\n    {\"id\": 25, \"name\": \"Four of Wands\"},\n    {\"id\": 26, \"name\": \"Five of Wands\"},\n    {\"id\": 27, \"name\": \"Six of Wands\"},\n    {\"id\": 28, \"name\": \"Seven of Wands\"},\n    {\"id\": 29, \"name\": \"Eight of Wands\"},\n    {\"id\": 30, \"name\": \"Nine of Wands\"},\n    {\"id\": 31, \"name\": \"Ten of Wands\"},\n    {\"id\": 32, \"name\": \"Page of Wands\"},\n    {\"id\": 33, \"name\": \"Knight of Wands\"},\n    {\"id\": 34, \"name\": \"Queen of Wands\"},\n    {\"id\": 35, \"name\": \"King of Wands\"},\n    {\"id\": 36, \"name\": \"Ace of Cups\"},\n    {\"id\": 37, \"name\": \"Two of Cups\"},\n    {\"id\": 38, \"name\": \"Three of Cups\"},\n    {\"id\": 39, \"name\": \"Four of Cups\"},\n    {\"id\": 40, \"name\": \"Five of Cups\"},\n    {\"id\": 41, \"name\": \"Six of Cups\"},\n    {\"id\": 42, \"name\": \"Seven of Cups\"},\n    {\"id\": 43, \"name\": \"Eight of Cups\"},\n    {\"id\": 44, \"name\": \"Nine of Cups\"},\n    {\"id\": 45, \"name\": \"Ten of Cups\"},\n    {\"id\": 46, \"name\": \"Page of Cups\"},\n    {\"id\": 47, \"name\": \"Knight of Cups\"},\n    {\"id\": 48, \"name\": \"Queen of Cups\"},\n    {\"id\": 49, \"name\": \"King of Cups\"},\n    {\"id\": 50, \"name\": \"Ace of Swords\"},\n    {\"id\": 51, \"name\": \"Two of Swords\"},\n    {\"id\": 52, \"name\": \"Three of Swords\"},\n    {\"id\": 53, \"name\": \"Four of Swords\"},\n    {\"id\": 54, \"name\": \"Five of Swords\"},\n    {\"id\": 55, \"name\": \"Six of Swords\"},\n    {\"id\": 56, \"name\": \"Seven of Swords\"},\n    {\"id\": 57, \"name\": \"Eight of Swords\"},\n    {\"id\": 58, \"name\": \"Nine of Swords\"},\n    {\"id\": 59, \"name\": \"Ten of Swords\"},\n    {\"id\": 60, \"name\": \"Page of Swords\"},\n    {\"id\": 61, \"name\": \"Knight of Swords\"},\n    {\"id\": 62, \"name\": \"Queen of Swords\"},\n    {\"id\": 63, \"name\": \"King of Swords\"},\n    {\"id\": 64, \"name\": \"Ace of Pentacles\"},\n    {\"id\": 65, \"name\": \"Two of Pentacles\"},\n    {\"id\": 66, \"name\": \"Three of Pentacles\"},\n    {\"id\": 67, \"name\": \"Four of Pentacles\"},\n    {\"id\": 68, \"name\": \"Five of Pentacles\"},\n    {\"id\": 69, \"name\": \"Six of Pentacles\"},\n    {\"id\": 70, \"name\": \"Seven of Pentacles\"},\n    {\"id\": 71, \"name\": \"Eight of Pentacles\"},\n    {\"id\": 72, \"name\": \"Nine of Pentacles\"},\n    {\"id\": 73, \"name\": \"Ten of Pentacles\"},\n    {\"id\": 74, \"name\": \"Page of Pentacles\"},\n    {\"id\": 75, \"name\": \"Knight of Pentacles\"},\n    {\"id\": 76, \"name\": \"Queen of Pentacles\"},\n    {\"id\": 77, \"name\": \"King of Pentacles\"}\n  ]\n}\n\nA Tarot session goes by these steps:\n1.  User setup question category, it may be love, career, or some topics\n2. The Fortune teller asks further questions to clarify the real question a user wants to ask.\n3. User reply question a teller asked\n4. When the Fortune teller asks enough questions, not more than 5, he or she asks the user to draw one Tarot card.\n5. The user draws a Tarot Card\n6. The Fortune teller answers the question by the drew card. If there are still cards to draw for one Tarot session, ask the user to draw another card. If it's the last card, ask the user to finish the game. Say bye-bye. We draw 3 cards in total.\n\nInput JSON format contains `action`,  \"chat\" and \"question_category\". The `action` includes: `setup_question_category`, `reply_question`, `draw_card`, `end_game`. The `chat` is the user input conversation text.\nOutput JSON contains `action`, which means the action the Tarot teller does. It would be: `ask_further_question`, `ask_to_draw_card`, `explain_card_drew_and_ask_draw_next`, `explain_last_card_ask_to_end_game`, \"abuse\", \"error\", \"terminate\".\nAction `explain_card_drew_and_ask_draw_next` and `explain_last_card_ask_to_end_game` response JSON contains `tarot_card_drew`\nWhen the user says something that does not relate to Tarot fortune telling, respond action `abuse` with \"chat\", not counting in the 5-question quota.\nWhen the user action is not the predefined action, respond action \"error\" with chat.\nWhen the user keeps talking shit more than 5 times, respond action \"terminate\" to end the Tarot session."
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
        Timber.d("responseText: $responseText ")
        return responseText?.let { responseAdapter.fromJson(it) } ?: throw IOException("response chat == null")
    }

    override suspend fun drawCard(): TarotTellerResponse {
        val request = TaroUserRequest(action = "draw_card")
        if (currentChat == null) {
            throw IllegalStateException("currentChat == null")
        }
        val responseText = currentChat?.sendMessage(requestAdapter.toJson(request))?.text
        Timber.d("responseText: $responseText ")
        return responseText?.let { responseAdapter.fromJson(it) } ?: throw IOException("response chat == null")
    }

    override suspend fun endSession() {
        val request = TaroUserRequest(action = "end_game")
        if (currentChat == null) {
            throw IllegalStateException("currentChat == null")
        }
        currentChat?.sendMessage(requestAdapter.toJson(request))?.text
        currentChat = null
    }
}
