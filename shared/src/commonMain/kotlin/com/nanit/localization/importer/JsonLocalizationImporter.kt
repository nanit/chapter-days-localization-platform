package com.nanit.localization.importer

import com.nanit.localization.LocalizationDatabaseManager
import com.nanit.localization.model.PluralQuantity
import kotlinx.serialization.json.Json

/**
 * Imports localization data from JSON format into the database
 */
class JsonLocalizationImporter(private val manager: LocalizationDatabaseManager) {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    /**
     * Import localization data from JSON string
     * @param jsonString JSON string containing localization data
     * @return ImportResult with statistics
     */
    suspend fun importFromJson(jsonString: String): ImportResult {
        return try {
            val data = json.decodeFromString<LocalizationJson>(jsonString)
            importLocalizationData(data)
        } catch (e: Exception) {
            ImportResult(
                success = false,
                error = "Failed to parse JSON: ${e.message}",
                valuesImported = 0,
                arraysImported = 0,
                pluralsImported = 0
            )
        }
    }

    /**
     * Import localization data object
     */
    private suspend fun importLocalizationData(data: LocalizationJson): ImportResult {
        var valuesCount = 0
        var arraysCount = 0
        var pluralsCount = 0
        val errors = mutableListOf<String>()

        // Import string values
        data.values.forEach { jsonValue ->
            try {
                manager.storeString(
                    key = jsonValue.key,
                    value = jsonValue.value,
                    locale = data.locale,
                    description = jsonValue.description
                )
                valuesCount++
            } catch (e: Exception) {
                errors.add("Failed to import value '${jsonValue.key}': ${e.message}")
            }
        }

        // Import string arrays
        data.arrays.forEach { jsonArray ->
            try {
                manager.storeStringArray(
                    key = jsonArray.key,
                    items = jsonArray.items,
                    locale = data.locale,
                    description = jsonArray.description
                )
                arraysCount++
            } catch (e: Exception) {
                errors.add("Failed to import array '${jsonArray.key}': ${e.message}")
            }
        }

        // Import string plurals
        data.plurals.forEach { jsonPlural ->
            try {
                val quantities = jsonPlural.quantities.mapKeys { (key, _) ->
                    PluralQuantity.fromString(key)
                }
                manager.storeStringPlural(
                    key = jsonPlural.key,
                    quantities = quantities,
                    locale = data.locale,
                    description = jsonPlural.description
                )
                pluralsCount++
            } catch (e: Exception) {
                errors.add("Failed to import plural '${jsonPlural.key}': ${e.message}")
            }
        }

        return ImportResult(
            success = errors.isEmpty(),
            locale = data.locale,
            error = if (errors.isNotEmpty()) errors.joinToString("\n") else null,
            valuesImported = valuesCount,
            arraysImported = arraysCount,
            pluralsImported = pluralsCount
        )
    }

    /**
     * Import multiple JSON files
     */
    suspend fun importMultipleJson(jsonStrings: Map<String, String>): MultiImportResult {
        val results = mutableMapOf<String, ImportResult>()

        jsonStrings.forEach { (locale, jsonString) ->
            results[locale] = importFromJson(jsonString)
        }

        return MultiImportResult(results)
    }
}

/**
 * Result of importing a single locale
 */
data class ImportResult(
    val success: Boolean,
    val locale: String? = null,
    val error: String? = null,
    val valuesImported: Int,
    val arraysImported: Int,
    val pluralsImported: Int
) {
    val totalImported: Int
        get() = valuesImported + arraysImported + pluralsImported

    override fun toString(): String {
        return if (success) {
            "✓ Successfully imported $totalImported items for locale '$locale' " +
                    "(${valuesImported} values, ${arraysImported} arrays, ${pluralsImported} plurals)"
        } else {
            "✗ Failed to import locale '$locale': $error"
        }
    }
}

/**
 * Result of importing multiple locales
 */
data class MultiImportResult(
    val results: Map<String, ImportResult>
) {
    val successCount: Int
        get() = results.values.count { it.success }

    val failureCount: Int
        get() = results.values.count { !it.success }

    val totalImported: Int
        get() = results.values.sumOf { it.totalImported }

    fun printSummary(): String {
        val summary = StringBuilder()
        summary.appendLine("=== Import Summary ===")
        summary.appendLine("Total locales: ${results.size}")
        summary.appendLine("Successful: $successCount")
        summary.appendLine("Failed: $failureCount")
        summary.appendLine("Total items imported: $totalImported")
        summary.appendLine()

        results.forEach { (_, result) ->
            summary.appendLine(result.toString())
        }

        return summary.toString()
    }
}
