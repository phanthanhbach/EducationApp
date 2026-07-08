package com.example.educationapp.presentation.screen.my_classes

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.domain.enums.ClassStatus
import com.example.educationapp.domain.enums.StudentClassStatus
import org.jetbrains.compose.resources.stringResource

@Composable
fun resolveClassStatus(statusStr: String, role: AppRole): Pair<String, Color> {
    return if (role == AppRole.TEACHER) {
        val statusEnum = ClassStatus.fromString(statusStr)
        val text = statusEnum?.let { stringResource(it.labelRes) } ?: statusStr
        val color = when (statusEnum) {
            ClassStatus.UPCOMING -> Color(0xFF2196F3) // Professional Blue
            ClassStatus.ONGOING -> Color(0xFF4CAF50)  // Success Green
            ClassStatus.COMPLETED -> Color(0xFF9E9E9E) // Grey
            ClassStatus.CANCELLED -> Color(0xFFF44336) // Red
            null -> AppColor.Primary
        }
        text to color
    } else {
        val statusEnum = StudentClassStatus.fromString(statusStr)
        val text = statusEnum?.let { stringResource(it.labelRes) } ?: statusStr
        val color = when (statusEnum) {
            StudentClassStatus.ACTIVE -> Color(0xFF4CAF50)   // Success Green
            StudentClassStatus.COMPLETED -> Color(0xFF9E9E9E)// Grey
            StudentClassStatus.DROPPED -> Color(0xFFF44336)  // Red
            null -> AppColor.Primary
        }
        text to color
    }
}
