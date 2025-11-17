package com.example.localizationManager.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("message")
    val message: String? = null,

    @SerialName("error")
    val error: String? = null
)