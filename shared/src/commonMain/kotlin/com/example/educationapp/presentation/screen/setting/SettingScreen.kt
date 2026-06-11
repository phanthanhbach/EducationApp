package com.example.educationapp.presentation.screen.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.dialog.AppAlertDialog
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.layout.AppScaffold
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.layout.ThreeSectionRow

import com.example.educationapp.core.ui.row.OptionRow
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.presentation.screen.login.LoginScreen
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.btn_cancel
import educationapp.shared.generated.resources.btn_change_password
import educationapp.shared.generated.resources.btn_edit_profile
import educationapp.shared.generated.resources.dialog_logout_desc
import educationapp.shared.generated.resources.ic_arrow_forward_ios_24dp
import educationapp.shared.generated.resources.ic_edit_24dp
import educationapp.shared.generated.resources.ic_lock_24dp
import educationapp.shared.generated.resources.lb_logout
import educationapp.shared.generated.resources.section_account
import educationapp.shared.generated.resources.title_settings
import org.jetbrains.compose.resources.stringResource

class SettingScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SettingScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        var showLogoutDialog by remember { mutableStateOf(false) }

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

        LaunchedEffect(state) {
            if (state is SettingState.LoggedOut) {
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

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = AppDimen.p16)
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            )

                            OptionRow(
                                iconRes = Res.drawable.ic_lock_24dp,
                                title = stringResource(Res.string.btn_change_password),
                                onClick = { /* TODO: Handle change password */ }
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
                        enabled = state !is SettingState.Loading
                    ) {
                        if (state is SettingState.Loading) {
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
