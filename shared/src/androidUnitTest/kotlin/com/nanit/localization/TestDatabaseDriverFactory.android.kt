package com.nanit.localization

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.nanit.localization.database.DatabaseDriverFactory

actual fun createTestDatabaseDriverFactory(): DatabaseDriverFactory {
    val context = ApplicationProvider.getApplicationContext<Application>()
    return DatabaseDriverFactory(context)
}
