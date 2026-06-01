package com.example.educationapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.example.educationapp.core.theme.AppTheme
import cafe.adriel.voyager.navigator.Navigator
import com.example.educationapp.presentation.screen.login.LoginScreen
import com.example.educationapp.presentation.screen.main.MainScreen
import com.example.educationapp.core.data.TokenManager
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val tokenManager = koinInject<TokenManager>()
    
    val initialScreen = remember(tokenManager) {
        val token = tokenManager.getAccessToken()
        val role = tokenManager.getUserRole()
        if (token != null && role.isMobileAccessAllowed) {
            MainScreen(role)
        } else {
            LoginScreen()
        }
    }

    AppTheme {
        Navigator(initialScreen)
    }
}