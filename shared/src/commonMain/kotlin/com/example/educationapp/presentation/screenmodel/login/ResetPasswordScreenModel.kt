package com.example.educationapp.presentation.screenmodel.login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.UiText
import com.example.educationapp.core.util.asUiText
import com.example.educationapp.domain.usecase.ResetPasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ResetPasswordState {
    object Idle : ResetPasswordState
    object Loading : ResetPasswordState
    object Success : ResetPasswordState
    data class Error(val error: UiText) : ResetPasswordState
}

class ResetPasswordScreenModel(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<ResetPasswordState>(ResetPasswordState.Idle)
    val state: StateFlow<ResetPasswordState> = _state.asStateFlow()

    fun resetPassword(token: String, new: String, confirm: String) {
        if (token.isBlank() || new.isBlank() || confirm.isBlank()) {
            _state.value = ResetPasswordState.Error(UiText.DynamicString("Vui lòng điền đầy đủ các thông tin."))
            return
        }
        if (new != confirm) {
            _state.value = ResetPasswordState.Error(UiText.DynamicString("Mật khẩu xác nhận không khớp."))
            return
        }
        val hasUpperCase = new.any { it.isUpperCase() }
        val hasLowerCase = new.any { it.isLowerCase() }
        val hasDigit = new.any { it.isDigit() }
        val hasSpecialChar = new.any { !it.isLetterOrDigit() }

        if (new.length < 8 || !hasUpperCase || !hasLowerCase || !hasDigit || !hasSpecialChar) {
            _state.value = ResetPasswordState.Error(
                UiText.DynamicString("Mật khẩu mới phải từ 8 ký tự trở lên, bao gồm chữ hoa, chữ thường, chữ số và ký tự đặc biệt.")
            )
            return
        }

        screenModelScope.launch {
            _state.value = ResetPasswordState.Loading
            when (val result = resetPasswordUseCase(token = token, newPassword = new, confirmPassword = confirm)) {
                is ApiResult.Success -> {
                    _state.value = ResetPasswordState.Success
                }
                is ApiResult.Error -> {
                    _state.value = ResetPasswordState.Error(result.asUiText())
                }
            }
        }
    }

    fun resetState() {
        _state.value = ResetPasswordState.Idle
    }
}
