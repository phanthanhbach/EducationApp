package com.example.educationapp.presentation.screen.schedule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.button.AppTextButton
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.util.CalendarHelper
import com.example.educationapp.presentation.screen.main.tab.component.ScheduleCalendar
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleSessionUiModel
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_calendar_month_filled_24dp
import educationapp.shared.generated.resources.schedule_btn_retry
import educationapp.shared.generated.resources.schedule_btn_today
import educationapp.shared.generated.resources.schedule_empty_desc
import educationapp.shared.generated.resources.schedule_empty_title
import educationapp.shared.generated.resources.schedule_error_title
import educationapp.shared.generated.resources.schedule_header_date_format
import educationapp.shared.generated.resources.schedule_header_today
import educationapp.shared.generated.resources.schedule_screen_title
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ScheduleTeacherContent(
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
    val today = remember { CalendarHelper.getCurrentDate() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AppTopBar(
            titleContent = {
                AppText(
                    text = stringResource(Res.string.schedule_screen_title),
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
                TeacherScheduleTabletLayout(
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
                TeacherScheduleMobileLayout(
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

@Composable
fun TeacherScheduleMobileLayout(
    selectedDate: LocalDate,
    isMonthExpanded: Boolean,
    schedulesState: ScheduleState,
    filteredSchedules: List<ScheduleSessionUiModel>,
    highlightDates: Set<LocalDate>,
    today: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onToggleExpand: () -> Unit,
    onSessionClick: (ScheduleSessionUiModel) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            AppText(
                text = if (selectedDate == today) {
                    stringResource(Res.string.schedule_header_today)
                } else {
                    stringResource(
                        Res.string.schedule_header_date_format,
                        selectedDate.day,
                        selectedDate.month.number
                    )
                },
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp),
                        strokeWidth = 3.dp
                    )
                }
            }

            is ScheduleState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
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
                    EmptyScheduleView(modifier = Modifier.weight(1f))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentPadding = PaddingValues(
                            start = AppDimen.p16,
                            end = AppDimen.p16,
                            bottom = AppDimen.p24
                        ),
                        verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
                    ) {
                        items(filteredSchedules, key = { it.id }) { session ->
                            ClassSessionCard(
                                session = session,
                                onClick = { onSessionClick(session) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherScheduleTabletLayout(
    selectedDate: LocalDate,
    isMonthExpanded: Boolean,
    schedulesState: ScheduleState,
    filteredSchedules: List<ScheduleSessionUiModel>,
    highlightDates: Set<LocalDate>,
    today: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onToggleExpand: () -> Unit,
    onSessionClick: (ScheduleSessionUiModel) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                    AppText(
                        text = if (selectedDate == today) {
                            stringResource(Res.string.schedule_header_today)
                        } else {
                            stringResource(
                                Res.string.schedule_header_date_format,
                                selectedDate.day,
                                selectedDate.month.number
                            )
                        },
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
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp),
                                strokeWidth = 3.dp
                            )
                        }
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
                                EmptyScheduleView()
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
                                        onClick = { onSessionClick(session) }
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

@Composable
fun EmptyScheduleView(
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isLandscape = maxHeight < 360.dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(if (isLandscape) AppDimen.p12 else AppDimen.p32),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isLandscape) 50.dp else 100.dp)
                        .clip(RoundedCornerShape(if (isLandscape) 12.dp else 20.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_calendar_month_filled_24dp),
                        contentDescription = "Empty Schedule",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.size(if (isLandscape) 28.dp else 48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(if (isLandscape) AppDimen.p8 else AppDimen.p24))

                AppText(
                    text = stringResource(Res.string.schedule_empty_title),
                    fontSize = if (isLandscape) 16.sp else 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(AppDimen.p4))

                AppText(
                    text = stringResource(Res.string.schedule_empty_desc),
                    fontSize = if (isLandscape) 12.sp else 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ScheduleErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(AppDimen.p16),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(AppDimen.p20),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimen.p8)
        ) {
            AppText(
                text = stringResource(Res.string.schedule_error_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            AppText(
                text = message,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(AppDimen.p8))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(8.dp)
            ) {
                AppText(
                    text = stringResource(Res.string.schedule_btn_retry),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
