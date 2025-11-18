package com.nanit.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Setup helpers for testing LocalizationManager with different configurations.
 */

/**
 * Simple LocaleProvider for testing purposes.
 * Allows manual locale changes via updateLocale() method.
 */
//class TestLocaleProvider(
//    initialLocale: String = "en"
//) : LocaleProvider {
//    private val _currentLocale = MutableStateFlow(LocaleInfo(initialLocale))
//
//    override fun getCurrentLocale(): LocaleInfo = _currentLocale.value
//
//    override fun observeLocalChanges(): Flow<LocaleInfo> = _currentLocale
//
//    fun updateLocale(languageCode: String) {
//        _currentLocale.value = LocaleInfo(languageCode)
//    }
//}

/**
 * Test with FAKE LocalizationManager.
 * Uses FakeLocalizationApiClient with mock data.
 *
 * Usage in App.kt:
 * ```
 * LocalizationTestWithFakeData()
 * ```
 */
//@Composable
//fun LocalizationTestWithFakeData() {
//    // Create test locale provider
//    val localeProvider = remember { TestLocaleProvider("en") }
//
//    // Initialize NanitLocalization with fake data on first composition
//    LaunchedEffect(Unit) {
//        val config = LocalizationManagerConfig(
//            localeProvider = localeProvider,
//            cacheSize = 1_000
//        )
//        NanitLocalization.initialize(config)
//    }
//
//    // Display test screen with locale change callback
//    LocalizationTestScreen(
//        onLocaleChange = { newLocale ->
//            localeProvider.updateLocale(newLocale)
//        }
//    )
//}

/**
 * Test with REAL LocalizationManager.
 * Uses actual API client (requires backend server running).
 *
 * Setup in MainActivity.onCreate():
 * ```
 * NanitLocalizationAndroid.initialize(applicationContext)
 * ```
 *
 * Then use in App.kt:
 * ```
 * LocalizationTestWithRealData()
 * ```
 *
 * Note: Locale changes will follow system locale automatically.
 */
@Composable
fun LocalizationTestWithRealData() {
    // Display test screen without manual locale selector
    // Real locale provider (AndroidLocaleProvider) handles system locale changes
    LocalizationTestScreen(
        onLocaleChange = null  // null = hide manual locale selector
    )
}