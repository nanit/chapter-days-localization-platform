package com.nanit.localization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateTranslationRequestBody(
    @SerialName("locale")
    val locale: String,
    @SerialName("key")
    val key: String,
    @SerialName("value")
    val value: String,
)
