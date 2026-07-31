package com.example.educationapp

import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Full End-to-End (E2E) App Test.
 * 
 * Khởi chạy TOÀN BỘ ứng dụng từ MainActivity.
 * Bạn sẽ thấy ứng dụng thật tự mở lên trên giả lập, tự gõ chữ, tự chuyển màn hình!
 */
@RunWith(AndroidJUnit4::class)
class FullAppE2ETest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun onTextFieldWithTag(tag: String) =
        composeTestRule.onNode(hasParent(hasTestTag(tag)) and hasSetTextAction())

    @Test
    fun testFullAppFlow_LoginToMainScreen() {
        // 1. Tạm dừng 1.5 giây để bạn kịp nhìn thấy ứng dụng vừa mở trên Emulator
        Thread.sleep(1500)

        // 2. Tự động điền email trên App thật
        onTextFieldWithTag("input_username")
            .performTextInput("teacher@school.edu.vn")

        // 3. Tạm dừng 1 giây để bạn quan sát chữ được gõ vào ô email
        Thread.sleep(1000)

        // 4. Tự động điền mật khẩu trên App thật
        onTextFieldWithTag("input_password")
            .performTextInput("12345678")

        // 5. Tạm dừng 1 giây để bạn quan sát mật khẩu
        Thread.sleep(1000)

        // 6. Tự động bấm nút Đăng nhập
        composeTestRule.onNodeWithTag("btn_login")
            .performClick()

        // 7. Giữ màn hình 3 giây để bạn nhìn thấy App chuyển sang màn hình tiếp theo
        Thread.sleep(3000)
    }
}
