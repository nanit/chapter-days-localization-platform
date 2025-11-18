package com.nanit.localization.testing

import com.nanit.localization.LocalizationEnvironment
import com.nanit.localization.LocalizationDatabaseManager
import com.nanit.localization.importer.JsonLocalizationImporter

/**
 * Comprehensive test demonstration of the localization system
 *
 * This class demonstrates:
 * 1. Importing JSON localization files
 * 2. Querying strings in different locales
 * 3. Fallback mechanisms
 * 4. All three resource types (Values, Arrays, Plurals)
 */
class LocalizationTestDemo(private val manager: LocalizationDatabaseManager) {

    private val importer = JsonLocalizationImporter(manager)

    /**
     * Main test workflow - demonstrates the complete system
     */
    suspend fun runCompleteDemo(): DemoResult {
        val results = mutableListOf<String>()

        results.add("=".repeat(60))
        results.add("LOCALIZATION SYSTEM TEST DEMO")
        results.add("=".repeat(60))
        results.add("")

        // Step 1: Import mock JSON data
        results.add("STEP 1: Importing JSON localization files")
        results.add("-".repeat(60))
        val importResults = importMockData()
        results.add(importResults.printSummary())

        // Step 2: Test string values
        results.add("STEP 2: Testing String Values")
        results.add("-".repeat(60))
        results.addAll(testStringValues())

        // Step 3: Test string arrays
        results.add("\nSTEP 3: Testing String Arrays")
        results.add("-".repeat(60))
        results.addAll(testStringArrays())

        // Step 4: Test string plurals
        results.add("\nSTEP 4: Testing String Plurals")
        results.add("-".repeat(60))
        results.addAll(testStringPlurals())

        // Step 5: Test locale fallback
        results.add("\nSTEP 5: Testing Locale Fallback")
        results.add("-".repeat(60))
        results.addAll(testLocaleFallback())

        // Step 6: Test missing keys
        results.add("\nSTEP 6: Testing Missing Keys")
        results.add("-".repeat(60))
        results.addAll(testMissingKeys())

        results.add("\n" + "=".repeat(60))
        results.add("DEMO COMPLETED SUCCESSFULLY")
        results.add("=".repeat(60))

        return DemoResult(
            success = true,
            output = results.joinToString("\n")
        )
    }

    /**
     * Import mock JSON data for testing
     */
    private suspend fun importMockData(): com.nanit.localization.importer.MultiImportResult {
        val mockData = mapOf(
            "en" to getMockJsonEn(),
            "es" to getMockJsonEs(),
            "fr" to getMockJsonFr()
        )

        return importer.importMultipleJson(mockData)
    }

    /**
     * Test string value queries
     */
    private suspend fun testStringValues(): List<String> {
        val results = mutableListOf<String>()

        val testCases = listOf(
            Triple("en", "app_name", "English"),
            Triple("es", "app_name", "Spanish"),
            Triple("fr", "welcome_message", "French"),
            Triple("en", "login_button", "English"),
            Triple("es", "settings_title", "Spanish")
        )

        testCases.forEach { (locale, key, language) ->
            val env = LocalizationEnvironment(locale = locale)
            val value = manager.loadString(key, env)
            results.add("[$language] $key = \"$value\"")
        }

        return results
    }

    /**
     * Test string array queries
     */
    private suspend fun testStringArrays(): List<String> {
        val results = mutableListOf<String>()

        val testCases = listOf(
            Pair("en", "days_of_week"),
            Pair("es", "days_of_week"),
            Pair("fr", "months"),
            Pair("en", "navigation_items")
        )

        testCases.forEach { (locale, key) ->
            val env = LocalizationEnvironment(locale = locale)
            val array = manager.loadStringArray(key, env)
            results.add("[$locale] $key:")
            array?.forEachIndexed { index, item ->
                results.add("  [$index] $item")
            }
            results.add("")
        }

        return results
    }

    /**
     * Test string plural queries
     */
    private suspend fun testStringPlurals(): List<String> {
        val results = mutableListOf<String>()

        val testCases = listOf(
            Triple("en", "items_in_cart", listOf(0, 1, 5)),
            Triple("es", "unread_messages", listOf(0, 1, 10)),
            Triple("fr", "days_remaining", listOf(0, 1, 2, 7)),
            Triple("en", "files_selected", listOf(0, 1, 3))
        )

        testCases.forEach { (locale, key, quantities) ->
            val env = LocalizationEnvironment(locale = locale)
            results.add("[$locale] $key:")
            quantities.forEach { count ->
                val text = manager.loadStringPlural(key, count, env)
                results.add("  count=$count → \"$text\"")
            }
            results.add("")
        }

        return results
    }

