package com.nanit.localization

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform