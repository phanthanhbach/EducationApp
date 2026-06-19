package com.example.educationapp.presentation.screenmodel.login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.UiText
import com.example.educationapp.core.util.asUiText
import com.example.educationapp.domain.usecase.ForgotPasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ForgotPasswordState {
    object Idle : ForgotPasswordState
    object Loading : ForgotPasswordState
    data class Success(val expiresAt: String) : ForgotPasswordState
    data class Error(val error: UiText) : ForgotPasswordState
}

class ForgotPasswordScreenModel(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()

    fun submitEmail(email: String) {
        if (email.isBlank()) {
            _state.value = ForgotPasswordState.Error(UiText.DynamicString("Vui lòng nhập tên đăng nhập (email)."))
            return
        }

        screenModelScope.launch {
            _state.value = ForgotPasswordState.Loading
            when (val result = forgotPasswordUseCase(email)) {
                is ApiResult.Success -> {
                    _state.value = ForgotPasswordState.Success(result.data)
                }
                is ApiResult.Error -> {
                    _state.value = ForgotPasswordState.Error(result.asUiText())
                }
            }
        }
    }

    fun resetState() {
        _state.value = ForgotPasswordState.Idle
    }
}
