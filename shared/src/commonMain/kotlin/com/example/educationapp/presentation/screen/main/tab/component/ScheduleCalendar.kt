package com.example.educationapp.presentation.screen.main.tab.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.util.CalendarHelper
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.calendar_day_1_short
import educationapp.shared.generated.resources.calendar_day_2_short
import educationapp.shared.generated.resources.calendar_day_3_short
import educationapp.shared.generated.resources.calendar_day_4_short
import educationapp.shared.generated.resources.calendar_day_5_short
import educationapp.shared.generated.resources.calendar_day_6_short
import educationapp.shared.generated.resources.calendar_day_7_short
import educationapp.shared.generated.resources.calendar_year_suffix
import educationapp.shared.generated.resources.ic_arrow_back_ios_new_24dp
import educationapp.shared.generated.resources.ic_arrow_forward_ios_24dp
import educationapp.shared.generated.resources.ic_calendar_month_filled_24dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.jetbrains.compose.resources.stringResource

/**
 * A beautiful, adaptive calendar component for KMP.
 * Handles week view strip, month view grid (dynamic rows), and month/year selection.
 */
@Composable
fun ScheduleCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    highlightDates: Set<LocalDate>,
    isMonthExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false
) {
    val today = remember { CalendarHelper.getCurrentDate() }
    var showYearMonthPicker by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(AppDimen.p16),
                clip = false
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(AppDimen.p16)
    ) {
        Column(
            modifier = Modifier.padding(AppDimen.p16)
        ) {
            CalendarControlHeader(
                selectedDate = selectedDate,
                isMonthExpanded = isMonthExpanded,
                onPrevClick = {
                    onDateSelected(
                        if (isMonthExpanded) getOffsetMonth(selectedDate, -1)
                        else LocalDate.fromEpochDays(selectedDate.toEpochDays() - 7)
                    )
                },
                onNextClick = {
                    onDateSelected(
                        if (isMonthExpanded) getOffsetMonth(selectedDate, 1)
                        else LocalDate.fromEpochDays(selectedDate.toEpochDays() + 7)
                    )
                },
                onToggleExpand = onToggleExpand,
                onHeaderClick = { showYearMonthPicker = true }
            )

            Spacer(modifier = Modifier.height(AppDimen.p16))

            AnimatedContent(
                targetState = isMonthExpanded,
                transitionSpec = {
                    expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)) togetherWith
                            shrinkVertically(animationSpec = tween(300)) + fadeOut(
                        animationSpec = tween(
                            300
                        )
                    )
                }
            ) { expanded ->
                if (expanded) {
                    MonthGridView(
                        selectedDate = selectedDate,
                        onDateSelected = onDateSelected,
                        highlightDates = highlightDates
                    )
                } else {
                    WeekStripView(
                        selectedDate = selectedDate,
                        today = today,
                        onDateSelected = onDateSelected,
                        highlightDates = highlightDates
                    )
                }
            }
        }
    }

    if (showYearMonthPicker) {
        YearMonthPickerDialog(
            currentDate = selectedDate,
            isLandscape = isLandscape,
            onDismiss = { showYearMonthPicker = false },
            onConfirm = { year, month ->
                onDateSelected(LocalDate(year, month, 1))
                showYearMonthPicker = false
            }
        )
    }
}

@Composable
private fun CalendarControlHeader(
    selectedDate: LocalDate,
    isMonthExpanded: Boolean,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onToggleExpand: () -> Unit,
    onHeaderClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val monthLabel = CalendarHelper.getMonthDisplayName(selectedDate)
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onHeaderClick() }
                .padding(horizontal = AppDimen.p8, vertical = AppDimen.p4)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppText(
                    text = monthLabel,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                AppText(
                    text = "▼",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            AppText(
                text = stringResource(Res.string.calendar_year_suffix, selectedDate.year),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimen.p4)
        ) {
            AppIcon(
                drawableRes = Res.drawable.ic_arrow_back_ios_new_24dp,
                tint = MaterialTheme.colorScheme.primary,
                iconModifier = Modifier.size(16.dp),
                boxModifier = Modifier.size(40.dp),
                onClick = onPrevClick
            )

            AppIcon(
                drawableRes = Res.drawable.ic_arrow_forward_ios_24dp,
                tint = MaterialTheme.colorScheme.primary,
                iconModifier = Modifier.size(16.dp),
                boxModifier = Modifier.size(40.dp),
                onClick = onNextClick
            )

            AppIcon(
                drawableRes = Res.drawable.ic_calendar_month_filled_24dp,
                tint = if (isMonthExpanded) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                iconModifier = Modifier.size(20.dp),
                boxModifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isMonthExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else Color.Transparent
                    ),
                onClick = onToggleExpand
            )
        }
    }
}

