package com.example.educationapp.presentation.screenmodel.reset_password

import com.example.educationapp.core.util.UiText

sealed interface ResetPasswordState {
    object Idle : ResetPasswordState
    object Loading : ResetPasswordState
    object Success : ResetPasswordState
    data class Error(val error: UiText) : ResetPasswordState
}