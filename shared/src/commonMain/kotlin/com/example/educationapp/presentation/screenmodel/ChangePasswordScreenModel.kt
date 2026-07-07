package com.example.educationapp.presentation.screenmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.UiText
import com.example.educationapp.core.util.asUiText
import com.example.educationapp.domain.usecase.ChangePasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ChangePasswordScreenModel(
    private val changePasswordUseCase: ChangePasswordUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val state: StateFlow<ChangePasswordState> = _state.asStateFlow()

    fun changePassword(current: String, new: String, confirm: String) {
        if (current.isBlank() || new.isBlank() || confirm.isBlank()) {
            _state.value = ChangePasswordState.Error(UiText.DynamicString("Vui lòng điền đầy đủ các thông tin."))
            return
        }
        if (new != confirm) {
            _state.value = ChangePasswordState.Error(UiText.DynamicString("Mật khẩu xác nhận không khớp."))
            return
        }
        val hasUpperCase = new.any { it.isUpperCase() }
        val hasLowerCase = new.any { it.isLowerCase() }
        val hasDigit = new.any { it.isDigit() }
        val hasSpecialChar = new.any { !it.isLetterOrDigit() }

        if (new.length < 8 || !hasUpperCase || !hasLowerCase || !hasDigit || !hasSpecialChar) {
            _state.value = ChangePasswordState.Error(
                UiText.DynamicString("Mật khẩu mới phải từ 8 ký tự trở lên, bao gồm chữ hoa, chữ thường, chữ số và ký tự đặc biệt.")
            )
            return
        }

        screenModelScope.launch {
            _state.value = ChangePasswordState.Loading
            val result = changePasswordUseCase(
                currentPassword = current,
                newPassword = new,
                confirmPassword = confirm
            )
            when (result) {
                is ApiResult.Success -> {
                    _state.value = ChangePasswordState.Success
                }
                is ApiResult.Error -> {
                    _state.value = ChangePasswordState.Error(result.asUiText())
                }
            }
        }
    }

    fun resetState() {
        _state.value = ChangePasswordState.Idle
    }
}
