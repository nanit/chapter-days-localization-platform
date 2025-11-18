# Localization Module

A Kotlin Multiplatform module for managing localized string resources with SQL database support.

## Features

- **Cross-platform support**: Android, iOS, JVM, JS, and WebAssembly
- **Three string resource types**:
  - **Values**: Simple key-value string pairs
  - **Arrays**: Lists of strings
  - **Plurals**: Strings with different forms based on quantity (zero, one, two, few, many, other)
- **Multi-locale support**: Store and retrieve strings in multiple languages
- **Fallback mechanism**: Automatic fallback to default locale if translation is not available
- **SQLite database**: Persistent storage using SQLDelight
- **Coroutine-based**: Async/await API for all operations

## Architecture

```
shared/
├── src/
│   ├── commonMain/
│   │   ├── kotlin/
│   │   │   └── com/nanit/localization/
│   │   │       ├── database/
│   │   │       │   ├── DatabaseDriverFactory.kt (expect)
│   │   │       │   ├── LocalizationRepository.kt
│   │   │       │   └── ...
│   │   │       ├── model/
│   │   │       │   └── StringResource.kt
│   │   │       ├── LocalizationManager.kt
│   │   │       ├── StringLoader.kt
│   │   │       └── LocalizationExample.kt
│   │   └── sqldelight/
│   │       └── com/nanit/localization/database/
│   │           └── LocalizationDatabase.sq
│   ├── androidMain/
│   │   └── kotlin/.../DatabaseDriverFactory.android.kt (actual)
│   ├── iosMain/
│   │   └── kotlin/.../DatabaseDriverFactory.ios.kt (actual)
│   ├── jvmMain/
│   │   └── kotlin/.../DatabaseDriverFactory.jvm.kt (actual)
│   ├── jsMain/
│   │   └── kotlin/.../DatabaseDriverFactory.js.kt (actual)
│   └── wasmJsMain/
│       └── kotlin/.../DatabaseDriverFactory.wasmJs.kt (actual)
```

## Usage

### 1. Initialize LocalizationManager

Platform-specific initialization:

#### Android
```kotlin
// In your Application or Activity
val driverFactory = DatabaseDriverFactory(context)
val localizationManager = LocalizationManager(driverFactory)
```

#### iOS
```kotlin
val driverFactory = DatabaseDriverFactory()
val localizationManager = LocalizationManager(driverFactory)
```

#### JVM / JS / WebAssembly
```kotlin
val driverFactory = DatabaseDriverFactory()
val localizationManager = LocalizationManager(driverFactory)
```

### 2. Store String Resources

#### Simple String Values
```kotlin
suspend fun storeStrings(manager: LocalizationManager) {
    // English
    manager.storeString(
        key = "greeting",
        value = "Hello, World!",
        locale = "en"
    )

    // Spanish
    manager.storeString(
        key = "greeting",
        value = "¡Hola, Mundo!",
        locale = "es"
    )
}
```

#### String Arrays
```kotlin
suspend fun storeArrays(manager: LocalizationManager) {
    manager.storeStringArray(
        key = "colors",
        items = listOf("Red", "Green", "Blue"),
        locale = "en"
    )

    manager.storeStringArray(
        key = "colors",
        items = listOf("Rojo", "Verde", "Azul"),
        locale = "es"
    )
}
```

#### String Plurals
```kotlin
suspend fun storePlurals(manager: LocalizationManager) {
    manager.storeStringPlural(
        key = "message_count",
        quantities = mapOf(
            PluralQuantity.ZERO to "No messages",
            PluralQuantity.ONE to "One message",
            PluralQuantity.OTHER to "%d messages"
        ),
        locale = "en"
    )
}
```

### 3. Load String Resources

