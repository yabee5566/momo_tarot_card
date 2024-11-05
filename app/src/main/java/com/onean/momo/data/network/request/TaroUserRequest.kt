package com.onean.momo.data.network.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TaroUserRequest(
    val action: String,
    val chat: String? = null,
    val questionCategory: String? = null
)
