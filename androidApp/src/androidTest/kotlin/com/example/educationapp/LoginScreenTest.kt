package com.example.educationapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.educationapp.presentation.screen.login.LoginScreen
import com.example.educationapp.presentation.screenmodel.login.LoginState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Test cho màn hình Login sử dụng Compose UI Test & Espresso Runner.
 */
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Helper extension function để định vị chính xác ô gõ chữ (TextField) 
     * nằm bên trong các Custom Component như EmailOrPhoneTextField / PasswordTextField.
     */
    private fun onTextFieldWithTag(tag: String) =
        composeTestRule.onNode(hasParent(hasTestTag(tag)) and hasSetTextAction())

    @Test
    fun testLoginScreen_componentsAreDisplayed() {
        composeTestRule.setContent {
            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            LoginScreen().LoginForm(
                username = username,
                onUsernameChange = { username = it },
                password = password,
                onPasswordChange = { password = it },
                state = LoginState.Idle,
                onLoginClick = {},
                onForgotPasswordClick = {}
            )
        }

        // Kiểm tra ô Username (Container bọc ngoài) hiển thị
        composeTestRule.onNodeWithTag("input_username")
            .assertIsDisplayed()

        // Kiểm tra ô Password (Container bọc ngoài) hiển thị
        composeTestRule.onNodeWithTag("input_password")
            .assertIsDisplayed()

        // Kiểm tra Nút Đăng nhập hiển thị và Enabled
        composeTestRule.onNodeWithTag("btn_login")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun testLogin_fillFormAndSubmit() {
        var clicked = false
        var inputEmail = ""
        var inputPass = ""

        composeTestRule.setContent {
            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            LoginScreen().LoginForm(
                username = username,
                onUsernameChange = { username = it },
                password = password,
                onPasswordChange = { password = it },
                state = LoginState.Idle,
                onLoginClick = { 
                    clicked = true 
                    inputEmail = username
                    inputPass = password
                },
                onForgotPasswordClick = {}
            )
        }

        // 1. Định vị ô nhập text bên trong tag "input_username" và gõ email
        onTextFieldWithTag("input_username")
            .performTextInput("teacher@school.edu.vn")

        // 2. Định vị ô nhập text bên trong tag "input_password" và gõ mật khẩu
        onTextFieldWithTag("input_password")
            .performTextInput("12345678")

        // 3. Bấm nút Đăng nhập
        composeTestRule.onNodeWithTag("btn_login")
            .performClick()

        // 4. Báo cáo kết quả thành công
        assert(clicked)
        assert(inputEmail == "teacher@school.edu.vn")
        assert(inputPass == "12345678")
    }
}
