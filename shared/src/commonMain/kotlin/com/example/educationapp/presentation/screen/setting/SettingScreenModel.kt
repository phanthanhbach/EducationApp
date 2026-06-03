package com.example.educationapp.presentation.screen.setting

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SettingState {
    object Idle : SettingState
    object Loading : SettingState
    object LoggedOut : SettingState
    data class Error(val message: String) : SettingState
}

class SettingScreenModel(
    private val logoutUseCase: LogoutUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<SettingState>(SettingState.Idle)
    val state: StateFlow<SettingState> = _state.asStateFlow()

    fun logout() {
        screenModelScope.launch {
            _state.value = SettingState.Loading
            when (logoutUseCase()) {
                is ApiResult.Success -> {
                    _state.value = SettingState.LoggedOut
                }
                is ApiResult.Error -> {
                    _state.value = SettingState.LoggedOut
                }
            }
        }
    }
}
