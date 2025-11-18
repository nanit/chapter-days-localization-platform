package com.nanit.localization

//import io.ktor.client.call.body
//import io.ktor.client.plugins.logging.Logging
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

object MockData {
    private val coroutineScope = CoroutineScope(
        Dispatchers.Default + SupervisorJob()
    )

    private val _trtr: Channel<String> = Channel(Channel.BUFFERED)

    val _logs = _trtr
        .consumeAsFlow()
        .scan(mutableListOf<String>()) {list, str ->
            list.apply { add(str) }
        }
        .map { list -> list.toList() }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = listOf("SOMETHING HERE")
        )
    @Serializable
    data class TransInput(
        @SerialName("key")
        val key: String,
        @SerialName("description")
        val description: String?,
        @SerialName("value")
        val value: String,
    )

    init {
//        coroutineScope.launch {
//            while (true){
//                delay(1.seconds)
//                LoggerStringsProvider.update("Calling ktor ${Random.nextInt(0, 100)}")
//            }
//        }
//        coroutineScope.launch {
//            ApiClient.client.post("/translation") {
//                val input = TransInput(
//                    key = "some_test_stuff",
//                    description = "Another description",
//                    value = "Test Stuff For App"
//                )
////                contentType(ContentType.parse("application/json;charset=UTF-8"))
//                setBody(input)
//            }
//        }
        coroutineScope.launch(Dispatchers.Default) {
            LoggerStringsProvider.update("Calling ktor")

            val response = runCatching {
                ApiClient.client.get("/translations"){
                    url {
                        parameter("locale", "en")
                    }
                }.also {
                    _trtr.send("Res: ST: ${it.status} | ${it.bodyAsText()}")
                }
            }.onFailure {
                LoggerStringsProvider.updateSus("ERROR: ${it.message ?: it.toString()}")
                LoggerStringsProvider.update("ERROR: ${it.message ?: it.toString()}")
            }.onSuccess { response ->
                LoggerStringsProvider.update("SUCCESS: ${response.bodyAsText()}")
            }
        }
    }


    private val lock: ReentrantLock = ReentrantLock()
    private val database: MutableMap<String, LocalHolder> = buildMap {
        this += "login_btn_sign_in" to LocalHolder(
            desc = "Button used in Login Screen for Log In purpose",
            values = mutableMapOf(
                CombinedLocale("en", "us") to "Log In",
                CombinedLocale("ua", "ua") to "Логін",
            )
        )
    }.toMutableMap()

    data class LocalHolder(
        val desc: String?,
        val values: MutableMap<CombinedLocale, String>,
    )

    private val callbacks: MutableMap<String, () -> Unit> = mutableMapOf()

    fun registerObse(key: String, obs: () -> Unit) {
        callbacks[key] = obs
    }

    fun put(key: String, loc: CombinedLocale, value: String) = lock.withLock {
        database.compute(key){holder ->
            requireNotNull(holder)
            holder.let { v -> v.apply { values[loc] = value } }
        }
        callbacks.forEach { (_,v) -> v.invoke() }
    }

    fun getAll(): List<TranslationDomain> = lock.withLock {
        return database.map { (key, holder) ->
            TranslationDomain(
                key = key,
                desc = holder.desc,
                values = holder.values.asSequence().map { (locale, trs) ->
                    Stuff(
                        parentKey = key,
                        locale = locale,
                        value = trs
                    )
                }.toSet()
            )
        }
    }

    private fun <K, V> MutableMap<K, V>.compute(key: K, block: (V?) -> V?): V? {
        val oldValue = get(key)
        val newValue = block(oldValue)
        if (newValue == null) {
            if (oldValue != null || containsKey(key)) {
                remove(key)
                return null
            } else {
                return null
            }
        } else {
            put(key, newValue)
            return newValue
        }
    }
}
