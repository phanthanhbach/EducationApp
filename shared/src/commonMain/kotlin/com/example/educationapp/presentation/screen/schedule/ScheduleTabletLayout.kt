package com.example.educationapp.presentation.screen.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.educationapp.core.ui.shimmer.skeleton.ScheduleSessionSkeleton
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
import dev.chrisbanes.haze.hazeSource

@Composable
fun CommonScheduleTabletLayout(
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
 
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isLandscapePhone = maxHeight < 420.dp

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppDimen.p16),
            horizontalArrangement = Arrangement.spacedBy(if (isLandscapePhone) AppDimen.p12 else AppDimen.p24)
        ) {
            // Left Column: Interactive Calendar
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(AppDimen.p16))

                ScheduleCalendar(
                    selectedDate = selectedDate,
                    onDateSelected = onDateSelected,
                    highlightDates = highlightDates,
                    isMonthExpanded = isMonthExpanded,
                    onToggleExpand = onToggleExpand,
                    isLandscape = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Right Column: Session Details List
            Column(
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxHeight()
            ) {
                // Header for details
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(top = AppDimen.p16)
                        .padding(horizontal = AppDimen.p8),
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
                        fontSize = 20.sp,
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

                Spacer(modifier = Modifier.height(if (isLandscapePhone) AppDimen.p8 else AppDimen.p12))

                when (schedulesState) {
                    is ScheduleState.Loading -> {
                        ScheduleSessionSkeleton(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                                .padding(16.dp),
                            sessionCount = 3,
                            showDateHeader = true
                        )
                    }

                    is ScheduleState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            ScheduleErrorView(
                                message = schedulesState.message,
                                onRetry = onRetry
                            )
                        }
                    }

                    else -> {
                        if (filteredSchedules.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                EmptyScheduleView(role = role)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                contentPadding = PaddingValues(
                                    start = AppDimen.p4,
                                    end = AppDimen.p4,
                                    bottom = AppDimen.p24
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
    }
}
