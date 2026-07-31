package com.example.educationapp

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.educationapp.presentation.screen.attendance.AttendanceScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Test cho AttendanceScreen (Màn hình điểm danh học sinh).
 */
@RunWith(AndroidJUnit4::class)
class AttendanceScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAttendanceScreen_renderScreen() {
        composeTestRule.setContent {
            AttendanceScreen(
                classId = 1, sessionNumber = 1,
                className = "TOEIC 650",
                subjectName = "TOEIC",
                readOnly = true
            ).Content()
        }

        // Kiểm tra tiêu đề hoặc nút back hiển thị
        composeTestRule.onNodeWithTag("attendance_search_input", useUnmergedTree = true)
            .assertExists()
    }
}
