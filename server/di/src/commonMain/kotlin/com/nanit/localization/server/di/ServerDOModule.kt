package com.nanit.localization.server.di

import com.nanit.localization.server.data.database.DatabaseDriverFactory
import com.nanit.localization.server.data.database.LocalizationDatabase
import com.nanit.localization.server.data.database.LocalizationDatabaseQueries
import com.nanit.localization.server.data.repository.TranslationsRepositoryImpl
import com.nanit.localization.server.data.repository.MockDataRepositoryImpl
import com.nanit.localization.server.domain.repository.MockDataRepository
import com.nanit.localization.server.domain.repository.TranslationsRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val ServerDOModule: Module = module {
    factoryOf(::DatabaseDriverFactory)

    single {
        val factory: DatabaseDriverFactory = get<DatabaseDriverFactory>()
        LocalizationDatabase(factory.createDriver())
    }

    factoryOf(LocalizationDatabase::localizationDatabaseQueries) bind LocalizationDatabaseQueries::class

    singleOf(::TranslationsRepositoryImpl) bind TranslationsRepository::class
    singleOf(::MockDataRepositoryImpl) bind MockDataRepository::class
}
