package com.onean.momo.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StartChatRequest(
    @Json(name = "locale")
    val locale: String
)
