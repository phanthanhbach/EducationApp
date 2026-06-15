package com.example.educationapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.educationapp.domain.entity.AppPreferences
import com.example.educationapp.domain.enums.AppLanguage
import com.example.educationapp.domain.enums.AppThemeMode
import com.example.educationapp.domain.repository.AppPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : AppPreferencesRepository {

    override val preferencesFlow: Flow<AppPreferences> =
        dataStore.data.map { preferences ->
            AppPreferences(
                themeMode = AppThemeMode.fromString(preferences[THEME_MODE_KEY]),
                language = AppLanguage.fromLocaleTag(preferences[LANGUAGE_KEY])
            )
        }

    override suspend fun setThemeMode(mode: AppThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }

    override suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language.localeTag
        }
    }

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val LANGUAGE_KEY = stringPreferencesKey("app_language")
    }
}
