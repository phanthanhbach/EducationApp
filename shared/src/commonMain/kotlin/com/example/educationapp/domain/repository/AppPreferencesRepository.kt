package com.example.educationapp.domain.repository

import com.example.educationapp.domain.entity.AppPreferences
import com.example.educationapp.domain.enums.AppLanguage
import com.example.educationapp.domain.enums.AppThemeMode
import kotlinx.coroutines.flow.Flow

interface AppPreferencesRepository {
    val preferencesFlow: Flow<AppPreferences>

    suspend fun setThemeMode(mode: AppThemeMode)

    suspend fun setLanguage(language: AppLanguage)
}
