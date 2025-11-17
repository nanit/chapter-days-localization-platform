package com.nanit.localization

import com.nanit.localization.database.DatabaseDriverFactory

// Create in-memory database for iOS tests
actual fun createTestDatabaseDriverFactory(): DatabaseDriverFactory {
    return DatabaseDriverFactory(useInMemory = true)
}
