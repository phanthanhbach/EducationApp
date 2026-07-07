package com.example.educationapp.presentation.screenmodel.forgot_password

import com.example.educationapp.core.util.UiText

sealed interface ForgotPasswordState {
    object Idle : ForgotPasswordState
    object Loading : ForgotPasswordState
    data class Success(val expiresAt: String) : ForgotPasswordState
    data class Error(val error: UiText) : ForgotPasswordState
}