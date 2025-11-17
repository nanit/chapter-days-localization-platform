package com.example.localizationManager
import kotlinx.coroutines.flow.Flow

interface LocaleProvider {
    fun getCurrentLocale(): LocaleInfo
    fun observeLocalChanges(): Flow<LocaleInfo>
}