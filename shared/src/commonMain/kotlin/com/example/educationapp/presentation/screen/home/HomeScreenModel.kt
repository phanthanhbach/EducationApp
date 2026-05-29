package com.example.educationapp.presentation.screen.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeState {
    object Idle : HomeState
    object Loading : HomeState
    object LoggedOut : HomeState
    data class Error(val message: String) : HomeState
}

class HomeScreenModel(
    private val logoutUseCase: LogoutUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<HomeState>(HomeState.Idle)
    val state: StateFlow<HomeState> = _state.asStateFlow()

    fun logout() {
        screenModelScope.launch {
            _state.value = HomeState.Loading
            when (val result = logoutUseCase()) {
                is ApiResult.Success -> {
                    _state.value = HomeState.LoggedOut
                }
                is ApiResult.Error -> {
                    // Dù lỗi server hay không, ta vẫn logout thành công vì token cục bộ đã xóa
                    _state.value = HomeState.LoggedOut
                }
            }
        }
    }
}
