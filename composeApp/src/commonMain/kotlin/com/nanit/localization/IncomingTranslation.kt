package com.nanit.localization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendTranslationRequestBody(
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

    companion object {
        fun from(translationDomain: TranslationDomain) = with(translationDomain) {
            SendTranslationRequestBody(
                key = key,
                description = desc,
                values = values.map { stuff ->
                    Translation(
                        locale = stuff.locale.first,
                        value = stuff.value
                    )
                }
            )
        }
    }
}
