package com.example.educationapp.presentation.screen.login

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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import org.jetbrains.compose.resources.painterResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.button.AppButton
import com.example.educationapp.core.ui.layout.AppScaffold
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.ui.textfield.AppTextField
import com.example.educationapp.core.ui.textfield.AppTextFieldLabelStyle
import com.example.educationapp.core.ui.textfield.PasswordTextField
import com.example.educationapp.presentation.screenmodel.login.ResetPasswordScreenModel
import com.example.educationapp.presentation.screenmodel.login.ResetPasswordState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.btn_submit
import educationapp.shared.generated.resources.lbl_confirm_password
import educationapp.shared.generated.resources.lbl_confirm_password_placeholder
import educationapp.shared.generated.resources.lbl_new_password
import educationapp.shared.generated.resources.lbl_new_password_placeholder
import educationapp.shared.generated.resources.lbl_reset_token
import educationapp.shared.generated.resources.lbl_reset_token_placeholder
import educationapp.shared.generated.resources.reset_password_desc
import educationapp.shared.generated.resources.reset_password_title
import educationapp.shared.generated.resources.ic_lock_reset_24dp
import org.jetbrains.compose.resources.stringResource

class ResetPasswordScreen(private val email: String = "") : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ResetPasswordScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        var token by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }

        LaunchedEffect(state) {
            if (state is ResetPasswordState.Success) {
                screenModel.resetState()
                navigator.popUntilRoot()
            }
        }

        AppScaffold(
            topBar = {
                AppTopBar(
                    title = "",
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
                    val descText = if (email.isNotEmpty()) {
                        "Mã khôi phục đã được gửi tới email: $email. Vui lòng nhập mã khôi phục và mật khẩu mới của bạn."
                    } else {
                        stringResource(Res.string.reset_password_desc)
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_lock_reset_24dp),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        AppText(
                            text = stringResource(Res.string.reset_password_title),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        AppText(
                            text = descText,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AppTextField(
                            value = token,
                            onValueChange = { token = it },
                            label = stringResource(Res.string.lbl_reset_token),
                            labelStyle = AppTextFieldLabelStyle.External,
                            placeholder = stringResource(Res.string.lbl_reset_token_placeholder),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state !is ResetPasswordState.Loading,
                            singleLine = true
                        )

                        PasswordTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = stringResource(Res.string.lbl_new_password),
                            labelStyle = AppTextFieldLabelStyle.External,
                            placeholder = stringResource(Res.string.lbl_new_password_placeholder),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state !is ResetPasswordState.Loading,
                            imeAction = ImeAction.Next
                        )

                        PasswordTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = stringResource(Res.string.lbl_confirm_password),
                            labelStyle = AppTextFieldLabelStyle.External,
                            placeholder = stringResource(Res.string.lbl_confirm_password_placeholder),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state !is ResetPasswordState.Loading,
                            imeAction = ImeAction.Done
                        )
                    }
                }

                if (state is ResetPasswordState.Error) {
                    item {
                        AppText(
                            text = (state as ResetPasswordState.Error).error.asString(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    AppButton(
                        text = stringResource(Res.string.btn_submit),
                        onClick = {
                            screenModel.resetPassword(
                                token = token,
                                new = newPassword,
                                confirm = confirmPassword
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = token.isNotBlank() && newPassword.isNotBlank() && confirmPassword.isNotBlank(),
                        isLoading = state is ResetPasswordState.Loading
                    )
                }
            }
        }
    }
}
