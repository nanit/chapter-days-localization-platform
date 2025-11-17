package com.example.localizationManager.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalizationResponse(
    @SerialName("locale")
    val locale: String,

    @SerialName("strings")
    val strings: Map<String, String>,

    @SerialName("version")
    val version: String? = null,

    @SerialName("last_updated")
    val lastUpdated: Long? = null
)