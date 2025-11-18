package com.nanit.localization.server.data

import com.nanit.localization.server.data.database.DatabaseDriverFactory

/**
 * Provides a DatabaseDriverFactory for testing purposes
 * Uses expect/actual pattern to provide platform-specific test factories
 */
expect fun createTestDatabaseDriverFactory(): DatabaseDriverFactory
