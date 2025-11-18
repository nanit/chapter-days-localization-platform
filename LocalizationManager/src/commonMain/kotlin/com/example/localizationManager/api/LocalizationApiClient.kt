package com.example.localizationManager.api

interface LocalizationApiClient {
    suspend fun fetchStrings(locale: String): Map<String, String>
    fun close()
}