    /**
     * Test locale fallback mechanism
     */
    private suspend fun testLocaleFallback(): List<String> {
        val results = mutableListOf<String>()

        results.add("Testing fallback from German (de) to English (en):")

        val env = LocalizationEnvironment(
            locale = "de",          // German not imported
            fallbackLocale = "en"   // Should fall back to English
        )

        val testKeys = listOf("app_name", "welcome_message", "login_button")

        testKeys.forEach { key ->
            val value = manager.loadString(key, env)
            results.add("  $key = \"$value\" (fallback to English)")
        }

        return results
    }

    /**
     * Test handling of missing keys
     */
    private suspend fun testMissingKeys(): List<String> {
        val results = mutableListOf<String>()

        val env = LocalizationEnvironment(locale = "en")

        results.add("Querying non-existent keys:")

        val nonExistentKey = "this_key_does_not_exist"
        val value = manager.loadString(nonExistentKey, env)
        results.add("  $nonExistentKey = ${value ?: "null (key not found)"}")

        val valueWithDefault = manager.loadStringOrDefault(
            nonExistentKey,
            default = "Default Value",
            env = env
        )
        results.add("  $nonExistentKey (with default) = \"$valueWithDefault\"")

        return results
    }

    /**
     * Mock JSON data - English
     */
    private fun getMockJsonEn(): String = """
{
  "locale": "en",
  "values": [
    {
      "key": "app_name",
      "value": "Localization Platform",
      "description": "Application name"
    },
    {
      "key": "welcome_message",
      "value": "Welcome to our application!",
      "description": "Main welcome message"
    },
    {
      "key": "login_button",
      "value": "Log In"
    },
    {
      "key": "settings_title",
      "value": "Settings"
    }
  ],
  "arrays": [
    {
      "key": "days_of_week",
      "items": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
    },
    {
      "key": "navigation_items",
      "items": ["Home", "Profile", "Settings", "Help"]
    }
  ],
  "plurals": [
    {
      "key": "items_in_cart",
      "quantities": {
        "zero": "No items in cart",
        "one": "1 item in cart",
        "other": "%d items in cart"
      }
    },
    {
      "key": "files_selected",
      "quantities": {
        "zero": "No files selected",
        "one": "1 file selected",
        "other": "%d files selected"
      }
    }
  ]
}
    """.trimIndent()

    /**
     * Mock JSON data - Spanish
     */
    private fun getMockJsonEs(): String = """
{
  "locale": "es",
  "values": [
    {
      "key": "app_name",
      "value": "Plataforma de Localización"
    },
    {
      "key": "welcome_message",
      "value": "¡Bienvenido a nuestra aplicación!"
    },
    {
      "key": "login_button",
      "value": "Iniciar Sesión"
    },
    {
      "key": "settings_title",
      "value": "Configuración"
    }
  ],
  "arrays": [
    {
      "key": "days_of_week",
      "items": ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"]
    }
  ],
  "plurals": [
    {
      "key": "unread_messages",
      "quantities": {
        "zero": "No hay mensajes sin leer",
        "one": "1 mensaje sin leer",
        "other": "%d mensajes sin leer"
      }
    }
  ]
}
    """.trimIndent()

    /**
     * Mock JSON data - French
     */
    private fun getMockJsonFr(): String = """
{
  "locale": "fr",
  "values": [
    {
      "key": "app_name",
      "value": "Plateforme de Localisation"
    },
    {
      "key": "welcome_message",
      "value": "Bienvenue dans notre application!"
    }
  ],
  "arrays": [
    {
      "key": "months",
      "items": ["Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"]
    }
  ],
  "plurals": [
    {
      "key": "days_remaining",
      "quantities": {
        "zero": "Aucun jour restant",
        "one": "1 jour restant",
        "two": "2 jours restants",
        "other": "%d jours restants"
      }
    }
  ]
}
    """.trimIndent()
}

/**
 * Result of running the demo
 */
data class DemoResult(
    val success: Boolean,
    val output: String,
    val error: String? = null
)
