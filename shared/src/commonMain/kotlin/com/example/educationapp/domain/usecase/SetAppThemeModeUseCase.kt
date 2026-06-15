package com.example.educationapp.domain.usecase

import com.example.educationapp.domain.enums.AppThemeMode
import com.example.educationapp.domain.repository.AppPreferencesRepository

class SetAppThemeModeUseCase(
    private val repository: AppPreferencesRepository
) {
    suspend operator fun invoke(mode: AppThemeMode) {
        repository.setThemeMode(mode)
    }
}
