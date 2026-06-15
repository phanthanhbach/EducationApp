package com.example.educationapp.domain.entity

import com.example.educationapp.domain.enums.AppLanguage
import com.example.educationapp.domain.enums.AppThemeMode

data class AppPreferences(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.ENGLISH
)
