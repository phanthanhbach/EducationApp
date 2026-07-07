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
import com.example.educationapp.core.ui.textfield.AppTextFieldLabelStyle
import com.example.educationapp.core.ui.textfield.EmailTextField
import com.example.educationapp.presentation.screenmodel.forgot_password.ForgotPasswordScreenModel
import com.example.educationapp.presentation.screenmodel.forgot_password.ForgotPasswordState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.btn_send_reset_link
import educationapp.shared.generated.resources.forgot_password_desc
import educationapp.shared.generated.resources.forgot_password_title
import educationapp.shared.generated.resources.lb_email_address
import educationapp.shared.generated.resources.lb_email_address_placeholder
import educationapp.shared.generated.resources.ic_shield_lock_24dp
import org.jetbrains.compose.resources.stringResource

class ForgotPasswordScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ForgotPasswordScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        var email by remember { mutableStateOf("") }

        LaunchedEffect(state) {
            if (state is ForgotPasswordState.Success) {
                val emailSubmitted = email
                screenModel.resetState()
                navigator.push(ResetPasswordScreen(email = emailSubmitted))
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
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_shield_lock_24dp),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        AppText(
                            text = stringResource(Res.string.forgot_password_title),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        AppText(
                            text = stringResource(Res.string.forgot_password_desc),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    EmailTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = stringResource(Res.string.lb_email_address),
                        labelStyle = AppTextFieldLabelStyle.External,
                        placeholder = stringResource(Res.string.lb_email_address_placeholder),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state !is ForgotPasswordState.Loading,
                        imeAction = ImeAction.Done
                    )
                }

                if (state is ForgotPasswordState.Error) {
                    item {
                        AppText(
                            text = (state as ForgotPasswordState.Error).error.asString(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    AppButton(
                        text = stringResource(Res.string.btn_send_reset_link),
                        onClick = {
                            screenModel.submitEmail(email)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = email.isNotBlank(),
                        isLoading = state is ForgotPasswordState.Loading
                    )
                }
            }
        }
    }
}
