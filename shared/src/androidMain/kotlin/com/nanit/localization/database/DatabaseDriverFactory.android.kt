package com.nanit.localization.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

//actual class DatabaseDriverFactory(private val context: Context) {
//    actual fun createDriver(): SqlDriver {
//        return AndroidSqliteDriver(
//            schema = LocalizationDatabase.Schema,
//            context = context,
//            name = "localization.db"
//        )
//    }
//}

class AndroidSqlDriverProvider(
    val context: Context
): SqlDriverProvider {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = LocalizationDatabase.Schema,
            context = context,
            name = "localization.db"
        )
    }
}
