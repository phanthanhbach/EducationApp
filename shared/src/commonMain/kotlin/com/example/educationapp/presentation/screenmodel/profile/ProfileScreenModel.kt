package com.example.educationapp.presentation.screenmodel.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.data.TokenManager
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.usecase.GetMyProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProfileState {
    object Idle : ProfileState
    object Loading : ProfileState
    data class Success(val profile: UserProfile) : ProfileState
    data class Error(val message: String) : ProfileState
}

class ProfileScreenModel(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val tokenManager: TokenManager
) : ScreenModel {

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        screenModelScope.launch {
            _state.value = ProfileState.Loading
            val role = tokenManager.getUserRole()
            when (val result = getMyProfileUseCase(role)) {
                is ApiResult.Success -> {
                    _state.value = ProfileState.Success(result.data)
                }
                is ApiResult.Error -> {
                    _state.value = ProfileState.Error(
                        result.message ?: "Failed to load profile"
                    )
                }
            }
        }
    }
}
