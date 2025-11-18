package com.nanit.localization.server.data.repository

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import com.nanit.localization.server.data.database.LocalizationDatabaseQueries
import com.nanit.localization.server.data.importer.LocalizationJson
import com.nanit.localization.server.domain.repository.MockDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import java.io.File

class MockDataRepositoryImpl(
    private val queries: LocalizationDatabaseQueries
) : MockDataRepository {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    override suspend fun fillWithMock(): Either<Throwable, Unit> = either {
        val process: (String) -> Either<Throwable, Unit> = { fileName ->
            val content: String = catch(
                block = {
                    val f = File("server/data/src/commonMain/resources/localizations/$fileName").readText()
                    f
                },
                catch = ::raise
            )
            val enLoc: LocalizationJson = catch(
                block = { json.decodeFromString<LocalizationJson>(content) },
                catch = ::raise
            )

            insertLoc(enLoc)
        }

        process("strings_en.json").bind()
        process("strings_es.json").bind()
        process("strings_fr.json").bind()
    }

    private fun insertLoc(
        locationJson: LocalizationJson
    ): Either<Throwable, Unit> = with(Dispatchers.Default) {
        either {
            catch(
                block = {
                    queries.transaction {
                        val now = Clock.System.now().toEpochMilliseconds()
                        locationJson.values.forEach { jsv ->
                            with(jsv) {
                                queries.insertOrReplaceStringValue(
                                    key = key,
                                    locale = locationJson.locale,
                                    value_ = value,
                                    description = description,
                                    created_at = now,
                                    updated_at = now
                                )
                            }
                        }
                    }
                },
                catch = ::raise
            )
        }
    }
}
