# Test Factory Fix - DatabaseDriverFactory

## Problem

The `DatabaseDriverFactory` is an `expect` class with platform-specific constructors:

```kotlin
// Common
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

// Android - requires Context
actual class DatabaseDriverFactory(private val context: Context)

// iOS, JVM, JS - no parameters
actual class DatabaseDriverFactory()
```

In common test code (`commonTest`), you cannot directly instantiate `DatabaseDriverFactory()` because:
1. Android requires a `Context` parameter
2. The common code doesn't know which platform it will run on

**Error:**
```
DatabaseDriverFactory() does not have default constructor
```

## Solution

Created a test helper using the `expect/actual` pattern:

### 1. Common Test Helper

**`shared/src/commonTest/kotlin/com/nanit/localization/TestDatabaseDriverFactory.kt`**
```kotlin
expect fun createTestDatabaseDriverFactory(): DatabaseDriverFactory
```

### 2. Platform-Specific Implementations

#### JVM Test
**`shared/src/jvmTest/kotlin/com/nanit/localization/TestDatabaseDriverFactory.jvm.kt`**
```kotlin
actual fun createTestDatabaseDriverFactory(): DatabaseDriverFactory {
    return DatabaseDriverFactory()
}
```

#### iOS Test
**`shared/src/iosTest/kotlin/com/nanit/localization/TestDatabaseDriverFactory.ios.kt`**
```kotlin
actual fun createTestDatabaseDriverFactory(): DatabaseDriverFactory {
    return DatabaseDriverFactory()
}
```

#### JS Test
**`shared/src/jsTest/kotlin/com/nanit/localization/TestDatabaseDriverFactory.js.kt`**
```kotlin
actual fun createTestDatabaseDriverFactory(): DatabaseDriverFactory {
    return DatabaseDriverFactory()
}
```

#### Android Test
**`shared/src/androidUnitTest/kotlin/com/nanit/localization/TestDatabaseDriverFactory.android.kt`**
```kotlin
actual fun createTestDatabaseDriverFactory(): DatabaseDriverFactory {
    val context = ApplicationProvider.getApplicationContext<Application>()
    return DatabaseDriverFactory(context)
}
```

## Usage in Tests

### Before (Broken)
```kotlin
@Test
fun testExample() = runTest {
    val driverFactory = DatabaseDriverFactory() // ❌ Error!
    val manager = LocalizationManager(driverFactory)
    // ...
}
```

### After (Fixed)
```kotlin
@Test
fun testExample() = runTest {
    val driverFactory = createTestDatabaseDriverFactory() // ✅ Works!
    val manager = LocalizationManager(driverFactory)
    // ...
}
```

## Benefits

1. **Cross-platform tests**: Tests run on all platforms without modification
2. **No code duplication**: Test logic stays in `commonTest`
3. **Platform-appropriate setup**: Each platform gets the correct driver configuration
4. **Type-safe**: Compiler ensures all platforms are implemented

## Test Verification

All 8 tests pass successfully:

```bash
./gradlew :shared:test
```

✅ testCompleteLocalizationWorkflow
✅ testStringValueQueries
✅ testStringArrayQueries
✅ testStringPluralQueries
✅ testLocaleFallback
✅ testMissingKey
✅ testUpdateString
✅ testDeleteString

## Architecture

```
┌─────────────────────────────────────┐
│        commonTest                   │
│  LocalizationIntegrationTest.kt    │
│  │                                  │
│  └─ createTestDatabaseDriverFactory()│
└───────────────┬─────────────────────┘
                │ expect
        ┌───────┴──────────┐
        │                  │
  ┌─────▼─────┐     ┌─────▼─────┐
  │  jvmTest  │     │ androidTest│
  │  (actual) │     │  (actual)  │
  │           │     │            │
  │ return    │     │ return     │
  │ Factory() │     │ Factory(ctx)│
  └───────────┘     └────────────┘
        │                  │
        └─────────┬────────┘
                  │
        ┌─────────▼──────────┐
        │ DatabaseDriverFactory│
        │   (platform-specific)│
        └────────────────────┘
```

## Alternative Approaches Considered

### ❌ Option 1: Duplicate tests per platform
- **Cons**: Code duplication, maintenance nightmare

### ❌ Option 2: Suppress errors with `@Suppress`
- **Cons**: Doesn't actually solve the problem, tests fail at runtime

### ❌ Option 3: Make DatabaseDriverFactory nullable
- **Cons**: Pollutes production code for testing needs

### ✅ Option 4: Test helper with expect/actual (Chosen)
- **Pros**: Clean, maintainable, follows KMM best practices

## Summary

The fix uses Kotlin Multiplatform's `expect/actual` pattern to provide platform-appropriate `DatabaseDriverFactory` instances in tests. This allows common test code to work across all platforms without knowing platform-specific details.

**Key takeaway**: When testing multiplatform code that requires platform-specific setup, use the `expect/actual` pattern in your test source sets.
