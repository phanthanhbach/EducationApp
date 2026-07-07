package com.example.educationapp.presentation.screenmodel

import com.example.educationapp.core.util.UiText

sealed interface ChangePasswordState {
    object Idle : ChangePasswordState
    object Loading : ChangePasswordState
    object Success : ChangePasswordState
    data class Error(val message: UiText) : ChangePasswordState
}

