package com.example.educationapp.presentation.screenmodel.login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.UiText
import com.example.educationapp.core.util.asUiText
import com.example.educationapp.domain.usecase.LoginUseCase
import com.example.educationapp.domain.usecase.LogoutUseCase
import com.example.educationapp.presentation.screenmodel.login.LoginState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.error_empty_credentials
import educationapp.shared.generated.resources.error_role_not_allowed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginScreenModel(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _state.value = LoginState.Error(UiText.ResourceString(Res.string.error_empty_credentials))
            return
        }

        screenModelScope.launch {
            _state.value = LoginState.Loading
            when (val result = loginUseCase(username, password)) {
                is ApiResult.Success -> {
                    val userInfo = result.data
                    if (userInfo.userRole.isMobileAccessAllowed) {
                        _state.value = LoginState.Success(userInfo)
                    } else {
                        // Cleanup tokens since they were saved, but role is unauthorized
                        logoutUseCase()
                        _state.value = LoginState.Error(
                            UiText.ResourceString(Res.string.error_role_not_allowed)
                        )
                    }
                }
                is ApiResult.Error -> {
                    _state.value = LoginState.Error(result.asUiText())
                }
            }
        }
    }

    fun resetState() {
        _state.value = LoginState.Idle
    }
}