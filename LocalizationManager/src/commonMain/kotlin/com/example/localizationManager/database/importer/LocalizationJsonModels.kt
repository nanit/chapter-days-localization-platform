package com.example.localizationManager.database.importer

import kotlinx.serialization.Serializable

/**
 * JSON model for a simple string value
 */
@Serializable
data class JsonStringValue(
    val key: String,
    val value: String,
    val description: String? = null
)

/**
 * JSON model for a string array
 */
@Serializable
data class JsonStringArray(
    val key: String,
    val items: List<String>,
    val description: String? = null
)

/**
 * JSON model for a string plural
 */
@Serializable
data class JsonStringPlural(
    val key: String,
    val quantities: Map<String, String>,
    val description: String? = null
)

/**
 * Root JSON model for localization file
 */
@Serializable
data class LocalizationJson(
    val locale: String,
    val values: List<JsonStringValue> = emptyList(),
    val arrays: List<JsonStringArray> = emptyList(),
    val plurals: List<JsonStringPlural> = emptyList()
)
