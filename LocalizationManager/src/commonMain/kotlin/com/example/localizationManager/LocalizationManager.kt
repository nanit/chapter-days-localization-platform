package com.example.localizationManager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class LocalizationManager(
    val config: LocalizationManagerConfig,
) {

    private val coroutineScope by lazy { CoroutineScope(Dispatchers.Default + SupervisorJob()) }

    fun initialize() {
        // Start observing locale changes
        coroutineScope.launch {
            config.localeProvider.observeLocalChanges()
                .distinctUntilChanged()
                .collect { newLocale ->
                    println("New Locale received: $newLocale")
//                    handleLocaleChange(newLocale)
                }
        }

    }
}