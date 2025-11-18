package com.nanit.localization.server.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IncomingTranslation(
    @SerialName("key")
    val key: String,
    @SerialName("description")
    val description: String?,
    @SerialName("values")
    val values: List<Translation>,
) {
    @Serializable
    data class Translation(
        @SerialName("locale")
        val locale: String,
        @SerialName("value")
        val value: String,
    )
}
