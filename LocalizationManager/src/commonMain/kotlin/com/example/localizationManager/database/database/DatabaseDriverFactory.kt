package com.example.localizationManager.database.database

import app.cash.sqldelight.db.SqlDriver

//expect class DatabaseDriverFactory {
//}

interface SqlDriverProvider {
    fun createDriver(): SqlDriver
}
