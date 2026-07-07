package com.example.educationapp.presentation.screenmodel.setting

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.enums.AppLanguage
import com.example.educationapp.domain.enums.AppThemeMode
import com.example.educationapp.domain.usecase.LogoutUseCase
import com.example.educationapp.domain.usecase.ObserveAppPreferencesUseCase
import com.example.educationapp.domain.usecase.SetAppLanguageUseCase
import com.example.educationapp.domain.usecase.SetAppThemeModeUseCase
import com.example.educationapp.presentation.model.SettingUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingScreenModel(
    private val logoutUseCase: LogoutUseCase,
    private val observeAppPreferencesUseCase: ObserveAppPreferencesUseCase,
    private val setAppThemeModeUseCase: SetAppThemeModeUseCase,
    private val setAppLanguageUseCase: SetAppLanguageUseCase,
) : ScreenModel {

    private val _uiState = MutableStateFlow(SettingUiModel())
    val uiState: StateFlow<SettingUiModel> = _uiState.asStateFlow()

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
