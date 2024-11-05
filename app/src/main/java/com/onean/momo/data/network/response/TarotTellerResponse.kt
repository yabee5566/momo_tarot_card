package com.onean.momo.data.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TarotTellerResponse(
    val action: String,
    val chat: String?,
    val tarotCardDrew: TarotCardDetail?
)

@JsonClass(generateAdapter = true)
data class TarotCardDetail(
    val tarotCardNameZh: String,
    val tarotCardNameEn: String,
    val tarotCardNumber: Int,
    val isTarotCardUpRight: Boolean,
    val cardDescription: String,
    val answerFromCard: String
)
