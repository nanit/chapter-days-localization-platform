package com.nanit.localization.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

//actual class DatabaseDriverFactory {
//
//}

class IosSqlDriverProvider : SqlDriverProvider {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = LocalizationDatabase.Schema,
            name = "localization.db"
        )
    }
}
