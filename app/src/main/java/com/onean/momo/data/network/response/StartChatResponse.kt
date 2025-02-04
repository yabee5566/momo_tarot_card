package com.onean.momo.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StartChatResponse(
    @Json(name = "session_id")
    val sessionId: String
)
