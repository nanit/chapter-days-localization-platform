package com.example.localizationManager

import com.example.localizationManager.api.ApiConfig

data class LocalizationManagerConfig(
    val localeProvider: LocaleProvider,
    val cacheSize: Long = 1_000
)