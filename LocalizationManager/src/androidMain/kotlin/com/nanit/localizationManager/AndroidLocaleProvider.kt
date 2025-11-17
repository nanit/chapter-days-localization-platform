package com.nanit.localizationManager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.example.localizationManager.LocaleInfo
import com.example.localizationManager.LocaleProvider
import com.example.localizationManager.LocalizationManagerConfig
import com.example.localizationManager.NanitLocalization
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class AndroidLocaleProvider(
    private val context: Context,
) : LocaleProvider {

    private val _localeFlow = MutableStateFlow(getCurrentLocale())
    private val localeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_LOCALE_CHANGED) {
                _localeFlow.value = getCurrentLocale()
            }
        }
    }

    init {
        val filter = IntentFilter(Intent.ACTION_LOCALE_CHANGED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(localeReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(localeReceiver, filter)
        }
    }

    override fun getCurrentLocale(): LocaleInfo {
        val locale = context.resources.configuration.locales[0]

        return LocaleInfo(
            languageCode = locale.language,
        )
    }

    override fun observeLocalChanges(): Flow<LocaleInfo> = _localeFlow

    fun cleanup() {
        context.unregisterReceiver(localeReceiver)
    }
}

// Android initialization helper
object NanitLocalizationAndroid {
    fun initialize(
        context: Context,
    ) {
        val localeProvider = AndroidLocaleProvider(context.applicationContext)

        NanitLocalization.initialize(
            LocalizationManagerConfig(
                localeProvider = localeProvider,
            )
        )
    }
}
