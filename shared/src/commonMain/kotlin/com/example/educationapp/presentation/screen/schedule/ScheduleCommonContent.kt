package com.example.educationapp.presentation.screen.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.util.CalendarHelper
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.model.ScheduleSessionUiModel
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.schedule_screen_title
import educationapp.shared.generated.resources.schedule_screen_title_student
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource


@Composable
fun ScheduleCommonContent(
    role: AppRole,
    selectedDate: LocalDate,
    isMonthExpanded: Boolean,
    schedulesState: ScheduleState,
    filteredSchedules: List<ScheduleSessionUiModel>,
    highlightDates: Set<LocalDate>,
    onSessionClick: ((ScheduleSessionUiModel) -> Unit)?,
    onDateSelected: (LocalDate) -> Unit,
    onToggleExpand: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = remember { CalendarHelper.getCurrentDate() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            val titleRes = if (role == AppRole.TEACHER) {
                Res.string.schedule_screen_title
            } else {
                Res.string.schedule_screen_title_student
            }

            AppTopBar(
                titleContent = {
                    AppText(
                        text = stringResource(titleRes),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                isTitleCentered = false
            )

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                val isTablet = maxWidth >= 600.dp

                if (isTablet) {
                    ScheduleTabletLayout(
                        role = role,
                        selectedDate = selectedDate,
                        isMonthExpanded = isMonthExpanded,
                        schedulesState = schedulesState,
                        filteredSchedules = filteredSchedules,
                        highlightDates = highlightDates,
                        today = today,
                        onDateSelected = onDateSelected,
                        onToggleExpand = onToggleExpand,
                        onSessionClick = onSessionClick,
                        onRetry = onRetry
                    )
                } else {
                    ScheduleMobileLayout(
                        role = role,
                        selectedDate = selectedDate,
                        isMonthExpanded = isMonthExpanded,
                        schedulesState = schedulesState,
                        filteredSchedules = filteredSchedules,
                        highlightDates = highlightDates,
                        today = today,
                        onDateSelected = onDateSelected,
                        onToggleExpand = onToggleExpand,
                        onSessionClick = onSessionClick,
                        onRetry = onRetry
                    )
                }
            }
        }
    }
}
