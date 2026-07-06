package com.example.educationapp.presentation.model

import com.example.educationapp.domain.enums.AppLanguage
import com.example.educationapp.domain.enums.AppThemeMode
import com.example.educationapp.presentation.screenmodel.setting.SettingState

data class SettingUiModel(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val logoutState: SettingState = SettingState.Idle,
)
