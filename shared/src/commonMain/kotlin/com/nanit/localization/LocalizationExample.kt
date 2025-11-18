package com.nanit.localization

import com.nanit.localization.database.SqlDriverProvider
import com.nanit.localization.model.PluralQuantity

/**
 * Example usage of the LocalizationManager
 *
 * This demonstrates how to use the localization module in a Compose Multiplatform application
 */
object LocalizationExample {

    /**
     * Initialize the localization manager
     *
     * Platform-specific initialization:
     * - Android: LocalizationDatabaseManager(AndroidSqlDriverProvider(context))
     * - iOS: LocalizationDatabaseManager(IosSqlDriverProvider())
     * - JVM: LocalizationDatabaseManager(JvmSqlDriverProvider())
     * - JS: LocalizationDatabaseManager(JsSqlDriverProvider())
     */
    fun createManager(sqlDriverProvider: SqlDriverProvider): LocalizationDatabaseManager {
        return LocalizationDatabaseManager(sqlDriverProvider)
    }

    /**
     * Example: Store and load simple string values
     */
    suspend fun exampleStringValues(manager: LocalizationDatabaseManager) {
        // Store strings in different locales
        manager.storeString(
            key = "app_name",
            value = "My Application",
            locale = "en",
            description = "Application name"
        )

        manager.storeString(
            key = "app_name",
            value = "Mi Aplicación",
            locale = "es"
        )

        // Load strings
        val env = LocalizationEnvironment(locale = "en")
        val appName = manager.loadString("app_name", env)
        println("App name (en): $appName")

        val envEs = LocalizationEnvironment(locale = "es", fallbackLocale = "en")
        val appNameEs = manager.loadString("app_name", envEs)
        println("App name (es): $appNameEs")
    }

    /**
     * Example: Store and load string arrays
     */
    suspend fun exampleStringArrays(manager: LocalizationDatabaseManager) {
        // Store a string array
        manager.storeStringArray(
            key = "days_of_week",
            items = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"),
            locale = "en"
        )

        manager.storeStringArray(
            key = "days_of_week",
            items = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"),
            locale = "es"
        )

        // Load the array
        val env = LocalizationEnvironment(locale = "en")
        val days = manager.loadStringArray("days_of_week", env)
        println("Days (en): ${days?.joinToString(", ")}")
    }

    /**
     * Example: Store and load plurals
     */
    suspend fun exampleStringPlurals(manager: LocalizationDatabaseManager) {
        // Store plural forms
        manager.storeStringPlural(
            key = "items_count",
            quantities = mapOf(
                PluralQuantity.ZERO to "No items",
                PluralQuantity.ONE to "One item",
                PluralQuantity.OTHER to "%d items"
            ),
            locale = "en"
        )

        manager.storeStringPlural(
            key = "items_count",
            quantities = mapOf(
                PluralQuantity.ZERO to "Ningún elemento",
                PluralQuantity.ONE to "Un elemento",
                PluralQuantity.OTHER to "%d elementos"
            ),
            locale = "es"
        )

        // Load plural strings
        val env = LocalizationEnvironment(locale = "en")
        println("Count 0: ${manager.loadStringPlural("items_count", 0, env)}")
        println("Count 1: ${manager.loadStringPlural("items_count", 1, env)}")
        println("Count 5: ${manager.loadStringPlural("items_count", 5, env)}")
    }

}
