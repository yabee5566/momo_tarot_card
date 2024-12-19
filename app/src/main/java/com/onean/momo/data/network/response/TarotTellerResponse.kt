package com.onean.momo.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TarotTellerResponse(
    val action: String,
    val chat: String?,
    @Json(name = "drawn_tarot_cards")
    val drawnTarotCardList: List<TarotCardDetail>?
)

@JsonClass(generateAdapter = true)
data class TarotCardDetail(
    @Json(name = "tarot_card_name_zh")
    val tarotCardNameZh: String,
    @Json(name = "tarot_card_name_en")
    val tarotCardNameEn: String,
    @Json(name = "tarot_card_id")
    val tarotCardNumber: Int,
    @Json(name = "is_tarot_card_up_right")
    val isTarotCardUpRight: Boolean,
    @Json(name = "answer_from_card")
    val answerFromCard: String
)
