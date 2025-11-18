package com.example.localizationManager

import androidx.compose.runtime.Composable

object NanitLocalization {
    private lateinit var config: LocalizationManagerConfig

    private val localizationManager by lazy { LocalizationManager(config) }

    fun initialize(config: LocalizationManagerConfig) {
        this.config = config
        localizationManager.initialize()
    }

    fun getManager(): LocalizationManager {
        return localizationManager
    }

    @Composable
    fun stringResource(key: String): String {
        return localizationManager.stringResource(key)
    }

    suspend fun getString(key: String): String {
        return localizationManager.getString(key)
    }
}