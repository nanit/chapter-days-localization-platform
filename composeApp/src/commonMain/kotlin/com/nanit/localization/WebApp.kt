package com.nanit.localization

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun WebAppPreview() {
    MaterialTheme {
        WebApp()
    }
}


@Composable
private fun nvm(navController: NavController): NavDestination? {
    val previousDestination = mutableStateOf<NavDestination?>(null)

    // Collect the currentBackStackEntryFlow as a state
    val currentEntry = navController.currentBackStackEntryFlow
        .collectAsState(initial = null)

    // Fallback to previousDestination if currentEntry is null
    return currentEntry.value?.destination.also { destination ->
        if (destination != null) {
            previousDestination.value = destination
        }
    } ?: previousDestination.value
}

@Composable
fun WebApp() {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { paddingValues ->
        val navController = rememberNavController()
        var cur: NavDestination? = nvm(navController)

        Row(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            PermanentDrawerSheet {
                Text(
                    text = cur?.toString() ?: "No Destination"
                )
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = "123"
                        )
                    },
                    selected = true,
                    onClick = {

                    },

                    )
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = "123"
                        )
                    },
                    selected = false,
                    onClick = {
                        navController.navigate(PluralsNavigationPage)
                    }
                )
            }
            NavHost(
                navController = navController,
                startDestination = ValuesBaseRoute,
                modifier = Modifier
            ) {
                valuesSection {

                }
            }
        }
    }
}

interface NavStuff

data object PluralsNavigationPage : NavStuff


//private object TestCache {
//
//    private val callback: MutableMap<String, () -> Int> = mutableMapOf()
//
//    private val coroutineScope = CoroutineScope()
//
//    private val _transfer: Channel<Flow<Locale>> = Channel(Channel.CONFLATED)
//    private val _transfer2: Channel<Locale> = Channel(Channel.CONFLATED)
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    private val clf: StateFlow<Locale> = _transfer
//        .consumeAsFlow()
//        .transformLatest { incoming ->
//            combine(
//            _transfer2,
//                incoming
//            ).collectLatest(::emit)
//        }
//        .stateIn(
//            scope = coroutineScope,
//            started = SharingStarted.Eagerly,
//            initialValue = Locale.current
//        )
//
//    fun callFromPlatform(flow: Flow<Locale>) {
//        _transfer.trySend(flow)
//    }
//
//    fun callFromPlatform(flow: Locale) {
//        _transfer2.trySend(flow)
//    }
//
//    val cache = Cache
//        .Builder<String, String>()
//        .maximumCacheSize(1_000)
//        .eventListener { stuff ->
//            when (stuff) {
//                is CacheEvent.Created<*, *> -> {
//                    println("Log")
//                }
//
//                is CacheEvent.Evicted<*, *> -> println("Log")
//                is CacheEvent.Expired<*, *> -> println("Log")
//                is CacheEvent.Removed<*, *> -> println("Log")
//                is CacheEvent.Updated<*, *> -> {
//                    callback[stuff.key]?.invoke()
//                }
//            }
//        }
//        .build()
//
//    private var currentLocal = Delegates.observable(Locale.current) { a,
//                                                                      old, new ->
//        if (old != new) {
//            cache.invalidateAll()
//            // Fill the cache
//        }
//    }
//
//    init {
//        currentLocal = Locale.current
//        cache.invalidate("123")
//    }
//
//    private fun getFromApi() {
//        val listOf = listOf<String>()
//        // Put listOf in db
//        listOf.forEach {
//            cache.put(it, it)
//        }
//
//    }
//
//    private suspend fun loadFromDb(key: String): String {
//        delay(100.milliseconds)
//        return key
//    }
//
//    suspend fun getString(key: String): String {
//        return cache.get(key) { loadFromDb(key) }
//    }
//
//    @Composable
//    fun stringResource(resourceKey: String): String {
//        val resourceReader = LocalResourceReader.currentOrPreview
//        val str by rememberResourceState(resourceKey, { "" }) { env ->
//            getString(resourceKey)
//        }
//        return str
//    }
//
//    fun getString(key: String) {
//
//    }
//
//    @Composable
//    internal actual fun <T> rememberResourceState(
//        key1: Any,
//        getDefault: () -> T,
//        asdfdsa: () -> Int,
//        block: suspend (ResourceEnvironment) -> T
//    ): State<T> {
//        Locale.current
//        val environment = LocalComposeEnvironment.current.rememberEnvironment()
//        var stuff: Int by remember { mutableIntStateOf(0) }
//        DisposableEffect(key1) {
//            callback.register { v ->
//                stuff = v
//            }
//            onDispose {
//                callback.remove(key1)
//            }
//        }
//        return remember(key1, environment, stuff) {
//            mutableStateOf(
//                runBlocking { block(environment) }
//            )
//        }
//    }
//}
