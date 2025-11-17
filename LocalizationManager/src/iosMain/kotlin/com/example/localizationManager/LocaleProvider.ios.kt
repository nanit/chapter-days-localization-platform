package com.example.localizationManager

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.Foundation.*

actual fun createLocalProvider(): LocaleProvider {
    return LocaleProviderOS()
}

class LocaleProviderOS: LocaleProvider {
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
            awaitClose {
                NSNotificationCenter.defaultCenter.removeObserver(observer)
            }
        }
    }
}