package com.nanit.localization.server.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateTranslationModel(
    @SerialName("locale")
    val locale: String,
    @SerialName("key")
    val key: String,
    @SerialName("value")
    val value: String,
)
