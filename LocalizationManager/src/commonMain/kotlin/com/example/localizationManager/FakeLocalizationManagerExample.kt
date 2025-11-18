package com.example.localizationManager

import app.cash.sqldelight.db.SqlDriver
import com.example.localizationManager.api.FakeLocalizationApiClient
import com.example.localizationManager.database.database.SqlDriverProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Example usage of LocalizationManager with FakeLocalizationApiClient.
 * This can be used for manual testing and development.
 */

/**
 * Fake SqlDriverProvider for testing/examples without actual database
 */
class FakeSqlDriverProvider : SqlDriverProvider {
    override fun createDriver(): SqlDriver {
        throw UnsupportedOperationException("Fake driver - not meant for actual use. Use platform-specific driver provider.")
    }
}

/**
 * Simple LocaleProvider implementation for testing.
 * Allows manual locale changes via updateLocale() method.
 */
class TestLocaleProvider(
    private val initialLocale: String = "en"
) : LocaleProvider {

    private val _currentLocale = MutableStateFlow(LocaleInfo(initialLocale))

    override fun getCurrentLocale(): LocaleInfo {
        return _currentLocale.value
    }

    override fun observeLocalChanges(): Flow<LocaleInfo> {
        return _currentLocale
    }

    /**
     * Update the current locale (for testing locale changes)
     */
    fun updateLocale(languageCode: String) {
        _currentLocale.value = LocaleInfo(languageCode)
    }
}

/**
 * Create a LocalizationManager configured with fake data for testing.
 *
 * Example usage:
 * ```
 * // Create manager with fake data
 * val localeProvider = TestLocaleProvider("en")
 * val manager = createFakeLocalizationManager(localeProvider)
 *
 * // Initialize
 * manager.initialize()
 *
 * // Get strings
 * val welcomeMsg = manager.getString("welcome_message") // "Welcome to our app!"
 *
 * // Change locale
 * localeProvider.updateLocale("es")
 * val welcomeMsgSpanish = manager.getString("welcome_message") // "¡Bienvenido a nuestra aplicación!"
 * ```
 *
 * @param localeProvider Provider for locale information (use TestLocaleProvider for testing)
 * @param simulateDelay Whether to simulate network delay (default: false for faster testing)
 * @param delayMillis Network delay in milliseconds if simulateDelay is true
 * @return Configured LocalizationManager with fake API client
 */
fun createFakeLocalizationManager(
    localeProvider: LocaleProvider,
    simulateDelay: Boolean = false,
    delayMillis: Long = 500L
): LocalizationManager {
    val fakeApiClient = FakeLocalizationApiClient(
        simulateDelay = simulateDelay,
        delayMillis = delayMillis
    )

    val config = LocalizationManagerConfig(
        localeProvider = localeProvider,
        sqlDriverProvider = FakeSqlDriverProvider(),
        cacheSize = 1_000
    )

    return LocalizationManager(
        config = config,
        apiClient = fakeApiClient
    )
}

/**
 * Available test locales in FakeLocalizationApiClient:
 * - "en" - English
 * - "es" - Spanish
 * - "fr" - French
 * - "he" - Hebrew
 * - "de" - German
 *
 * Available test string keys:
 * - "welcome_message"
 * - "login_button"
 * - "signup_button"
 * - "settings_title"
 * - "profile_title"
 * - "logout_button"
 * - "greeting"
 * - "goodbye"
 */