package com.example.localizationManager

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.localizationManager.database.database.SqlDriverProvider
import com.nanit.localization.database.LocalizationDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.Foundation.NSCurrentLocaleDidChangeNotification
import platform.Foundation.NSLocale
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

class IOSLocaleProvider: LocaleProvider {
    override fun getCurrentLocale(): LocaleInfo =
        LocaleInfo(
            NSLocale.currentLocale.languageCode
        )

    override fun observeLocalChanges(): Flow<LocaleInfo> {
        return callbackFlow {
            val observer = NSNotificationCenter.defaultCenter.addObserverForName(
                NSCurrentLocaleDidChangeNotification,
                null,
                NSOperationQueue.mainQueue
            ) { _ -> trySend(getCurrentLocale()) }
            send(getCurrentLocale())
            awaitClose {
                NSNotificationCenter.defaultCenter.removeObserver(observer)
            }
        }
    }
}

class IosSqlDriverProvider : SqlDriverProvider {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = LocalizationDatabase.Schema,
            name = "localization.db"
        )
    }
}

public object NanitLocalizationOS {
    fun initialize() {
        val localeProvider = IOSLocaleProvider()
        NanitLocalization.initialize(
            LocalizationManagerConfig(
                localeProvider = localeProvider,
                sqlDriverProvider = IosSqlDriverProvider()
            )
        )
    }
}