package com.nanit.localization.server.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("message")
    val message: String? = null,

    @SerialName("error")
    val error: String? = null
) {
    companion object {
        fun from(throwable: Throwable, message: () -> String): ErrorResponse = ErrorResponse(
            message = message(),
            error = throwable.message
        )
    }
}
