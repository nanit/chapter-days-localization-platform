package com.nanit.localization.server.data

import com.nanit.localization.server.data.database.DatabaseDriverFactory

actual fun createTestDatabaseDriverFactory(): DatabaseDriverFactory {
    return DatabaseDriverFactory()
}
