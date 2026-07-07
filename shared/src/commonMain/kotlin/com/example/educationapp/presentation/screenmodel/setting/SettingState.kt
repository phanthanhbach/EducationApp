package com.example.educationapp.presentation.screenmodel.setting

import com.example.educationapp.core.util.UiText

sealed interface SettingState {
    object Idle : SettingState
    object Loading : SettingState
    object LoggedOut : SettingState
    data class Error(val message: UiText) : SettingState
}