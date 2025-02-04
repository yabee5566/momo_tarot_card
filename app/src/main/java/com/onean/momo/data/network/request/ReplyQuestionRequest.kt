package com.onean.momo.data.network.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReplyQuestionRequest(
    val chat: String,
)
