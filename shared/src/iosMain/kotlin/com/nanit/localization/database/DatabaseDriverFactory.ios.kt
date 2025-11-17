package com.nanit.localization.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.inMemoryDriver

actual open class DatabaseDriverFactory(
    private val useInMemory: Boolean = false // true = in-memory for testing
) {
    actual open fun createDriver(): SqlDriver {
        return if (useInMemory) {
            inMemoryDriver(LocalizationDatabase.Schema)
        } else {
            NativeSqliteDriver(
                schema = LocalizationDatabase.Schema,
                name = "localization.db"
            )
        }
    }
}
