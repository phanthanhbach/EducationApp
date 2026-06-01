package com.example.educationapp.presentation.screenmodel.login

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.UserInfo

sealed interface LoginState {
    object Idle : LoginState
    object Loading : LoginState
    data class Success(val userInfo: UserInfo) : LoginState
    data class Error(val error: UiText) : LoginState
}