package com.nanit.localization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LMResponse(
    @SerialName("locale")
    val locale: String,
    @SerialName("values")
    val values: List<Value>,
) {
    @Serializable
    data class Value(
        @SerialName("key")
        val key: String,
        @SerialName("translation")
        val translation: String,
    )
}
