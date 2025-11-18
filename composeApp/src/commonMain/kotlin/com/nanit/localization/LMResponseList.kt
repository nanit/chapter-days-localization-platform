package com.nanit.localization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LMResponseList(
    @SerialName("values")
    val values: List<LMResponse>
)
