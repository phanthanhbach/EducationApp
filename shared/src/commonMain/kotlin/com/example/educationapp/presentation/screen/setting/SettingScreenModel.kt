package com.example.educationapp.presentation.screen.setting

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.enums.AppLanguage
import com.example.educationapp.domain.enums.AppThemeMode
import com.example.educationapp.domain.usecase.LogoutUseCase
import com.example.educationapp.domain.usecase.ObserveAppPreferencesUseCase
import com.example.educationapp.domain.usecase.SetAppLanguageUseCase
import com.example.educationapp.domain.usecase.SetAppThemeModeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface SettingState {
    object Idle : SettingState
    object Loading : SettingState
    object LoggedOut : SettingState
    data class Error(val message: String) : SettingState
}

data class SettingUiState(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val logoutState: SettingState = SettingState.Idle,
)

class SettingScreenModel(
    private val logoutUseCase: LogoutUseCase,
    private val observeAppPreferencesUseCase: ObserveAppPreferencesUseCase,
    private val setAppThemeModeUseCase: SetAppThemeModeUseCase,
    private val setAppLanguageUseCase: SetAppLanguageUseCase,
) : ScreenModel {

    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            observeAppPreferencesUseCase().collect { preferences ->
                _uiState.update {
                    it.copy(
                        themeMode = preferences.themeMode,
                        language = preferences.language,
                    )
                }
            }
        }
    }

    fun onThemeModeSelected(mode: AppThemeMode) {
        if (_uiState.value.themeMode == mode) return
        screenModelScope.launch {
            setAppThemeModeUseCase(mode)
        }
    }

    fun onLanguageSelected(language: AppLanguage) {
        if (_uiState.value.language == language) return
        screenModelScope.launch {
            setAppLanguageUseCase(language)
        }
    }

    fun logout() {
        screenModelScope.launch {
            _uiState.update { it.copy(logoutState = SettingState.Loading) }
            when (logoutUseCase()) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(logoutState = SettingState.LoggedOut) }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(logoutState = SettingState.LoggedOut) }
                }
            }
        }
    }
}
