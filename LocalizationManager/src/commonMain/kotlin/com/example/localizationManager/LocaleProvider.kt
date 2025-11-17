package com.example.localizationManager
import kotlinx.coroutines.flow.Flow

expect fun createLocalProvider(): LocaleProvider
interface LocaleProvider {
    fun getCurrentLocale(): LocaleInfo
    fun observeLocalChanges(): Flow<LocaleInfo>
}