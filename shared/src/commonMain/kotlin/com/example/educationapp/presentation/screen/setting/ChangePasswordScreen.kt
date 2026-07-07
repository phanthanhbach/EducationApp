package com.example.educationapp.presentation.screen.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.button.AppButton
import com.example.educationapp.core.ui.layout.AppScaffold
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.ui.textfield.AppTextFieldLabelStyle
import com.example.educationapp.core.ui.textfield.PasswordTextField
import com.example.educationapp.presentation.screenmodel.ChangePasswordScreenModel
import com.example.educationapp.presentation.screenmodel.ChangePasswordState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.btn_change_password
import educationapp.shared.generated.resources.lbl_confirm_password
import educationapp.shared.generated.resources.lbl_confirm_password_placeholder
import educationapp.shared.generated.resources.lbl_current_password
import educationapp.shared.generated.resources.lbl_current_password_placeholder
import educationapp.shared.generated.resources.lbl_new_password
import educationapp.shared.generated.resources.lbl_new_password_placeholder
import org.jetbrains.compose.resources.stringResource

class ChangePasswordScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ChangePasswordScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        var currentPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }

        LaunchedEffect(state) {
            if (state is ChangePasswordState.Success) {
                screenModel.resetState()
                navigator.pop()
            }
        }

        AppScaffold(
            topBar = {
                AppTopBar(
                    title = stringResource(Res.string.btn_change_password),
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
                contentPadding = PaddingValues(
                    start = AppDimen.p16,
                    top = AppDimen.p16,
                    end = AppDimen.p16,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        PasswordTextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            label = stringResource(Res.string.lbl_current_password),
                            labelStyle = AppTextFieldLabelStyle.External,
                            placeholder = stringResource(Res.string.lbl_current_password_placeholder),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state !is ChangePasswordState.Loading,
                            imeAction = ImeAction.Next
                        )

                        PasswordTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = stringResource(Res.string.lbl_new_password),
                            labelStyle = AppTextFieldLabelStyle.External,
                            placeholder = stringResource(Res.string.lbl_new_password_placeholder),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state !is ChangePasswordState.Loading,
                            imeAction = ImeAction.Next
                        )

                        PasswordTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = stringResource(Res.string.lbl_confirm_password),
                            labelStyle = AppTextFieldLabelStyle.External,
                            placeholder = stringResource(Res.string.lbl_confirm_password_placeholder),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state !is ChangePasswordState.Loading,
                            imeAction = ImeAction.Done
                        )
                    }
                }

                if (state is ChangePasswordState.Error) {
                    item {
                        AppText(
                            text = (state as ChangePasswordState.Error).message.asString(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    AppButton(
                        text = stringResource(Res.string.btn_change_password),
                        onClick = {
                            screenModel.changePassword(
                                current = currentPassword,
                                new = newPassword,
                                confirm = confirmPassword
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = currentPassword.isNotBlank() && newPassword.isNotBlank() && confirmPassword.isNotBlank(),
                        isLoading = state is ChangePasswordState.Loading
                    )
                }
            }
        }
    }
}
