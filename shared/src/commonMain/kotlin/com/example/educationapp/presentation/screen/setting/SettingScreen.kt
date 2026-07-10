package com.example.educationapp.presentation.screen.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.button.AppButton
import com.example.educationapp.core.ui.dialog.AppAlertDialog
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.layout.AppScaffold
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.row.OptionRow
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.enums.AppThemeMode
import com.example.educationapp.presentation.screen.login.LoginScreen
import com.example.educationapp.presentation.screen.setting.composable.AboutAppSection
import com.example.educationapp.presentation.screen.setting.composable.LanguageBottomSheet
import com.example.educationapp.presentation.screen.setting.composable.ThemeOptionCard
import com.example.educationapp.presentation.screen.update_profile.EditProfileScreen
import com.example.educationapp.presentation.screenmodel.setting.SettingScreenModel
import com.example.educationapp.presentation.screenmodel.setting.SettingState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.btn_cancel
import educationapp.shared.generated.resources.btn_change_password
import educationapp.shared.generated.resources.btn_edit_profile
import educationapp.shared.generated.resources.dialog_logout_desc
import educationapp.shared.generated.resources.ic_dark_mode_24dp
import educationapp.shared.generated.resources.ic_edit_24dp
import educationapp.shared.generated.resources.ic_language_24dp
import educationapp.shared.generated.resources.ic_light_mode_24dp
import educationapp.shared.generated.resources.ic_lock_24dp
import educationapp.shared.generated.resources.ic_palette_24dp
import educationapp.shared.generated.resources.ic_system_24dp
import educationapp.shared.generated.resources.lb_logout
import educationapp.shared.generated.resources.option_language
import educationapp.shared.generated.resources.option_theme
import educationapp.shared.generated.resources.section_account
import educationapp.shared.generated.resources.section_preferences
import educationapp.shared.generated.resources.theme_dark
import educationapp.shared.generated.resources.theme_light
import educationapp.shared.generated.resources.theme_system
import educationapp.shared.generated.resources.title_settings
import org.jetbrains.compose.resources.stringResource

class SettingScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SettingScreenModel>()
        val uiState by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        var showLogoutDialog by remember { mutableStateOf(false) }
        var showLanguageSheet by remember { mutableStateOf(false) }

        if (showLogoutDialog) {
            AppAlertDialog(
                title = stringResource(Res.string.lb_logout),
                description = stringResource(Res.string.dialog_logout_desc),
                confirmText = stringResource(Res.string.lb_logout),
                dismissText = stringResource(Res.string.btn_cancel),
                isConfirmDestructive = true,
                onConfirm = {
                    showLogoutDialog = false
                    screenModel.logout()
                },
                onDismiss = {
                    showLogoutDialog = false
                }
            )
        }

        if (showLanguageSheet) {
            LanguageBottomSheet(
                selectedLanguage = uiState.language,
                onLanguageSelected = { language ->
                    screenModel.onLanguageSelected(language)
                    showLanguageSheet = false
                },
                onDismiss = { showLanguageSheet = false }
            )
        }

        LaunchedEffect(uiState.logoutState) {
            if (uiState.logoutState is SettingState.LoggedOut) {
                navigator.replaceAll(LoginScreen())
            }
        }

        AppScaffold(
            topBar = {
                AppTopBar(
                    title = stringResource(Res.string.title_settings),
                    onBackClick = { navigator.pop() },
                    containerColor = MaterialTheme.colorScheme.background
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(AppDimen.p16),
                contentPadding = PaddingValues(
                    start = AppDimen.p16,
                    top = AppDimen.p16,
                    end = AppDimen.p16,
                    bottom = AppDimen.p24
                )
            ) {
                item {
                    AppText(
                        text = stringResource(Res.string.section_account),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = AppDimen.p4)
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(AppDimen.p16),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column {
                            OptionRow(
                                iconRes = Res.drawable.ic_edit_24dp,
                                title = stringResource(Res.string.btn_edit_profile),
                                onClick = { navigator.push(EditProfileScreen()) }
                            )

                            SettingDivider()

                            OptionRow(
                                iconRes = Res.drawable.ic_lock_24dp,
                                title = stringResource(Res.string.btn_change_password),
                                onClick = { navigator.push(ChangePasswordScreen()) }
                            )
                        }
                    }
                }

                item {
                    AppText(
                        text = stringResource(Res.string.section_preferences),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = AppDimen.p4)
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(AppDimen.p16),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = AppDimen.p16,
                                        top = AppDimen.p16,
                                        end = AppDimen.p16
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppIcon(
                                    drawableRes = Res.drawable.ic_palette_24dp,
                                    tint = MaterialTheme.colorScheme.primary,
                                    iconModifier = Modifier.size(AppDimen.p24)
                                )
                                Spacer(modifier = Modifier.width(AppDimen.p16))
                                AppText(
                                    text = stringResource(Res.string.option_theme),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .widthIn(max = AppDimen.p480)
                                    .fillMaxWidth()
                                    .padding(horizontal = AppDimen.p16, vertical = AppDimen.p12),
                                horizontalArrangement = Arrangement.spacedBy(AppDimen.p12)
                            ) {
                                val themes = listOf(
                                    Triple(
                                        AppThemeMode.LIGHT,
                                        Res.string.theme_light,
                                        Res.drawable.ic_light_mode_24dp
                                    ),
                                    Triple(
                                        AppThemeMode.DARK,
                                        Res.string.theme_dark,
                                        Res.drawable.ic_dark_mode_24dp
                                    ),
                                    Triple(
                                        AppThemeMode.SYSTEM,
                                        Res.string.theme_system,
                                        Res.drawable.ic_system_24dp
                                    )
                                )

                                themes.forEach { (mode, nameResId, icon) ->
                                    val isSelected = uiState.themeMode == mode
                                    ThemeOptionCard(
                                        themeMode = mode,
                                        label = stringResource(nameResId),
                                        icon = icon,
                                        selected = isSelected,
                                        onClick = { screenModel.onThemeModeSelected(mode) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            SettingDivider()

                            OptionRow(
                                iconRes = Res.drawable.ic_language_24dp,
                                title = stringResource(Res.string.option_language),
                                description = "${uiState.language.flagEmoji}  ${uiState.language.displayName}",
                                onClick = { showLanguageSheet = true }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(AppDimen.p8))
                }

                item {
                    AppButton(
                        text = stringResource(Res.string.lb_logout),
                        onClick = { showLogoutDialog = true },
                        isLoading = uiState.logoutState is SettingState.Loading,
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                        elevation = AppDimen.p3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppDimen.p8)
                    )
                }

                item {
                    AboutAppSection()
                }
            }
        }
    }
}

@Composable
private fun SettingDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimen.p16)
            .height(AppDimen.p1)
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    )
}
