package com.nanit.localization

import com.nanit.localization.database.DatabaseDriverFactory

actual fun createTestDatabaseDriverFactory(): DatabaseDriverFactory {
    return DatabaseDriverFactory()
}
