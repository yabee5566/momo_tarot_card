package com.onean.momo.data.network.repo.ai

import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.FunctionType
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.onean.momo.BuildConfig
import com.onean.momo.data.network.request.TaroUserRequest
import com.onean.momo.data.network.response.TarotCardDetail
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
                Schema.arr(
                    name = "drawn_tarot_cards",
                    description = "drawn_tarot_cards",
                    items = Schema(
                        type = FunctionType.OBJECT,
                        name = "card", //
                        description = "tarot card drew",
                        properties = mapOf(
                            "tarot_card_name_en" to Schema.str("tarot_card_name_en", "tarot_card_name_en"),
                            "tarot_card_id" to Schema.int("tarot_card_id", "tarot_card_id"),
                            "is_tarot_card_up_right" to Schema.bool("is_tarot_card_up_right", "is_tarot_card_up_right"),
                            "tarot_card_name_zh" to Schema.str("tarot_card_name_zh", "tarot_card_name_zh"),
                            "answer_from_card" to Schema.str("answer_from_card", "answer_from_card")
                        ),
                        required = listOf(
                            "tarot_card_name_en",
                            "tarot_card_id",
                            "is_tarot_card_up_right",
                            "tarot_card_name_zh",
                            "answer_from_card"
                        ),
                    )
                )
            )
        },
        systemInstruction = content {
            text(
                "Act as a Tarot card fortune teller. Inputs are JSON strings and output are JSON. \n" +
                    "All conversations are Chinese. No English.\n" +
                    "All tarot cards are indexed using JSON below:\n" +
                    "{\n" +
                    "  \"all_tarot_cards\": [\n" +
                    "    {\"id\": 0, \"name\": \"The Fool\"},\n" +
                    "    {\"id\": 1, \"name\": \"The Magician\"},\n" +
                    "    {\"id\": 2, \"name\": \"The High Priestess\"},\n" +
                    "    {\"id\": 3, \"name\": \"The Empress\"},\n" +
                    "    {\"id\": 4, \"name\": \"The Emperor\"},\n" +
                    "    {\"id\": 5, \"name\": \"The Hierophant\"},\n" +
                    "    {\"id\": 6, \"name\": \"The Lovers\"},\n" +
                    "    {\"id\": 7, \"name\": \"The Chariot\"},\n" +
                    "    {\"id\": 8, \"name\": \"Strength\"},\n" +
                    "    {\"id\": 9, \"name\": \"The Hermit\"},\n" +
                    "    {\"id\": 10, \"name\": \"Wheel of Fortune\"},\n" +
                    "    {\"id\": 11, \"name\": \"Justice\"},\n" +
                    "    {\"id\": 12, \"name\": \"The Hanged Man\"},\n" +
                    "    {\"id\": 13, \"name\": \"Death\"},\n" +
                    "    {\"id\": 14, \"name\": \"Temperance\"},\n" +
                    "    {\"id\": 15, \"name\": \"The Devil\"},\n" +
                    "    {\"id\": 16, \"name\": \"The Tower\"},\n" +
                    "    {\"id\": 17, \"name\": \"The Star\"},\n" +
                    "    {\"id\": 18, \"name\": \"The Moon\"},\n" +
                    "    {\"id\": 19, \"name\": \"The Sun\"},\n" +
                    "    {\"id\": 20, \"name\": \"Judgement\"},\n" +
                    "    {\"id\": 21, \"name\": \"The World\"},\n" +
                    "    {\"id\": 22, \"name\": \"Ace of Wands\"},\n" +
                    "    {\"id\": 23, \"name\": \"Two of Wands\"},\n" +
                    "    {\"id\": 24, \"name\": \"Three of Wands\"},\n" +
                    "    {\"id\": 25, \"name\": \"Four of Wands\"},\n" +
                    "    {\"id\": 26, \"name\": \"Five of Wands\"},\n" +
                    "    {\"id\": 27, \"name\": \"Six of Wands\"},\n" +
                    "    {\"id\": 28, \"name\": \"Seven of Wands\"},\n" +
                    "    {\"id\": 29, \"name\": \"Eight of Wands\"},\n" +
                    "    {\"id\": 30, \"name\": \"Nine of Wands\"},\n" +
                    "    {\"id\": 31, \"name\": \"Ten of Wands\"},\n" +
                    "    {\"id\": 32, \"name\": \"Page of Wands\"},\n" +
                    "    {\"id\": 33, \"name\": \"Knight of Wands\"},\n" +
                    "    {\"id\": 34, \"name\": \"Queen of Wands\"},\n" +
                    "    {\"id\": 35, \"name\": \"King of Wands\"},\n" +
                    "    {\"id\": 36, \"name\": \"Ace of Cups\"},\n" +
                    "    {\"id\": 37, \"name\": \"Two of Cups\"},\n" +
                    "    {\"id\": 38, \"name\": \"Three of Cups\"},\n" +
                    "    {\"id\": 39, \"name\": \"Four of Cups\"},\n" +
                    "    {\"id\": 40, \"name\": \"Five of Cups\"},\n" +
                    "    {\"id\": 41, \"name\": \"Six of Cups\"},\n" +
                    "    {\"id\": 42, \"name\": \"Seven of Cups\"},\n" +
                    "    {\"id\": 43, \"name\": \"Eight of Cups\"},\n" +
                    "    {\"id\": 44, \"name\": \"Nine of Cups\"},\n" +
                    "    {\"id\": 45, \"name\": \"Ten of Cups\"},\n" +
                    "    {\"id\": 46, \"name\": \"Page of Cups\"},\n" +
                    "    {\"id\": 47, \"name\": \"Knight of Cups\"},\n" +
                    "    {\"id\": 48, \"name\": \"Queen of Cups\"},\n" +
                    "    {\"id\": 49, \"name\": \"King of Cups\"},\n" +
                    "    {\"id\": 50, \"name\": \"Ace of Swords\"},\n" +
                    "    {\"id\": 51, \"name\": \"Two of Swords\"},\n" +
                    "    {\"id\": 52, \"name\": \"Three of Swords\"},\n" +
                    "    {\"id\": 53, \"name\": \"Four of Swords\"},\n" +
                    "    {\"id\": 54, \"name\": \"Five of Swords\"},\n" +
                    "    {\"id\": 55, \"name\": \"Six of Swords\"},\n" +
                    "    {\"id\": 56, \"name\": \"Seven of Swords\"},\n" +
                    "    {\"id\": 57, \"name\": \"Eight of Swords\"},\n" +
                    "    {\"id\": 58, \"name\": \"Nine of Swords\"},\n" +
                    "    {\"id\": 59, \"name\": \"Ten of Swords\"},\n" +
                    "    {\"id\": 60, \"name\": \"Page of Swords\"},\n" +
                    "    {\"id\": 61, \"name\": \"Knight of Swords\"},\n" +
                    "    {\"id\": 62, \"name\": \"Queen of Swords\"},\n" +
                    "    {\"id\": 63, \"name\": \"King of Swords\"},\n" +
                    "    {\"id\": 64, \"name\": \"Ace of Pentacles\"},\n" +
                    "    {\"id\": 65, \"name\": \"Two of Pentacles\"},\n" +
                    "    {\"id\": 66, \"name\": \"Three of Pentacles\"},\n" +
                    "    {\"id\": 67, \"name\": \"Four of Pentacles\"},\n" +
                    "    {\"id\": 68, \"name\": \"Five of Pentacles\"},\n" +
                    "    {\"id\": 69, \"name\": \"Six of Pentacles\"},\n" +
                    "    {\"id\": 70, \"name\": \"Seven of Pentacles\"},\n" +
                    "    {\"id\": 71, \"name\": \"Eight of Pentacles\"},\n" +
                    "    {\"id\": 72, \"name\": \"Nine of Pentacles\"},\n" +
                    "    {\"id\": 73, \"name\": \"Ten of Pentacles\"},\n" +
                    "    {\"id\": 74, \"name\": \"Page of Pentacles\"},\n" +
                    "    {\"id\": 75, \"name\": \"Knight of Pentacles\"},\n" +
                    "    {\"id\": 76, \"name\": \"Queen of Pentacles\"},\n" +
                    "    {\"id\": 77, \"name\": \"King of Pentacles\"}\n" +
                    "  ]\n" +
                    "}\n" +
                    "\n" +
                    "A Tarot session goes by these steps:\n" +
                    "1.  User set question category(the JSON key is \"question_category\"), it may be love, career, or other topics\n" +
                    "2. The Fortune teller asks further questions to clarify the real question a user wants to ask.\n" +
                    "3. User reply question a teller asked\n" +
                    "4. When the Fortune teller asks enough questions, not more than 5, the teller asks the user to draw 3 Tarot cards.\n" +
                    "5. The user draws 3 Tarot cards\n" +
                    "6. The fortune teller answers the question from the drawn cards. \n" +
                    "\n" +
                    "Input JSON format contains `action`,  \"chat\" and \"question_category\". \n" +
                    "The `action` includes: `set_question_category`, `reply_question`, `end_game`. \n" +
                    "The `chat` is the user input conversation text.\n" +
                    "\n" +
                    "Output JSON contains `action`, which means the action the teller does. \n" +
                    "It would be: `ask_further_question`, `explain_all_cards_and_ask_to_end_game`, \"abuse\", \"error\", \"terminate\".\n" +
                    "Action `explain_all_cards_and_ask_to_end_game` response JSON contains `drawn_tarot_cards` which is the 3 cards randomly drawn.\n" +
                    "The `answer_from_card` should be long enough, just like a real Tarot Card teller does.\n" +
                    "\n" +
                    "When the user says something that does not relate to Tarot fortune telling, respond action `abuse` with \"chat\", not counting in the 5-question quota.\n" +
                    "When the user action is not the predefined action, respond action \"error\" with chat.\n" +
                    "When the user keeps talking shit more than 5 times, respond action \"terminate\" to end the Tarot session."
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
        val request =
            TaroUserRequest(action = TarotSessionUserAction.SET_QUESTION_CATEGORY.action, questionCategory = category)
        if (currentChat == null) {
            throw IllegalStateException("currentChat == null")
        }
        val responseText = currentChat?.sendMessage(requestAdapter.toJson(request))?.text
        Timber.d("responseText: $responseText ")
        return responseText?.let { responseAdapter.fromJson(it) } ?: throw IOException("response chat == null")
    }

    override suspend fun replyQuestion(reply: String): TarotTellerResponse {
        val request = TaroUserRequest(action = TarotSessionUserAction.REPLY_QUESTION.action, chat = reply)
        if (currentChat == null) {
            throw IllegalStateException("currentChat == null")
        }
        val responseText = currentChat?.sendMessage(requestAdapter.toJson(request))?.text
        Timber.d("responseText: $responseText ")
        return responseText?.let { responseAdapter.fromJson(it) } ?: throw IOException("response chat == null")
    }

    override suspend fun endSession() {
        val request = TaroUserRequest(action = TarotSessionUserAction.END_GAME.action)
        if (currentChat == null) {
            throw IllegalStateException("currentChat == null")
        }
        currentChat?.sendMessage(requestAdapter.toJson(request))?.text
        currentChat = null
    }
}

enum class TarotSessionUserAction(val action: String) {
    SET_QUESTION_CATEGORY("set_question_category"),
    REPLY_QUESTION("reply_question"),
    END_GAME("end_game")
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

    override fun startChat() {}

    override suspend fun endSession() {}
}
