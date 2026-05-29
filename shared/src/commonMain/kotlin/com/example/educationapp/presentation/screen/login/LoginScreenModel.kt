package com.example.educationapp.presentation.screen.login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginScreenModel(
    private val loginUseCase: LoginUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _state.value = LoginState.Error("Username và Password không được để trống")
            return
        }

        screenModelScope.launch {
            _state.value = LoginState.Loading
            when (val result = loginUseCase(username, password)) {
                is ApiResult.Success -> {
                    _state.value = LoginState.Success(result.data)
                }
                is ApiResult.Error -> {
                    _state.value = LoginState.Error(result.message)
                }
            }
        }
    }

    fun resetState() {
        _state.value = LoginState.Idle
    }
}
