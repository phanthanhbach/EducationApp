package com.example.educationapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import com.example.educationapp.core.data.TokenManager
import com.example.educationapp.core.locale.AppEnvironment
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.theme.AppTheme
import com.example.educationapp.core.ui.toast.AppToast
import com.example.educationapp.core.ui.toast.LocalToastController
import com.example.educationapp.core.ui.toast.ToastController
import com.example.educationapp.domain.entity.AppPreferences
import com.example.educationapp.domain.enums.AppThemeMode
import com.example.educationapp.domain.usecase.ObserveAppPreferencesUseCase
import com.example.educationapp.presentation.screen.login.LoginScreen
import com.example.educationapp.presentation.screen.main.LocalIsTablet
import com.example.educationapp.presentation.screen.main.MainScreen
import org.koin.compose.koinInject
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.educationapp.core.data.SessionManager
import com.example.educationapp.core.data.SessionEvent
import com.example.educationapp.core.ui.dialog.AppAlertDialog
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.dialog_session_expired_title
import educationapp.shared.generated.resources.dialog_session_expired_desc
import educationapp.shared.generated.resources.btn_ok
import org.jetbrains.compose.resources.stringResource
import cafe.adriel.voyager.navigator.CurrentScreen

@Composable
@Preview
fun App() {
    val tokenManager = koinInject<TokenManager>()
    val observeAppPreferencesUseCase = koinInject<ObserveAppPreferencesUseCase>()
    val preferences by observeAppPreferencesUseCase().collectAsState(initial = AppPreferences())
    val coroutineScope = rememberCoroutineScope()
    val toastController = remember(coroutineScope) { ToastController(coroutineScope) }
    val sessionManager = koinInject<SessionManager>()
    var showSessionExpiredDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        sessionManager.sessionEvent.collect { event ->
            when (event) {
                SessionEvent.Expired -> showSessionExpiredDialog = true
            }
        }
    }

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
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isTablet = maxWidth >= AppDimen.tabletWidth
                CompositionLocalProvider(
                    LocalToastController provides toastController,
                    LocalIsTablet provides isTablet
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Navigator(initialScreen) { navigator ->
                            if (showSessionExpiredDialog) {
                                AppAlertDialog(
                                    title = stringResource(Res.string.dialog_session_expired_title),
                                    description = stringResource(Res.string.dialog_session_expired_desc),
                                    confirmText = stringResource(Res.string.btn_ok),
                                    dismissText = "",
                                    onConfirm = {
                                        showSessionExpiredDialog = false
                                        navigator.replaceAll(LoginScreen())
                                    },
                                    onDismiss = {
                                        showSessionExpiredDialog = false
                                        navigator.replaceAll(LoginScreen())
                                    }
                                )
                            }
                            CurrentScreen()
                        }

                        AppToast(
                            message = toastController.message,
                            visible = toastController.visible,
                            logoIcon = toastController.logoIcon,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(
                                    bottom = AppDimen.p80,
                                    start = AppDimen.p24,
                                    end = AppDimen.p24
                                )
                        )
                    }
                }
            }
        }
    }
}
