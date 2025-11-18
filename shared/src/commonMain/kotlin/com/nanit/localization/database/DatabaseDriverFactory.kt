package com.nanit.localization.database

import app.cash.sqldelight.db.SqlDriver

//expect class DatabaseDriverFactory {
//}

interface SqlDriverProvider {
    fun createDriver(): SqlDriver
}
