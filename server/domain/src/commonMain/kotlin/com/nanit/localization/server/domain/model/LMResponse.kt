package com.nanit.localization.server.domain.model

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

    companion object {
        fun from(locale: String, list: List<StringResource.Value>): LMResponse = LMResponse(
            locale = locale,
            values = list.map { v -> Value(key = v.key, translation = v.value) }
        )
    }
}
