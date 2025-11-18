package com.nanit.localization.server.data

import com.nanit.localization.server.data.testing.LocalizationTestDemo
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals

/**
 * Integration test for the localization system
 *
 * This test demonstrates:
 * - JSON import functionality
 * - Database operations
 * - Multi-locale support
 * - Fallback mechanisms
 * - All resource types (Values, Arrays, Plurals)
 */
class LocalizationIntegrationTest {

    @Test
    fun testCompleteLocalizationWorkflow() = runTest {
        // Create manager with in-memory database
        val driverFactory = createTestDatabaseDriverFactory()
        val manager = LocalizationManager(driverFactory)

        // Create and run demo
        val demo = LocalizationTestDemo(manager)
        val result = demo.runCompleteDemo()

        // Print results
        println("\n" + result.output)

        // Verify success`
        assertTrue(result.success, "Demo should complete successfully")
    }

    @Test
    fun testStringValueQueries() = runTest {
        val driverFactory = createTestDatabaseDriverFactory()
        val manager = LocalizationManager(driverFactory)

        // Store test data
        manager.storeString("test_key", "Test Value", "en")
        manager.storeString("test_key", "Valor de Prueba", "es")

        // Query English
        val envEn = LocalizationEnvironment(locale = "en")
        val valueEn = manager.loadString("test_key", envEn)
        assertEquals(valueEn, "Test Value", "Should retrieve English value")

        // Query Spanish
        val envEs = LocalizationEnvironment(locale = "es")
        val valueEs = manager.loadString("test_key", envEs)
        assertEquals(valueEs, "Valor de Prueba", "Should retrieve Spanish value")
    }

    @Test
    fun testStringArrayQueries() = runTest {
        val driverFactory = createTestDatabaseDriverFactory()
        val manager = LocalizationManager(driverFactory)

        // Store test array
        val daysEn = listOf("Mon", "Tue", "Wed", "Thu", "Fri")
        manager.storeStringArray("days", daysEn, "en")

        // Query array
        val env = LocalizationEnvironment(locale = "en")
        val retrieved = manager.loadStringArray("days", env)

        assertTrue(retrieved != null, "Should retrieve array")
        assertEquals(retrieved?.size, 5, "Should have 5 items")
        assertEquals(retrieved?.get(0), "Mon", "First item should be Mon")
    }

    @Test
    fun testStringPluralQueries() = runTest {
        val driverFactory = createTestDatabaseDriverFactory()
        val manager = LocalizationManager(driverFactory)

        // Store test plurals
        manager.storeStringPlural(
            key = "items",
            quantities = mapOf(
                com.nanit.localization.model.PluralQuantity.ZERO to "No items",
                com.nanit.localization.model.PluralQuantity.ONE to "1 item",
                com.nanit.localization.model.PluralQuantity.OTHER to "%d items"
            ),
            locale = "en"
        )

        val env = LocalizationEnvironment(locale = "en")

        // Test different quantities
        val zero = manager.loadStringPlural("items", 0, env)
        val one = manager.loadStringPlural("items", 1, env)
        val many = manager.loadStringPlural("items", 5, env)

        assertEquals(zero, "No items", "Should get zero form")
        assertEquals(one, "1 item", "Should get one form")
        assertEquals(many, "%d items", "Should get other form")
    }

    @Test
    fun testLocaleFallback() = runTest {
        val driverFactory = createTestDatabaseDriverFactory()
        val manager = LocalizationManager(driverFactory)

        // Store only English
        manager.storeString("greeting", "Hello", "en")

        // Query German with English fallback
        val env = LocalizationEnvironment(
            locale = "de",
            fallbackLocale = "en"
        )

        val value = manager.loadString("greeting", env)
        assertEquals(value, "Hello", "Should fall back to English")
    }

    @Test
    fun testMissingKey() = runTest {
        val driverFactory = createTestDatabaseDriverFactory()
        val manager = LocalizationManager(driverFactory)

        val env = LocalizationEnvironment(locale = "en")

        // Query non-existent key
        val value = manager.loadString("non_existent", env)
        assertEquals(value, null, "Should return null for missing key")

        // Query with default
        val withDefault = manager.loadStringOrDefault("non_existent", "Default", env)
        assertEquals(withDefault, "Default", "Should return default value")
    }

    @Test
    fun testUpdateString() = runTest {
        val driverFactory = createTestDatabaseDriverFactory()
        val manager = LocalizationManager(driverFactory)

        val env = LocalizationEnvironment(locale = "en")

        // Store initial value
        manager.storeString("key", "Original", "en")
        val original = manager.loadString("key", env)
        assertEquals(original, "Original", "Should get original value")

        // Update value
        manager.updateString("key", "Updated", "en")
        val updated = manager.loadString("key", env)
        assertEquals(updated, "Updated", "Should get updated value")
    }

    @Test
    fun testDeleteString() = runTest {
        val driverFactory = createTestDatabaseDriverFactory()
        val manager = LocalizationManager(driverFactory)

        val env = LocalizationEnvironment(locale = "en")

        // Store and verify
        manager.storeString("temp", "Temporary", "en")
        val before = manager.loadString("temp", env)
        assertEquals(before, "Temporary", "Should exist before delete")

        // Delete and verify
        manager.deleteString("temp", "en")
        val after = manager.loadString("temp", env)
        assertEquals(after, null, "Should be null after delete")
    }
}
