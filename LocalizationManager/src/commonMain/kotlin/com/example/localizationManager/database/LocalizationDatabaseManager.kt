package com.example.localizationManager.database

import com.example.localizationManager.database.database.LocalizationRepository
import com.example.localizationManager.database.database.SqlDriverProvider
import com.example.localizationManager.database.model.PluralQuantity
import com.example.localizationManager.database.model.StringResource

/**
 * Main facade for managing localization in a Compose Multiplatform application
 * Provides a unified API for storing and retrieving localized strings
 */
class LocalizationDatabaseManager(sqlDriverProvider: SqlDriverProvider) {
    private val repository = LocalizationRepository(sqlDriverProvider)
    private val stringLoader: StringLoader = DatabaseStringLoader(repository)

    // ==================== String Value Operations ====================

    /**
     * Store a simple string value
     */
    suspend fun storeString(key: String, value: String, locale: String = "en", description: String? = null) {
        val resource = StringResource.Value(
            key = key,
            value = value,
            locale = locale,
            description = description
        )
        repository.insertStringValue(resource)
    }

    /**
     * Update an existing string value
     */
    suspend fun updateString(key: String, value: String, locale: String = "en", description: String? = null) {
        val resource = StringResource.Value(
            key = key,
            value = value,
            locale = locale,
            description = description
        )
        repository.updateStringValue(resource)
    }

    /**
     * Load a string value
     */
    suspend fun loadString(key: String, env: LocalizationEnvironment = LocalizationEnvironment()): String? {
        return stringLoader.loadString(key, env)
    }

    suspend fun loadStringResourcesForLocale(locale: String): List<Pair<String, String>> {
        return repository.getAllStringValues(locale).map { resource ->
            resource.key to resource.value
        }
    }

    /**
     * Load a string with a default value if not found
     */
    suspend fun loadStringOrDefault(key: String, env: LocalizationEnvironment = LocalizationEnvironment()): String {
        return stringLoader.loadStringOrDefault(key, env)
    }

    /**
     * Delete a string value
     */
    suspend fun deleteString(key: String, locale: String = "en") {
        repository.deleteStringValue(key, locale)
    }

    // ==================== String Array Operations ====================

    /**
     * Store a string array
     */
    suspend fun storeStringArray(key: String, items: List<String>, locale: String = "en", description: String? = null) {
        val resource = StringResource.Array(
            key = key,
            items = items,
            locale = locale,
            description = description
        )
        repository.insertStringArray(resource)
    }

    /**
     * Load a string array
     */
    suspend fun loadStringArray(key: String, env: LocalizationEnvironment = LocalizationEnvironment()): List<String>? {
        return stringLoader.loadStringArray(key, env)
    }

    /**
     * Delete a string array
     */
    suspend fun deleteStringArray(key: String, locale: String = "en") {
        repository.deleteStringArray(key, locale)
    }

    // ==================== String Plural Operations ====================

    /**
     * Store a string plural with multiple quantity forms
     */
    suspend fun storeStringPlural(
        key: String,
        quantities: Map<PluralQuantity, String>,
        locale: String = "en",
        description: String? = null
    ) {
        val resource = StringResource.Plural(
            key = key,
            quantities = quantities,
            locale = locale,
            description = description
        )
        repository.insertStringPlural(resource)
    }

    /**
     * Load a plural string for a specific quantity
     */
    suspend fun loadStringPlural(key: String, quantity: Int, env: LocalizationEnvironment = LocalizationEnvironment()): String? {
        return stringLoader.loadStringPlural(key, quantity, env)
    }

    /**
     * Delete a string plural
     */
    suspend fun deleteStringPlural(key: String, locale: String = "en") {
        repository.deleteStringPlural(key, locale)
    }
}
