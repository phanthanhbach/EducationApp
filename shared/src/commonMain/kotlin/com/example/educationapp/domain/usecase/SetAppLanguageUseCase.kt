package com.example.educationapp.domain.usecase

import com.example.educationapp.domain.enums.AppLanguage
import com.example.educationapp.domain.repository.AppPreferencesRepository

class SetAppLanguageUseCase(
    private val repository: AppPreferencesRepository
) {
    suspend operator fun invoke(language: AppLanguage) {
        repository.setLanguage(language)
    }
}
