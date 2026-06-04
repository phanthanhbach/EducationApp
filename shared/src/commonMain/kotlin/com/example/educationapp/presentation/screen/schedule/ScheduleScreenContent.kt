package com.example.educationapp.presentation.screen.schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleSessionUiModel
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleState
import kotlinx.datetime.LocalDate

@Composable
fun ScheduleScreenContent(
    role: AppRole,
    selectedDate: LocalDate,
    isMonthExpanded: Boolean,
    schedulesState: ScheduleState,
    filteredSchedules: List<ScheduleSessionUiModel>,
    highlightDates: Set<LocalDate>,
    onSessionClick: (ScheduleSessionUiModel) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onToggleExpand: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when (role) {
            AppRole.TEACHER -> {
                ScheduleTeacherContent(
                    selectedDate = selectedDate,
                    isMonthExpanded = isMonthExpanded,
                    schedulesState = schedulesState,
                    filteredSchedules = filteredSchedules,
                    highlightDates = highlightDates,
                    onSessionClick = onSessionClick,
                    onDateSelected = onDateSelected,
                    onToggleExpand = onToggleExpand,
                    onRetry = onRetry
                )
            }
            else -> OtherRolesScheduleView(role)
        }
    }
}

@Composable
fun OtherRolesScheduleView(
    role: AppRole,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(AppDimen.p24),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AppText(
                text = "Lịch Trình",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(AppDimen.p12))
            val roleText = when (role) {
                AppRole.STUDENT -> "Học sinh"
                AppRole.PARENT -> "Phụ huynh"
                else -> "Người dùng"
            }
            AppText(
                text = "Giao diện lịch của $roleText sẽ được thiết lập ở giai đoạn sau.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
