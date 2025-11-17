# Testing Guide - Localization Module

This guide demonstrates how to test the localization system with mock JSON data and database interactions.

## Overview

The testing framework includes:
- **Mock JSON files** with localization data for multiple languages
- **JSON Importer** to load data into the database
- **Integration tests** demonstrating the complete workflow
- **Unit tests** for individual components

## Mock JSON Data Structure

### File Format

```json
{
  "locale": "en",
  "values": [
    {
      "key": "app_name",
      "value": "My Application",
      "description": "Optional description"
    }
  ],
  "arrays": [
    {
      "key": "days_of_week",
      "items": ["Monday", "Tuesday", "Wednesday"],
      "description": "Optional description"
    }
  ],
  "plurals": [
    {
      "key": "items_count",
      "quantities": {
        "zero": "No items",
        "one": "1 item",
        "other": "%d items"
      },
      "description": "Optional description"
    }
  ]
}
```

### Available Mock Files

- `shared/src/commonMain/resources/localizations/strings_en.json` - English
- `shared/src/commonMain/resources/localizations/strings_es.json` - Spanish
- `shared/src/commonMain/resources/localizations/strings_fr.json` - French

## Using the JSON Importer

### Basic Import

```kotlin
import com.nanit.localization.LocalizationManager
import com.nanit.localization.database.DatabaseDriverFactory
import com.nanit.localization.importer.JsonLocalizationImporter

// Initialize
val manager = LocalizationManager(DatabaseDriverFactory())
val importer = JsonLocalizationImporter(manager)

// Import from JSON string
val jsonString = """
{
  "locale": "en",
  "values": [
    {
      "key": "greeting",
      "value": "Hello, World!"
    }
  ]
}
"""

val result = importer.importFromJson(jsonString)
println(result.toString())
// Output: ✓ Successfully imported 1 items for locale 'en' (1 values, 0 arrays, 0 plurals)
```

### Import Multiple Locales

```kotlin
val jsonFiles = mapOf(
    "en" to readJsonFile("strings_en.json"),
    "es" to readJsonFile("strings_es.json"),
    "fr" to readJsonFile("strings_fr.json")
)

val results = importer.importMultipleJson(jsonFiles)
println(results.printSummary())
```

### Import Result

```kotlin
data class ImportResult(
    val success: Boolean,
    val locale: String?,
    val error: String?,
    val valuesImported: Int,
    val arraysImported: Int,
    val pluralsImported: Int
)
```

## Running the Test Demo

### Complete Workflow Demo

```kotlin
import com.nanit.localization.testing.LocalizationTestDemo

val manager = LocalizationManager(DatabaseDriverFactory())
val demo = LocalizationTestDemo(manager)

// Run complete demonstration
val result = demo.runCompleteDemo()
println(result.output)
```

### Demo Output Example

```
============================================================
LOCALIZATION SYSTEM TEST DEMO
============================================================

STEP 1: Importing JSON localization files
------------------------------------------------------------
=== Import Summary ===
Total locales: 3
Successful: 3
Failed: 0
Total items imported: 45

✓ Successfully imported 15 items for locale 'en' (8 values, 2 arrays, 4 plurals)
✓ Successfully imported 12 items for locale 'es' (8 values, 1 arrays, 1 plurals)
✓ Successfully imported 18 items for locale 'fr' (8 values, 1 arrays, 1 plurals)

STEP 2: Testing String Values
------------------------------------------------------------
[English] app_name = "Localization Platform"
[Spanish] app_name = "Plataforma de Localización"
[French] welcome_message = "Bienvenue dans notre application!"
[English] login_button = "Log In"
[Spanish] settings_title = "Configuración"

STEP 3: Testing String Arrays
------------------------------------------------------------
[en] days_of_week:
  [0] Monday
  [1] Tuesday
  [2] Wednesday
  [3] Thursday
  [4] Friday
  [5] Saturday
  [6] Sunday

[es] days_of_week:
  [0] Lunes
  [1] Martes
  [2] Miércoles
  [3] Jueves
  [4] Viernes
  [5] Sábado
  [6] Domingo

STEP 4: Testing String Plurals
------------------------------------------------------------
[en] items_in_cart:
  count=0 → "No items in cart"
  count=1 → "1 item in cart"
  count=5 → "%d items in cart"

[es] unread_messages:
  count=0 → "No hay mensajes sin leer"
  count=1 → "1 mensaje sin leer"
  count=10 → "%d mensajes sin leer"

STEP 5: Testing Locale Fallback
------------------------------------------------------------
Testing fallback from German (de) to English (en):
  app_name = "Localization Platform" (fallback to English)
  welcome_message = "Welcome to our application!" (fallback to English)
  login_button = "Log In" (fallback to English)

STEP 6: Testing Missing Keys
------------------------------------------------------------
Querying non-existent keys:
  this_key_does_not_exist = null (key not found)
  this_key_does_not_exist (with default) = "Default Value"

============================================================
DEMO COMPLETED SUCCESSFULLY
============================================================
```

## Running Unit Tests

### Execute All Tests

```bash
./gradlew :shared:test
```

### Execute Specific Test

```bash
./gradlew :shared:test --tests "LocalizationIntegrationTest.testCompleteLocalizationWorkflow"
```

### Available Unit Tests

