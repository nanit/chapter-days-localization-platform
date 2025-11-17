package com.example.localizationManager

object NanitLocalization {
    private lateinit var config: LocalizationManagerConfig

//    private val apiClient by lazy { ApiClient(config) }
    private val localizationManager by lazy { LocalizationManager(config) }

    fun initialize(config: LocalizationManagerConfig) {
        this.config = config
        localizationManager.initialize()
    }
}