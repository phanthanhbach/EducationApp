package com.example.educationapp.presentation.screen.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.chip.AppChip
import com.example.educationapp.core.ui.dialog.AppAlertDialog
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.layout.AppScaffold
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.row.OptionRow
import com.example.educationapp.core.ui.sheet.AppBottomSheet
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.enums.AppLanguage
import com.example.educationapp.domain.enums.AppThemeMode
import com.example.educationapp.presentation.screen.login.LoginScreen
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.btn_cancel
import educationapp.shared.generated.resources.btn_change_password
import educationapp.shared.generated.resources.btn_edit_profile
import educationapp.shared.generated.resources.dialog_logout_desc
import educationapp.shared.generated.resources.ic_check_circle_24dp
import educationapp.shared.generated.resources.ic_edit_24dp
import educationapp.shared.generated.resources.ic_language_24dp
import educationapp.shared.generated.resources.ic_lock_24dp
import educationapp.shared.generated.resources.ic_palette_24dp
import educationapp.shared.generated.resources.language_english
import educationapp.shared.generated.resources.language_vietnamese
import educationapp.shared.generated.resources.lb_logout
import educationapp.shared.generated.resources.option_language
import educationapp.shared.generated.resources.option_theme
import educationapp.shared.generated.resources.section_account
import educationapp.shared.generated.resources.section_preferences
import educationapp.shared.generated.resources.sheet_title_language
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
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = AppDimen.p16,
                    top = AppDimen.p16,
                    end = AppDimen.p16,
                    bottom = 24.dp
                )
            ) {
                item {
                    AppText(
                        text = stringResource(Res.string.section_account),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 4.dp)
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
                                onClick = { /* TODO: Handle edit profile */ }
                            )

                            SettingDivider()

                            OptionRow(
                                iconRes = Res.drawable.ic_lock_24dp,
                                title = stringResource(Res.string.btn_change_password),
                                onClick = { /* TODO: Handle change password */ }
                            )
                        }
                    }
                }

                item {
                    AppText(
                        text = stringResource(Res.string.section_preferences),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 4.dp)
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
                                iconRes = Res.drawable.ic_palette_24dp,
                                title = stringResource(Res.string.option_theme),
                                onClick = {},
                                trailing = {
                                    Row(
                                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AppChip(
                                            text = stringResource(Res.string.theme_system),
                                            selected = uiState.themeMode == AppThemeMode.SYSTEM,
                                            onClick = { screenModel.onThemeModeSelected(AppThemeMode.SYSTEM) }
                                        )
                                        AppChip(
                                            text = stringResource(Res.string.theme_light),
                                            selected = uiState.themeMode == AppThemeMode.LIGHT,
                                            onClick = { screenModel.onThemeModeSelected(AppThemeMode.LIGHT) }
                                        )
                                        AppChip(
                                            text = stringResource(Res.string.theme_dark),
                                            selected = uiState.themeMode == AppThemeMode.DARK,
                                            onClick = { screenModel.onThemeModeSelected(AppThemeMode.DARK) }
                                        )
                                    }
                                }
                            )

                            SettingDivider()

                            OptionRow(
                                iconRes = Res.drawable.ic_language_24dp,
                                title = stringResource(Res.string.option_language),
                                description = languageLabel(uiState.language),
                                onClick = { showLanguageSheet = true }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    Button(
                        onClick = { showLogoutDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = uiState.logoutState !is SettingState.Loading
                    ) {
                        if (uiState.logoutState is SettingState.Loading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onError,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            AppText(
                                text = stringResource(Res.string.lb_logout),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
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
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageBottomSheet(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit,
) {
    AppBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AppDimen.p16),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppText(
                modifier = Modifier.padding(horizontal = AppDimen.p16),
                text = stringResource(Res.string.sheet_title_language),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppLanguage.entries.forEach { language ->
                LanguageSheetItem(
                    label = languageLabel(language),
                    selected = language == selectedLanguage,
                    onClick = { onLanguageSelected(language) }
                )
            }
        }
    }
}

@Composable
private fun LanguageSheetItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = AppDimen.p16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppText(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (selected) {
            AppIcon(
                drawableRes = Res.drawable.ic_check_circle_24dp,
                tint = MaterialTheme.colorScheme.primary,
                iconModifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun languageLabel(language: AppLanguage): String {
    return when (language) {
        AppLanguage.ENGLISH -> stringResource(Res.string.language_english)
        AppLanguage.VIETNAMESE -> stringResource(Res.string.language_vietnamese)
    }
}
