package com.example.localizationManager.database

import com.example.localizationManager.database.database.LocalizationRepository

/**
 * Configuration for loading strings from the database
 */
data class LocalizationEnvironment(
    val locale: String = "en",
    val fallbackLocale: String = "en"
)

/**
 * Interface for loading localized strings
 */
interface StringLoader {
    /**
     * Load a simple string value by key
     */
    suspend fun loadString(key: String, env: LocalizationEnvironment): String?

    /**
     * Load a string array by key
     */
    suspend fun loadStringArray(key: String, env: LocalizationEnvironment): List<String>?

    /**
     * Load a plural string by key and quantity
     */
    suspend fun loadStringPlural(key: String, quantity: Int, env: LocalizationEnvironment): String?
}

/**
 * Implementation of StringLoader using LocalizationRepository
 */
class DatabaseStringLoader(private val repository: LocalizationRepository) : StringLoader {

    /**
     * Load a simple string value by key
     * Falls back to fallback locale if not found in primary locale
     */
    override suspend fun loadString(key: String, env: LocalizationEnvironment): String? {
        // Try primary locale
        repository.getStringValue(key, env.locale)?.let {
            return it.value
        }

        // Try fallback locale
        if (env.locale != env.fallbackLocale) {
            repository.getStringValue(key, env.fallbackLocale)?.let {
                return it.value
            }
        }

        return null
    }

    /**
     * Load a string array by key
     * Falls back to fallback locale if not found in primary locale
     */
    override suspend fun loadStringArray(key: String, env: LocalizationEnvironment): List<String>? {
        // Try primary locale
        repository.getStringArray(key, env.locale)?.let {
            return it.items
        }

        // Try fallback locale
        if (env.locale != env.fallbackLocale) {
            repository.getStringArray(key, env.fallbackLocale)?.let {
                return it.items
            }
        }

        return null
    }

    /**
     * Load a plural string by key and quantity
     * Falls back to fallback locale if not found in primary locale
     */
    override suspend fun loadStringPlural(key: String, quantity: Int, env: LocalizationEnvironment): String? {
        // Try primary locale
        repository.getStringPlural(key, env.locale)?.let {
            return it.getForQuantity(quantity)
        }

        // Try fallback locale
        if (env.locale != env.fallbackLocale) {
            repository.getStringPlural(key, env.fallbackLocale)?.let {
                return it.getForQuantity(quantity)
            }
        }

        return null
    }
}

/**
 * Extension functions for easier usage
 */
suspend fun StringLoader.loadStringOrDefault(key: String, env: LocalizationEnvironment, default: String = ""): String {
    return loadString(key, env) ?: default
}
