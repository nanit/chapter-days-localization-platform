package com.nanit.localization.server.data.repository

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import com.nanit.localization.server.data.database.LocalizationDatabaseQueries
import com.nanit.localization.server.data.enJsonMock
import com.nanit.localization.server.data.esJsonMock
import com.nanit.localization.server.data.frJsonMock
import com.nanit.localization.server.data.model.LocalizationJson
import com.nanit.localization.server.domain.repository.MockDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

class MockDataRepositoryImpl(
    private val queries: LocalizationDatabaseQueries
) : MockDataRepository {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    override suspend fun fillWithMock(): Either<Throwable, Unit> = either {
        val process: (String) -> Either<Throwable, Unit> = { jsonStr ->
            val enLoc: LocalizationJson = catch(
                block = { json.decodeFromString<LocalizationJson>(jsonStr) },
                catch = ::raise
            )

            insertLoc(enLoc)
        }

        process(enJsonMock).bind()
        process(esJsonMock).bind()
        process(frJsonMock).bind()
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
