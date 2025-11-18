package com.nanit.localization

import com.nanit.localization.server.data.database.DatabaseDriverFactory
import com.nanit.localization.server.data.database.LocalizationDatabase
import com.nanit.localization.server.data.database.LocalizationRepositoryV2
import com.nanit.localization.server.data.repository.MockDataRepositoryImpl
import com.nanit.localization.server.domain.model.StringResource
import com.nanit.localization.server.domain.repository.MockDataRepository
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
import io.ktor.server.routing.routing
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module

private val databaseModule = module {
    singleOf(::DatabaseDriverFactory)

    single {
        val driverFactory = get<DatabaseDriverFactory>().createDriver()

        LocalizationDatabase(driverFactory)
    }

    single {
        LocalizationRepositoryV2(
            queries = get<LocalizationDatabase>().localizationDatabaseQueries
        )
    } bind LocalizationRepositoryV2::class

    single {
        MockDataRepositoryImpl(
            queries = get<LocalizationDatabase>().localizationDatabaseQueries
        )
    } bind MockDataRepository::class
}

@Suppress("RedundantRequireNotNullCall")
private data object ServerApp {
    val application: KoinApplication by lazy {
        koinApplication {
            modules(databaseModule)
        }
    }

    init {
        requireNotNull(application)
    }

    inline fun <reified T : Any> get(): T {
        return application.koin.get<T>()
    }
}

@Suppress("RedundantRequireNotNullCall")
fun main() {
    requireNotNull(ServerApp)
    embeddedServer(Netty, port = 8081, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }

        module()
    }
        .start(wait = true)
}

fun Application.module(
    repo: LocalizationRepositoryV2 = ServerApp.get(),
    mockRepo: MockDataRepository = ServerApp.get(),
) {
    launch {
        mockRepo
            .fillWithMock()
            .onLeft {
                val a = 1
            }.onRight {
                val b = 2
            }
    }

    routing {
        get("/") {
            val res= mockRepo.fillWithMock()
            val all = repo.getAll()
            res.onLeft {
                call.respondText("All failed: $it")
            }.onRight {
                call.respondText("All Success: $it")
            }
        }
        get("/translations") {
            val allTranslations = repo.getAll()
            call.respondText("Ktor 2: WTF")
        }
        contentType(ContentType.Application.Json) {
            post("/translation") {
                val incoming = call.receive<TransInput>()
                repo.insertStringValue(incoming.toStringValue())
                call.respondText(
                    "Customer stored correctly: $incoming",
                    status = HttpStatusCode.Created
                )
            }
        }
    }
}

@Serializable
data class TransInput(
    @SerialName("key")
    val key: String,
    @SerialName("description")
    val description: String?,
    @SerialName("value")
    val value: String,
)

private fun TransInput.toStringValue(): StringResource.Value =
    StringResource.Value(key, value, description = description)
