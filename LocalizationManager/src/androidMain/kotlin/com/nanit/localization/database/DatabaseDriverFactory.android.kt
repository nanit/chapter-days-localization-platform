package com.nanit.localization.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual open class DatabaseDriverFactory(
    private val context: Context?,
    private val databaseName: String? = "localization.db" // null = in-memory for testing
) {
    actual open fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = LocalizationDatabase.Schema,
            context = context!!,
            name = databaseName
        )
    }
}
