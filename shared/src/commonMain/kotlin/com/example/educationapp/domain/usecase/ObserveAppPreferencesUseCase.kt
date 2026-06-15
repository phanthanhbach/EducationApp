package com.example.educationapp.domain.usecase

import com.example.educationapp.domain.entity.AppPreferences
import com.example.educationapp.domain.repository.AppPreferencesRepository
import kotlinx.coroutines.flow.Flow

class ObserveAppPreferencesUseCase(
    private val repository: AppPreferencesRepository
) {
    operator fun invoke(): Flow<AppPreferences> = repository.preferencesFlow
}
