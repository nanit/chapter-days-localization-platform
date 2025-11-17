package com.nanit.localization

import com.nanit.localization.database.DatabaseDriverFactory
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.nanit.localization.database.LocalizationDatabase
import android.content.Context

// For Android unit tests, we create a wrapper that uses JVM driver since unit tests run on JVM
private class AndroidUnitTestDatabaseDriverFactory : DatabaseDriverFactory(null as Context?) {
    private val jvmDriver: SqlDriver by lazy {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        LocalizationDatabase.Schema.create(driver)
        driver
    }

    override fun createDriver(): SqlDriver = jvmDriver
}

actual fun createTestDatabaseDriverFactory(): DatabaseDriverFactory {
    return AndroidUnitTestDatabaseDriverFactory()
}
