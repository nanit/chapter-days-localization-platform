package com.example.localizationManager.api

data class ApiConfig(
    val baseUrl: String,
    val stringsEndpoint: String = "/api/v1/localizations",
    val appVersion: String = "1",
    val headers: Map<String, String> = emptyMap(),
    val timeoutMillis: Long = 30_000
)