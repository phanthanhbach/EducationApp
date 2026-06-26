package com.example.educationapp.presentation.screen.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.educationapp.core.ui.shimmer.skeleton.ScheduleSessionSkeleton
import com.example.educationapp.core.ui.error.ErrorStateView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.button.AppTextButton
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.presentation.screen.main.tab.component.ScheduleCalendar
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleSessionUiModel
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleState
import com.example.educationapp.domain.enums.AppRole
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.schedule_btn_today
import educationapp.shared.generated.resources.schedule_header_date_format
import educationapp.shared.generated.resources.schedule_header_date_format_student
import educationapp.shared.generated.resources.schedule_header_today
import educationapp.shared.generated.resources.schedule_header_today_student
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.jetbrains.compose.resources.stringResource
import com.example.educationapp.presentation.screen.main.LocalSharedHazeState
import com.example.educationapp.presentation.screen.main.LocalBottomBarHeight
import dev.chrisbanes.haze.hazeSource

@Composable
fun CommonScheduleMobileLayout(
    role: AppRole,
    selectedDate: LocalDate,
    isMonthExpanded: Boolean,
    schedulesState: ScheduleState,
    filteredSchedules: List<ScheduleSessionUiModel>,
    highlightDates: Set<LocalDate>,
    today: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onToggleExpand: () -> Unit,
    onSessionClick: ((ScheduleSessionUiModel) -> Unit)?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sharedHazeState = LocalSharedHazeState.current
    val bottomBarHeight = LocalBottomBarHeight.current

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(AppDimen.p12))

        // Calendar Component
        ScheduleCalendar(
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
            highlightDates = highlightDates,
            isMonthExpanded = isMonthExpanded,
            onToggleExpand = onToggleExpand,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimen.p16)
        )

        Spacer(modifier = Modifier.height(AppDimen.p16))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = AppDimen.p20),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val headerText = if (selectedDate == today) {
                if (role == AppRole.TEACHER) {
                    stringResource(Res.string.schedule_header_today)
                } else {
                    stringResource(Res.string.schedule_header_today_student)
                }
            } else {
                val formatRes = if (role == AppRole.TEACHER) {
                    Res.string.schedule_header_date_format
                } else {
                    Res.string.schedule_header_date_format_student
                }
                stringResource(
                    formatRes,
                    selectedDate.day,
                    selectedDate.month.number
                )
            }

            AppText(
                text = headerText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (selectedDate != today) {
                AppTextButton(
                    text = stringResource(Res.string.schedule_btn_today),
                    onClick = { onDateSelected(today) },
                    modifier = Modifier.height(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(AppDimen.p8))

        // Content switching based on State
        when (schedulesState) {
            is ScheduleState.Loading -> {
                ScheduleSessionSkeleton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    sessionCount = 3,
                    showDateHeader = true
                )
            }

            is ScheduleState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorStateView(
                        error = schedulesState.error,
                        onRetry = onRetry,
                        modifier = Modifier.padding(AppDimen.p16)
                    )
                }
            }

            else -> {
                if (filteredSchedules.isEmpty()) {
                    EmptyScheduleView(
                        role = role,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentPadding = PaddingValues(
                            start = AppDimen.p16,
                            end = AppDimen.p16,
                            bottom = AppDimen.p24 + bottomBarHeight
                        ),
                        verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
                    ) {
                        items(filteredSchedules, key = { it.id }) { session ->
                            ClassSessionCard(
                                session = session,
                                onClick = onSessionClick?.let { click -> { click(session) } }
                            )
                        }
                    }
                }
            }
        }
    }
}