@Composable
private fun WeekStripView(
    selectedDate: LocalDate,
    today: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    highlightDates: Set<LocalDate>
) {
    val weekDates = remember(selectedDate) {
        CalendarHelper.getWeekDates(selectedDate)
    }

    val dayNames = listOf(
        stringResource(Res.string.calendar_day_1_short),
        stringResource(Res.string.calendar_day_2_short),
        stringResource(Res.string.calendar_day_3_short),
        stringResource(Res.string.calendar_day_4_short),
        stringResource(Res.string.calendar_day_5_short),
        stringResource(Res.string.calendar_day_6_short),
        stringResource(Res.string.calendar_day_7_short)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weekDates.forEachIndexed { index, date ->
            val isSelected = date == selectedDate
            val isToday = date == today
            val dayName = dayNames[index]

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        } else {
                            Color.Transparent
                        }
                    )
                    .border(
                        width = if (isToday && !isSelected) 1.dp else 0.dp,
                        color = if (isToday && !isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onDateSelected(date)
                    }
                    .padding(vertical = AppDimen.p10),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppText(
                    text = dayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(AppDimen.p6))

                AppText(
                    text = date.day.toString(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(AppDimen.p4))

                val hasClasses = highlightDates.contains(date)
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected && hasClasses) MaterialTheme.colorScheme.primary
                            else if (!isSelected && hasClasses) MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.6f
                            )
                            else Color.Transparent
                        )
                )
            }
        }
    }
}

@Composable
private fun MonthGridView(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    highlightDates: Set<LocalDate>
) {
    val dates = remember(selectedDate.year, selectedDate.month.number) {
        CalendarHelper.getMonthGridDates(selectedDate)
    }

    val rowsCount = remember(dates, selectedDate) {
        when {
            dates[28].month.number != selectedDate.month.number -> 4
            dates[35].month.number != selectedDate.month.number -> 5
            else -> 6
        }
    }

    val dayNames = listOf(
        stringResource(Res.string.calendar_day_1_short),
        stringResource(Res.string.calendar_day_2_short),
        stringResource(Res.string.calendar_day_3_short),
        stringResource(Res.string.calendar_day_4_short),
        stringResource(Res.string.calendar_day_5_short),
        stringResource(Res.string.calendar_day_6_short),
        stringResource(Res.string.calendar_day_7_short)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            dayNames.forEach { dayName ->
                AppText(
                    text = dayName,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(AppDimen.p8))

        for (row in 0 until rowsCount) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (col in 0 until 7) {
                    val dateIndex = row * 7 + col
                    val date = dates[dateIndex]
                    val isSelected = date == selectedDate
                    val isCurrentMonth = date.month.number == selectedDate.month.number

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .padding(AppDimen.p2)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                } else {
                                    Color.Transparent
                                }
                            )
                            .clickable { onDateSelected(date) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AppText(
                                text = date.day.toString(),
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    isCurrentMonth -> MaterialTheme.colorScheme.onSurface
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                }
                            )

                            val hasClasses = highlightDates.contains(date)
                            if (hasClasses) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .size(3.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else if (isCurrentMonth) MaterialTheme.colorScheme.primary.copy(
                                                alpha = 0.6f
                                            )
                                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getOffsetMonth(date: LocalDate, offset: Int): LocalDate {
    var newMonth = date.month.number + offset
    var newYear = date.year
    while (newMonth > 12) {
        newMonth -= 12
        newYear += 1
    }
    while (newMonth < 1) {
        newMonth += 12
        newYear -= 1
    }
    val lastDay = when (newMonth) {
        2 -> if ((newYear % 4 == 0 && newYear % 100 != 0) || (newYear % 400 == 0)) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }
    val targetDay = date.day.coerceAtMost(lastDay)
    return LocalDate(newYear, newMonth, targetDay)
}
