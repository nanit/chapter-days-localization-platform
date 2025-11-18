package com.example.localizationManager.database.utils

import kotlinx.datetime.Clock

/**
 * Get current time in milliseconds
 */
fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()
