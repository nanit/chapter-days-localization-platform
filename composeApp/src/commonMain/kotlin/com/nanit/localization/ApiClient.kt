package com.nanit.localization

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json


object LoggerStringsProvider {
    val _state: MutableStateFlow<Pair<MutableList<String>, Long>> = MutableStateFlow(mutableListOf("1", "2") to 0)
    val messages: Flow<List<String>> = _state
        .map { (messages,_) -> messages.toList() }

    fun update(message: String) {
        _state.update { (v, l) ->
            v.apply { add(message) } to l + 1
        }
    }
    suspend fun updateSus(message: String) {
//        _state.update { v -> v.apply { add(message) } }
    }
}

class CustomKtorLogger : Logger {
    override fun log(message: String) {
        LoggerStringsProvider.update(message)
    }
}

object ApiClient {
    private const val BASE_URL = "http://192.168.2.250:8081"

    val client = HttpClient(CIO) {

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }

        install(Logging) {
            logger = CustomKtorLogger()
            level = LogLevel.ALL
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 30000
        }

        defaultRequest {
            url(BASE_URL)
            contentType(ContentType.Application.Json)
        }
    }

    suspend inline fun <reified T> get(path: String): T {
        return client.get(path).body()
    }

    suspend fun getBodyAsString(path: String): String {
        return client.get(path).bodyAsText()
    }


    suspend inline fun <reified T, reified R> post(path: String, body: T): R {
        return client.post(path) {
            setBody(body)
        }.body()
    }

    suspend inline fun <reified T, reified R> put(path: String, body: T): R {
        return client.put(path) {
            setBody(body)
        }.body()
    }

    suspend inline fun <reified T> delete(path: String): T {
        return client.delete(path).body()
    }

    fun close() {
        client.close()
    }
}
