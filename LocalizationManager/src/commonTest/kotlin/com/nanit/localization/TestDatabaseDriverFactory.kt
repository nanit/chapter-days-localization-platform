package com.nanit.localization

import com.nanit.localization.database.DatabaseDriverFactory

/**
 * Provides a DatabaseDriverFactory for testing purposes
 * Uses expect/actual pattern to provide platform-specific test factories
 */
expect fun createTestDatabaseDriverFactory(): DatabaseDriverFactory
