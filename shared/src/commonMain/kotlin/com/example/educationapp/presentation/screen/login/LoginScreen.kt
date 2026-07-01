package com.example.educationapp.presentation.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.button.AppButton
import com.example.educationapp.core.ui.button.AppTextButton
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.ui.textfield.PasswordTextField
import com.example.educationapp.presentation.screen.main.MainScreen
import com.example.educationapp.presentation.screenmodel.login.LoginScreenModel
import com.example.educationapp.presentation.screenmodel.login.LoginState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.lb_email_or_phone
import educationapp.shared.generated.resources.lb_email_or_phone_placeholder
import educationapp.shared.generated.resources.lb_login_desc
import educationapp.shared.generated.resources.lb_login_title
import educationapp.shared.generated.resources.lb_password
import educationapp.shared.generated.resources.lb_password_placeholder
import educationapp.shared.generated.resources.login_button
import educationapp.shared.generated.resources.login_description
import educationapp.shared.generated.resources.login_forgot_password
import educationapp.shared.generated.resources.login_tagline
import org.jetbrains.compose.resources.stringResource

class LoginScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<LoginScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        LaunchedEffect(state) {
            if (state is LoginState.Success) {
                val userInfo = (state as LoginState.Success).userInfo
                navigator.replaceAll(MainScreen(userInfo.userRole))
                screenModel.resetState()
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            val isLandscape = maxWidth > maxHeight
            val isTabletOrLandscape = maxWidth >= 600.dp || isLandscape
            val scrollState = rememberScrollState()

            if (isTabletOrLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(AppDimen.p24),
                    horizontalArrangement = Arrangement.spacedBy(AppDimen.p24),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(AppDimen.p16),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        AppText(
                            text = "EducationApp",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(AppDimen.p12))
                        AppText(
                            text = stringResource(Res.string.login_tagline),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(AppDimen.p8))
                        AppText(
                            text = stringResource(Res.string.login_description),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.Start
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight()
                            .verticalScroll(scrollState),
                        contentAlignment = Alignment.Center
                    ) {
                        LoginForm(
                            username = username,
                            onUsernameChange = { username = it },
                            password = password,
                            onPasswordChange = { password = it },
                            state = state,
                            onLoginClick = { screenModel.login(username, password) },
                            onForgotPasswordClick = { navigator.push(ForgotPasswordScreen()) },
                            modifier = Modifier
                                .widthIn(max = 400.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(AppDimen.p16)
                        .verticalScroll(scrollState),
                    contentAlignment = Alignment.Center
                ) {
                    LoginForm(
                        username = username,
                        onUsernameChange = { username = it },
                        password = password,
                        onPasswordChange = { password = it },
                        state = state,
                        onLoginClick = { screenModel.login(username, password) },
                        onForgotPasswordClick = { navigator.push(ForgotPasswordScreen()) },
                        modifier = Modifier
                            .widthIn(max = 400.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }

    @Composable
    private fun LoginForm(
        username: String,
        onUsernameChange: (String) -> Unit,
        password: String,
        onPasswordChange: (String) -> Unit,
        state: LoginState,
        onLoginClick: () -> Unit,
        onForgotPasswordClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimen.p16)
        ) {
            AppText(
                text = stringResource(Res.string.lb_login_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            AppText(
                text = stringResource(Res.string.lb_login_desc),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = AppDimen.p8)
            )

            EmailOrPhoneTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = stringResource(Res.string.lb_email_or_phone),
                placeholder = stringResource(Res.string.lb_email_or_phone_placeholder),
                modifier = Modifier.fillMaxWidth(),
                enabled = state !is LoginState.Loading
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppDimen.p4)
            ) {
                PasswordTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = stringResource(Res.string.lb_password),
                    placeholder = stringResource(Res.string.lb_password_placeholder),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state !is LoginState.Loading,
                    imeAction = ImeAction.Done
                )

                AppTextButton(
                    text = stringResource(Res.string.login_forgot_password),
                    onClick = onForgotPasswordClick,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            if (state is LoginState.Error) {
                AppText(
                    text = state.error.asString(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(AppDimen.p4))

            AppButton(
                text = stringResource(Res.string.login_button),
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
                isLoading = state is LoginState.Loading
            )
        }
    }
}
