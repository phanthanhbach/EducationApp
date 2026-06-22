package com.example.educationapp.presentation.screen.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.DrawableResource
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.button.AppButton
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
import com.example.educationapp.presentation.screen.profile.EditProfileScreen
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.btn_cancel
import educationapp.shared.generated.resources.btn_change_password
import educationapp.shared.generated.resources.btn_edit_profile
import educationapp.shared.generated.resources.dialog_logout_desc
import educationapp.shared.generated.resources.ic_check_24dp
import educationapp.shared.generated.resources.ic_check_circle_24dp
import educationapp.shared.generated.resources.ic_edit_24dp
import educationapp.shared.generated.resources.ic_language_24dp
import educationapp.shared.generated.resources.ic_lock_24dp
import educationapp.shared.generated.resources.ic_palette_24dp
import educationapp.shared.generated.resources.ic_light_mode_24dp
import educationapp.shared.generated.resources.ic_dark_mode_24dp
import educationapp.shared.generated.resources.ic_system_24dp
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
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = AppDimen.p16, top = AppDimen.p16, end = AppDimen.p16),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppIcon(
                                    drawableRes = Res.drawable.ic_palette_24dp,
                                    tint = MaterialTheme.colorScheme.primary,
                                    iconModifier = Modifier.size(24.dp)
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
                                    .widthIn(max = 480.dp)
                                    .fillMaxWidth()
                                    .padding(horizontal = AppDimen.p16, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val themes = listOf(
                                    Triple(AppThemeMode.LIGHT, Res.string.theme_light, Res.drawable.ic_light_mode_24dp),
                                    Triple(AppThemeMode.DARK, Res.string.theme_dark, Res.drawable.ic_dark_mode_24dp),
                                    Triple(AppThemeMode.SYSTEM, Res.string.theme_system, Res.drawable.ic_system_24dp)
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
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    AppButton(
                        text = stringResource(Res.string.lb_logout),
                        onClick = { showLogoutDialog = true },
                        isLoading = uiState.logoutState is SettingState.Loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                            disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                            disabledContentColor = MaterialTheme.colorScheme.onError.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
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
    AppBottomSheet(
        onDismissRequest = onDismiss,
        title = stringResource(Res.string.sheet_title_language)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AppDimen.p16),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppLanguage.entries.forEach { language ->
                LanguageSheetItem(
                    flag = language.flagEmoji,
                    label = language.displayName,
                    selected = language == selectedLanguage,
                    onClick = { onLanguageSelected(language) }
                )
            }
        }
    }
}

@Composable
private fun LanguageSheetItem(
    flag: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(AppDimen.p16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppDimen.p16)
        ) {
            AppText(
                text = flag,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = AppDimen.s16,
                color = MaterialTheme.colorScheme.onSurface
            )
            AppText(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (selected) {
            AppIcon(
                drawableRes = Res.drawable.ic_check_24dp,
                tint = MaterialTheme.colorScheme.primary,
                iconModifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun ThemeOptionCard(
    themeMode: AppThemeMode,
    label: String,
    icon: DrawableResource,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .then(
                    if (selected) {
                        Modifier.border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                )
                .clip(RoundedCornerShape(12.dp))
        ) {
            ThemeMockupPreview(themeMode = themeMode)

            if (selected) {
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .size(16.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(
                        drawableRes = Res.drawable.ic_check_24dp,
                        tint = Color.White,
                        iconModifier = Modifier.size(10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AppIcon(
                drawableRes = icon,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                iconModifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            AppText(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ThemeMockupPreview(
    themeMode: AppThemeMode,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        when (themeMode) {
            AppThemeMode.LIGHT -> {
                ModePreviewContent(isDark = false)
            }
            AppThemeMode.DARK -> {
                ModePreviewContent(isDark = true)
            }
            AppThemeMode.SYSTEM -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().clipToBounds()) {
                        ModePreviewContent(isDark = false)
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().clipToBounds()) {
                        ModePreviewContent(isDark = true)
                    }
                }
            }
        }
    }
}

@Composable
private fun ModePreviewContent(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isDark) Color(0xFF161925) else Color(0xFFE2E4E9)
    val headerBgColor = if (isDark) Color(0xFF0F111A) else Color(0xFFD5D7DD)
    val headerDotColor = if (isDark) Color(0xFF25293C) else Color(0xFFB8BAC2)
    val cardBgColor = if (isDark) Color(0xFF1F2232) else Color(0xFFFFFFFF)
    val cardLineColor = if (isDark) Color(0xFF333852) else Color(0xFFE2E4E9)
    val primaryAccent = if (isDark) Color(0xFF8E97FD) else Color(0xFF475AD7)
    val secondaryAccent = Color(0xFF2ED33C)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .background(headerBgColor)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(headerDotColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .height(3.dp)
                    .width(20.dp)
                    .background(headerDotColor, RoundedCornerShape(1.5.dp))
            )
        }

        // Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PreviewCardItem(
                cardBgColor = cardBgColor,
                accentColor = primaryAccent,
                lineColor = cardLineColor
            )
            PreviewCardItem(
                cardBgColor = cardBgColor,
                accentColor = secondaryAccent,
                lineColor = cardLineColor
            )
        }
    }
}

@Composable
private fun PreviewCardItem(
    cardBgColor: Color,
    accentColor: Color,
    lineColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(18.dp)
            .background(cardBgColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .height(4.dp)
                .width(10.dp)
                .background(accentColor, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .height(3.dp)
                .weight(1f)
                .background(lineColor, RoundedCornerShape(1.5.dp))
        )
    }
}


