package com.nanit.localization

import android.app.Application
import com.example.localizationManager.NanitLocalizationAndroid

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize the sdk
        NanitLocalizationAndroid.initialize(applicationContext)
    }
}
