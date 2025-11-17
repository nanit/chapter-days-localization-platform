package com.nanit.localization.utils

import kotlin.js.Date

actual fun currentTimeMillis(): Long = Date.now().toLong()
