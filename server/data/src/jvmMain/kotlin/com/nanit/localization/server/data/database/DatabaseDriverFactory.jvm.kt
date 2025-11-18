package com.nanit.localization.server.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

//actual class DatabaseDriverFactory {
//    actual fun createDriver(): SqlDriver {
//        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
//        LocalizationDatabase.Schema.create(driver)
//        return driver
//    }
//}

class JvmSqlDriverProvider : SqlDriverProvider {
    override fun createDriver(): SqlDriver {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        LocalizationDatabase.Schema.create(driver)
        return driver
    }
}