#### Basic Loading
```kotlin
suspend fun loadStrings(manager: LocalizationManager) {
    // Create environment with locale
    val env = LocalizationEnvironment(
        locale = "en",
        fallbackLocale = "en"
    )

    // Load simple string
    val greeting = manager.loadString("greeting", env)
    println(greeting) // "Hello, World!"

    // Load string array
    val colors = manager.loadStringArray("colors", env)
    println(colors) // ["Red", "Green", "Blue"]

    // Load plural string
    val count0 = manager.loadStringPlural("message_count", 0, env)
    val count1 = manager.loadStringPlural("message_count", 1, env)
    val count5 = manager.loadStringPlural("message_count", 5, env)
    println(count0) // "No messages"
    println(count1) // "One message"
    println(count5) // "%d messages"
}
```

#### With Formatting
```kotlin
suspend fun loadFormattedString(manager: LocalizationManager) {
    manager.storeString(
        key = "welcome",
        value = "Welcome, %s!",
        locale = "en"
    )

    val env = LocalizationEnvironment(locale = "en")
    val message = manager.loadString("welcome", env, "John")
    println(message) // "Welcome, John!"
}
```

#### With Fallback Locale
```kotlin
suspend fun loadWithFallback(manager: LocalizationManager) {
    val env = LocalizationEnvironment(
        locale = "fr",           // Try French first
        fallbackLocale = "en"    // Fall back to English if not found
    )

    // If French translation doesn't exist, returns English
    val greeting = manager.loadString("greeting", env)
}
```

### 4. Update and Delete Resources

```kotlin
suspend fun updateAndDelete(manager: LocalizationManager) {
    // Update existing string
    manager.updateString(
        key = "greeting",
        value = "Hi, World!",
        locale = "en"
    )

    // Delete resources
    manager.deleteString("greeting", "en")
    manager.deleteStringArray("colors", "en")
    manager.deleteStringPlural("message_count", "en")
}
```

## API Reference

### LocalizationManager

Main facade for managing localization.

#### String Value Methods
- `storeString(key, value, locale, description?)`: Store a simple string
- `updateString(key, value, locale, description?)`: Update existing string
- `loadString(key, env)`: Load string value
- `loadString(key, env, ...args)`: Load string with formatting
- `loadStringOrDefault(key, default, env)`: Load string with default fallback
- `deleteString(key, locale)`: Delete string value

#### String Array Methods
- `storeStringArray(key, items, locale, description?)`: Store string array
- `loadStringArray(key, env)`: Load string array
- `deleteStringArray(key, locale)`: Delete string array

#### String Plural Methods
- `storeStringPlural(key, quantities, locale, description?)`: Store string plural
- `loadStringPlural(key, quantity, env)`: Load plural for specific quantity
- `deleteStringPlural(key, locale)`: Delete string plural

### LocalizationEnvironment

Configuration for loading strings.

```kotlin
data class LocalizationEnvironment(
    val locale: String = "en",
    val fallbackLocale: String = "en"
)
```

### PluralQuantity

Enum for plural forms (ICU standard):
- `ZERO`: For zero items
- `ONE`: For one item
- `TWO`: For two items
- `FEW`: For a few items (language-dependent)
- `MANY`: For many items (language-dependent)
- `OTHER`: Default/fallback form

## Database Schema

The module uses SQLite with the following tables:

- **StringValue**: Simple key-value string pairs
- **StringArray**: Array metadata
- **StringArrayItem**: Individual array items
- **StringPlural**: Plural metadata
- **StringPluralQuantity**: Plural quantity forms

All tables include:
- Unique constraints on (key, locale)
- Timestamps (created_at, updated_at)
- Optional descriptions

## Dependencies

The module uses SQLDelight for database operations:
- `app.cash.sqldelight:runtime`
- `app.cash.sqldelight:coroutines-extensions`
- Platform-specific drivers (android-driver, native-driver, sqlite-driver, web-worker-driver)

## Integration with Compose Multiplatform

You can use this module in your Compose Multiplatform app to provide localized strings:

```kotlin
@Composable
fun MyScreen(localizationManager: LocalizationManager) {
    val locale = remember { mutableStateOf("en") }
    val greeting by produceState<String?>(null) {
        value = localizationManager.loadString(
            "greeting",
            LocalizationEnvironment(locale = locale.value)
        )
    }

    Text(text = greeting ?: "Loading...")
}
```

## License

[Your License Here]
