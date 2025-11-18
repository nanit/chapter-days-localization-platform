package com.nanit.localization

import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import com.nanit.localization.server.di.ServerDOModule
import com.nanit.localization.server.domain.model.IncomingTranslation
import com.nanit.localization.server.domain.model.LMResponse
import com.nanit.localization.server.domain.model.LMResponseList
import com.nanit.localization.server.domain.model.UpdateTranslationModel
import com.nanit.localization.server.domain.repository.MockDataRepository
import com.nanit.localization.server.domain.repository.TranslationsRepository
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.contentType
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(Netty, port = 8081, host = "0.0.0.0") {
        install(Koin) {
            modules(ServerDOModule)
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }

        module()
    }.start(wait = true)
}

private fun Application.module() {
    val translationsRepository: TranslationsRepository by inject()
    val mockDataRepository: MockDataRepository by inject()

    launch { mockDataRepository.fillWithMock() }

    routing {
        get("/") { call.respondText("Nothing to show") }

        contentType(ContentType.Application.Json) {
            get("/translations") {
                either {
                    val locale = ensureNotNull(call.queryParameters["locale"]) {
                        raise(Throwable("No query for locale provided"))
                    }
                    val values = translationsRepository.getAllValuesBy(locale).bind()

                    LMResponse.from(locale, values)
                }
                    .onLeft { thr ->
                        call.respondText(
                            contentType = ContentType.Application.Json,
                            status = HttpStatusCode.NotFound,
                        ) { thr.message ?: "Unprocessable" }
                    }
                    .map(Json::encodeToString)
                    .onRight { responseStr ->

                        call.respondText(
                            text = responseStr,
                            contentType = ContentType.Application.Json,
                            status = HttpStatusCode.OK
                        )
                    }
            }

            put("/translation") {
                val model = call.receive<UpdateTranslationModel>()
                translationsRepository
                    .updateStringValue(model)
                    .onLeft { thr ->
                        call.respondText(
                            contentType = ContentType.Application.Json,
                            status = HttpStatusCode.NotFound,
                        ) { thr.message ?: "NotFound" }
                    }
                    .onRight {
                        call.respondText(
                            "OK",
                            status = HttpStatusCode.NoContent
                        )
                    }
            }

            post("/translation") {
                val incoming = call.receive<IncomingTranslation>()
                translationsRepository
                    .insert(incoming)
                    .onLeft { thr ->
                        call.respondText(
                            contentType = ContentType.Application.Json,
                            status = HttpStatusCode.UnprocessableEntity,
                        ) { thr.message ?: "Unprocessable" }
                    }
                    .map(LMResponseList::from)
                    .map(Json::encodeToString)
                    .onRight { responseStr ->
                        call.respondText(
                            text = responseStr,
                            contentType = ContentType.Application.Json,
                            status = HttpStatusCode.OK
                        )
                    }
            }
        }
    }
}
