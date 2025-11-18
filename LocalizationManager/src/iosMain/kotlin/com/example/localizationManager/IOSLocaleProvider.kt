package com.example.localizationManager

import com.nanit.localization.database.IosSqlDriverProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.Foundation.*

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