1. **testCompleteLocalizationWorkflow** - Full integration test with JSON import
2. **testStringValueQueries** - Test simple string value storage and retrieval
3. **testStringArrayQueries** - Test array storage and retrieval
4. **testStringPluralQueries** - Test plural forms with different quantities
5. **testLocaleFallback** - Test fallback to default locale
6. **testMissingKey** - Test handling of non-existent keys
7. **testUpdateString** - Test updating existing strings
8. **testDeleteString** - Test deletion of strings

## Test Examples

### Example 1: Basic String Test

```kotlin
@Test
fun testBasicString() = runTest {
    val manager = LocalizationManager(DatabaseDriverFactory())

    // Store
    manager.storeString("greeting", "Hello", "en")

    // Query
    val env = LocalizationEnvironment(locale = "en")
    val value = manager.loadString("greeting", env)

    // Verify
    assertEquals("Hello", value)
}
```

### Example 2: Multi-Locale Test

```kotlin
@Test
fun testMultiLocale() = runTest {
    val manager = LocalizationManager(DatabaseDriverFactory())

    // Store multiple locales
    manager.storeString("app_name", "My App", "en")
    manager.storeString("app_name", "Mi App", "es")
    manager.storeString("app_name", "Mon App", "fr")

    // Query each locale
    val enValue = manager.loadString("app_name", LocalizationEnvironment("en"))
    val esValue = manager.loadString("app_name", LocalizationEnvironment("es"))
    val frValue = manager.loadString("app_name", LocalizationEnvironment("fr"))

    // Verify
    assertEquals("My App", enValue)
    assertEquals("Mi App", esValue)
    assertEquals("Mon App", frValue)
}
```

### Example 3: Array Test

```kotlin
@Test
fun testArray() = runTest {
    val manager = LocalizationManager(DatabaseDriverFactory())

    // Store array
    val colors = listOf("Red", "Green", "Blue", "Yellow")
    manager.storeStringArray("colors", colors, "en")

    // Query
    val env = LocalizationEnvironment("en")
    val retrieved = manager.loadStringArray("colors", env)

    // Verify
    assertNotNull(retrieved)
    assertEquals(4, retrieved.size)
    assertEquals("Red", retrieved[0])
    assertEquals("Yellow", retrieved[3])
}
```

### Example 4: Plural Test

```kotlin
@Test
fun testPlurals() = runTest {
    val manager = LocalizationManager(DatabaseDriverFactory())

    // Store plurals
    manager.storeStringPlural(
        key = "messages",
        quantities = mapOf(
            PluralQuantity.ZERO to "No messages",
            PluralQuantity.ONE to "1 message",
            PluralQuantity.OTHER to "%d messages"
        ),
        locale = "en"
    )

    val env = LocalizationEnvironment("en")

    // Test different quantities
    assertEquals("No messages", manager.loadStringPlural("messages", 0, env))
    assertEquals("1 message", manager.loadStringPlural("messages", 1, env))
    assertEquals("%d messages", manager.loadStringPlural("messages", 5, env))
}
```

### Example 5: Fallback Test

```kotlin
@Test
fun testFallback() = runTest {
    val manager = LocalizationManager(DatabaseDriverFactory())

    // Only store English
    manager.storeString("title", "Welcome", "en")

    // Query with German, fallback to English
    val env = LocalizationEnvironment(
        locale = "de",
        fallbackLocale = "en"
    )
    val value = manager.loadString("title", env)

    // Should get English value
    assertEquals("Welcome", value)
}
```

## Database Interaction Flow

```
┌─────────────┐
│  JSON File  │
└──────┬──────┘
       │
       ▼
┌─────────────────┐
│ JSON Importer   │
│  - Parse JSON   │
│  - Validate     │
└──────┬──────────┘
       │
       ▼
┌─────────────────────┐
│ LocalizationManager │
└──────┬──────────────┘
       │
       ▼
┌────────────────────┐
│ Repository Layer   │
│  - Insert/Update   │
│  - Query           │
│  - Delete          │
└──────┬─────────────┘
       │
       ▼
┌────────────────────┐
│  SQLite Database   │
│  - StringValue     │
│  - StringArray     │
│  - StringPlural    │
└────────────────────┘
```

## Performance Testing

### Benchmark Large Import

```kotlin
@Test
fun benchmarkLargeImport() = runTest {
    val manager = LocalizationManager(DatabaseDriverFactory())
    val importer = JsonLocalizationImporter(manager)

    // Generate large JSON with 1000 strings
    val largeJson = generateLargeJson(itemCount = 1000)

    val startTime = System.currentTimeMillis()
    val result = importer.importFromJson(largeJson)
    val duration = System.currentTimeMillis() - startTime

    println("Imported ${result.totalImported} items in ${duration}ms")
    assertTrue(result.success)
}
```

## Best Practices

1. **Always use in-memory database for tests** to avoid file system dependencies
2. **Test all three resource types** (Values, Arrays, Plurals) in integration tests
3. **Test locale fallback** to ensure graceful degradation
4. **Test missing keys** to verify error handling
5. **Use descriptive test names** that explain what is being tested
6. **Clean up after tests** (though in-memory DB handles this automatically)

## Troubleshooting

### Common Issues

1. **JSON Parse Error**: Verify JSON syntax is valid
2. **Database Lock**: Ensure only one test runs at a time
3. **Missing Translations**: Check locale spelling and JSON import success
4. **Plural Not Working**: Verify quantity keys match PluralQuantity enum values

## Next Steps

- Add more locales to test comprehensive internationalization
- Create performance benchmarks for large datasets
- Test concurrent access patterns
- Add stress tests for database limits
