package com.nanit.localization.server.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LMResponseList(
    @SerialName("values")
    val values: List<LMResponse>
) {
    companion object {
        fun from(list: List<StringResource.Value>): LMResponseList = LMResponseList(
            values = list
                .groupBy { it.locale }
                .map { (locale, list) -> LMResponse.from(locale, list) }
        )
    }
}
