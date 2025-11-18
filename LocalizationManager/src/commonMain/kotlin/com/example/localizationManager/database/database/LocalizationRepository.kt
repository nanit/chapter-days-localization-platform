package com.example.localizationManager.database.database

import com.example.localizationManager.database.model.PluralQuantity
import com.example.localizationManager.database.model.StringResource
import com.example.localizationManager.database.utils.currentTimeMillis
import com.nanit.localization.database.LocalizationDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for managing localization string resources in the database
 */
class LocalizationRepository(sqlDriverProvider: SqlDriverProvider) {
    private val database = LocalizationDatabase.Companion(sqlDriverProvider.createDriver())
    private val queries = database.localizationDatabaseQueries

    // ==================== String Value Operations ====================

    /**
     * Insert a new string value resource
     */
    suspend fun insertStringValue(resource: StringResource.Value) = withContext(Dispatchers.Default) {
        val currentTime = currentTimeMillis()
        queries.insertStringValue(
            key = resource.key,
            value_ = resource.value,
            locale = resource.locale,
            description = resource.description,
            created_at = currentTime,
            updated_at = currentTime
        )
    }

    /**
     * Update an existing string value resource
     */
    suspend fun updateStringValue(resource: StringResource.Value) = withContext(Dispatchers.Default) {
        queries.updateStringValue(
            value_ = resource.value,
            description = resource.description,
            updated_at = currentTimeMillis(),
            key = resource.key,
            locale = resource.locale
        )
    }

    /**
     * Get a string value by key and locale
     */
    suspend fun getStringValue(key: String, locale: String = "en"): StringResource.Value? =
        withContext(Dispatchers.Default) {
            queries.getStringValue(key, locale).executeAsOneOrNull()?.let {
                StringResource.Value(
                    key = it.key,
                    value = it.value_,
                    locale = it.locale,
                    description = it.description
                )
            }
        }

    /**
     * Get all string values for a locale
     */
    suspend fun getAllStringValues(locale: String = "en"): List<StringResource.Value> =
        withContext(Dispatchers.Default) {
            queries.getAllStringValues(locale).executeAsList().map {
                StringResource.Value(
                    key = it.key,
                    value = it.value_,
                    locale = it.locale,
                    description = it.description
                )
            }
        }

    /**
     * Delete a string value
     */
    suspend fun deleteStringValue(key: String, locale: String = "en") = withContext(Dispatchers.Default) {
        queries.deleteStringValue(key, locale)
    }

    // ==================== String Array Operations ====================

    /**
     * Insert a new string array resource
     */
    suspend fun insertStringArray(resource: StringResource.Array) = withContext(Dispatchers.Default) {
        queries.transaction {
            val currentTime = currentTimeMillis()
            queries.insertStringArray(
                key = resource.key,
                locale = resource.locale,
                description = resource.description,
                created_at = currentTime,
                updated_at = currentTime
            )

            val arrayId = queries.getStringArray(resource.key, resource.locale)
                .executeAsOne().id

            resource.items.forEachIndexed { index, item ->
                queries.insertStringArrayItem(
                    array_id = arrayId,
                    value_ = item,
                    position = index.toLong()
                )
            }
        }
    }

    /**
     * Get a string array by key and locale
     */
    suspend fun getStringArray(key: String, locale: String = "en"): StringResource.Array? =
        withContext(Dispatchers.Default) {
            val array = queries.getStringArray(key, locale).executeAsOneOrNull()
                ?: return@withContext null

            val items = queries.getStringArrayItems(array.id).executeAsList()
                .sortedBy { it.position }
                .map { it.value_ }

            StringResource.Array(
                key = array.key,
                items = items,
                locale = array.locale,
                description = array.description
            )
        }

    /**
     * Delete a string array
     */
    suspend fun deleteStringArray(key: String, locale: String = "en") = withContext(Dispatchers.Default) {
        queries.deleteStringArray(key, locale)
    }

    // ==================== String Plural Operations ====================

    /**
     * Insert a new string plural resource
     */
    suspend fun insertStringPlural(resource: StringResource.Plural) = withContext(Dispatchers.Default) {
        queries.transaction {
            val currentTime = currentTimeMillis()
            queries.insertStringPlural(
                key = resource.key,
                locale = resource.locale,
                description = resource.description,
                created_at = currentTime,
                updated_at = currentTime
            )

            val pluralId = queries.getStringPlural(resource.key, resource.locale)
                .executeAsOne().id

            resource.quantities.forEach { (quantity, value) ->
                queries.insertStringPluralQuantity(
                    plural_id = pluralId,
                    quantity = quantity.value,
                    value_ = value
                )
            }
        }
    }

    /**
     * Get a string plural by key and locale
     */
    suspend fun getStringPlural(key: String, locale: String = "en"): StringResource.Plural? =
        withContext(Dispatchers.Default) {
            val plural = queries.getStringPlural(key, locale).executeAsOneOrNull()
                ?: return@withContext null

            val quantities = queries.getStringPluralQuantities(plural.id).executeAsList()
                .associate { PluralQuantity.fromString(it.quantity) to it.value_ }

            StringResource.Plural(
                key = plural.key,
                quantities = quantities,
                locale = plural.locale,
                description = plural.description
            )
        }

    /**
     * Delete a string plural
     */
    suspend fun deleteStringPlural(key: String, locale: String = "en") = withContext(Dispatchers.Default) {
        queries.deleteStringPlural(key, locale)
    }

}
