package com.example.localizationManager

import com.example.localizationManager.database.database.SqlDriverProvider

data class LocalizationManagerConfig(
    val localeProvider: LocaleProvider,
    val sqlDriverProvider: SqlDriverProvider,
    val cacheSize: Long = 1_000
)
