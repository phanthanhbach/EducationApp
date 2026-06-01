package com.example.educationapp.presentation.screen.main

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MainState {
    object Idle : MainState
    object Loading : MainState
    object LoggedOut : MainState
    data class Error(val message: String) : MainState
}

class MainScreenModel(
    private val logoutUseCase: LogoutUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<MainState>(MainState.Idle)
    val state: StateFlow<MainState> = _state.asStateFlow()

    fun logout() {
        screenModelScope.launch {
            _state.value = MainState.Loading
            when (logoutUseCase()) {
                is ApiResult.Success -> {
                    _state.value = MainState.LoggedOut
                }
                is ApiResult.Error -> {
                    // Dù lỗi server hay không, ta vẫn logout thành công vì token cục bộ đã xóa
                    _state.value = MainState.LoggedOut
                }
            }
        }
    }
}
