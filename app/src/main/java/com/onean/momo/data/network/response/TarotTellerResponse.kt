package com.onean.momo.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TarotTellerResponse(
    val action: String?,
    val chat: String?,
    val code: Int?,
    val error: String?,
    @Json(name = "drawn_tarot_cards")
    val drawnTarotCardList: List<TarotCardDetail>?
)

@JsonClass(generateAdapter = true)
data class TarotCardDetail(
    @Json(name = "id")
    val tarotCardNumber: Int,
    @Json(name = "card_name_with_direction")
    val cardNameWithDirection: String,
    @Json(name = "is_tarot_card_upright")
    val isTarotCardUpRight: Boolean,
    @Json(name = "answer_from_card")
    val answerFromCard: String
)
