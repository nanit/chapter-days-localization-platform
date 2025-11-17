package com.example.localizationManager.api

import com.example.localizationManager.getPlatformName
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class KtorLocalizationApiClient( // Default simple implementation
    private val config: ApiConfig
) : LocalizationApiClient {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
                coerceInputValues = true
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = config.timeoutMillis
            connectTimeoutMillis = config.timeoutMillis
        }

        defaultRequest {
            url(config.baseUrl)

            // Add common headers
            config.headers.forEach { (key, value) ->
                header(key, value)
            }

//            // Add auth if provided
//            config.authToken?.let { token ->
//                header(HttpHeaders.Authorization, "Bearer $token")
//            }
        }
    }

    override suspend fun fetchStrings(locale: String): Map<String, String> {
        return try {
            val response: HttpResponse = client.get(config.stringsEndpoint) {
                parameter("locale", locale)
                parameter("app_version", config.appVersion)
                parameter("platform", getPlatformName())
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val localizationResponse: LocalizationResponse = response.body()
                    localizationResponse.strings
                }
                HttpStatusCode.NotFound -> {
                    // Locale not found, return empty map
                    emptyMap()
                }
                else -> {
                    val errorBody = try {
                        response.body<ErrorResponse>()
                    } catch (e: Exception) {
                        ErrorResponse("Unknown error", response.bodyAsText())
                    }
                    throw LocalizationApiException(
                        "API error: ${errorBody.message ?: errorBody.error}",
                        response.status.value
                    )
                }
            }
        } catch (e: LocalizationApiException) {
            throw e
        } catch (e: Exception) {
            throw LocalizationApiException(
                "Failed to fetch strings for locale $locale: ${e.message}",
                cause = e
            )
        }
    }

    override fun close() {
        client.close()
    }
}