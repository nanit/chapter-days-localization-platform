package com.example.localizationManager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.localizationManager.api.FakeLocalizationApiClient
import com.example.localizationManager.api.LocalizationApiClient
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

// TODO TASKS:
// - Add a worker job to fetch the string every day.
// - Somehow to populate the initial value on the database on build

class LocalizationManager(
    val config: LocalizationManagerConfig,
    private val apiClient: LocalizationApiClient? = FakeLocalizationApiClient() // TODO: REMOVE
) {

    private val coroutineScope by lazy { CoroutineScope(Dispatchers.Default + SupervisorJob()) }

    // Create API client internally - hidden from platform code
    // Use provided apiClient if available, otherwise create default Ktor client
    private val localizationApiClient: LocalizationApiClient by lazy {
//        apiClient ?: run {
//            val apiConfig = ApiConfig(
//                baseUrl = "base url",
//            )
//            KtorLocalizationApiClient(apiConfig)
//        }
        FakeLocalizationApiClient()
    }

    private val cache = Cache.Builder<String, String>()
        .maximumCacheSize(config.cacheSize)
        .eventListener { event ->
            refreshCallbacks[event.key]?.invoke()
        }
        .build()

    private val _currentLocale = MutableStateFlow(
        config.localeProvider.getCurrentLocale()
    )
    val currentLocale: StateFlow<LocaleInfo> = _currentLocale.asStateFlow()

    private val refreshCallbacks = mutableMapOf<String, () -> Unit>()

    fun initialize() {
        // Start observing locale changes
        coroutineScope.launch {
            config.localeProvider.observeLocalChanges()
                .distinctUntilChanged()
                .collect { newLocale ->
                    println("New Locale received: $newLocale")
                    handleLocaleChange(newLocale)
                }
        }

        // Load initial strings
        coroutineScope.launch {
            loadStringsForCurrentLocale()
        }
    }

    private suspend fun handleLocaleChange(newLocale: LocaleInfo) {
        if (_currentLocale.value != newLocale) {
            _currentLocale.value = newLocale
            cache.invalidateAll()
            loadStringsForCurrentLocale()

            // Trigger all registered callbacks
            refreshCallbacks.values.forEach { it.invoke() }
        }
    }

    private suspend fun loadStringsForCurrentLocale() {
        try {
            // Try to load from storage first
//            val cachedStrings = config.storage.getStrings(currentLocale.value.languageCode)

//            if (cachedStrings.isNotEmpty()) {
//                cachedStrings.forEach { (key, value) ->
//                    cache.put(key, value)
//                }
//            }

            // Fetch from API in background
            fetchAndCacheStrings()
        } catch (e: Exception) {
            // Handle error
            println("Error loading strings: ${e.message}")
        }
    }

    private suspend fun fetchAndCacheStrings() {
        try {
            val strings = localizationApiClient.fetchStrings(currentLocale.value.languageCode)

            // Save to storage
//            config.storage.saveStrings(currentLocale.value.languageCode, strings)

            // Update cache
            strings.forEach { (key, value) ->
                cache.put(key, value)
            }
        } catch (e: Exception) {
            println("Error fetching strings from API: ${e.message}")
        }
    }

    suspend fun getString(key: String): String {
        return cache.get(key) {
            // Fallback: try to load from storage
//            config.storage.getString(currentLocale.value.languageCode, key)
//                ?: key // Return key as fallback
            key
        }
    }

    fun observeString(key: String): Flow<String> = flow {
        // Emit initial value
        emit(cache.get(key) ?: key)

        // Observe locale changes
        currentLocale.collect { locale ->
            // Re-fetch from cache when locale changes
            val value = getString(key)
            emit(value)
        }
    }

    @Composable
    fun stringResource(key: String): String {
        val locale by currentLocale.collectAsState()
        var refreshTrigger by remember { mutableIntStateOf(0) }

        DisposableEffect(key) {
            val callback: () -> Unit = { refreshTrigger++ }
            refreshCallbacks[key] = callback

            onDispose {
                refreshCallbacks.remove(key)
            }
        }

        val value by produceState(
            initialValue = key,
            key1 = key,
            key2 = locale,
            key3 = refreshTrigger
        ) {
            value = getString(key)
        }

        return value
    }

}
