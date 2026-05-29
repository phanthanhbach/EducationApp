package com.example.educationapp.presentation.screen.login

import com.example.educationapp.domain.entity.UserToken

sealed interface LoginState {
    object Idle : LoginState
    object Loading : LoginState
    data class Success(val userToken: UserToken) : LoginState
    data class Error(val message: String) : LoginState
}
