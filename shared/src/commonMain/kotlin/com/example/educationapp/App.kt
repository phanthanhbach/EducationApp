package com.example.educationapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import com.example.educationapp.core.data.TokenManager
import com.example.educationapp.core.locale.AppEnvironment
import com.example.educationapp.core.theme.AppTheme
import com.example.educationapp.domain.entity.AppPreferences
import com.example.educationapp.domain.enums.AppThemeMode
import com.example.educationapp.domain.usecase.ObserveAppPreferencesUseCase
import com.example.educationapp.presentation.screen.login.LoginScreen
import com.example.educationapp.presentation.screen.main.MainScreen
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val tokenManager = koinInject<TokenManager>()
    val observeAppPreferencesUseCase = koinInject<ObserveAppPreferencesUseCase>()
    val preferences by observeAppPreferencesUseCase().collectAsState(initial = AppPreferences())

    val initialScreen = remember(tokenManager) {
        val token = tokenManager.getAccessToken()
        val role = tokenManager.getUserRole()
        if (token != null && role.isMobileAccessAllowed) {
            MainScreen(role)
        } else {
            LoginScreen()
        }
    }

    val darkTheme = when (preferences.themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    AppEnvironment(localeTag = preferences.language.localeTag) {
        AppTheme(darkTheme = darkTheme) {
            Navigator(initialScreen)
        }
    }
}
