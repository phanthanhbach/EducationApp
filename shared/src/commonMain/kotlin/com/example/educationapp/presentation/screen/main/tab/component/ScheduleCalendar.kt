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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.util.CalendarHelper
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_arrow_back_24dp
import educationapp.shared.generated.resources.ic_arrow_back_ios_new_24dp
import educationapp.shared.generated.resources.ic_arrow_forward_ios_24dp
import educationapp.shared.generated.resources.ic_calendar_month_filled_24dp
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource

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
    modifier: Modifier = Modifier
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
                text = "Năm ${selectedDate.year}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimen.p4)
        ) {
            IconButton(onClick = onPrevClick) {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_back_ios_new_24dp),
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }

            IconButton(onClick = onNextClick) {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_forward_ios_24dp),
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }

            IconButton(
                onClick = onToggleExpand,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (isMonthExpanded) MaterialTheme.colorScheme.primaryContainer
                        else Color.Transparent
                    )
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_calendar_month_filled_24dp),
                    contentDescription = "Toggle Month View",
                    tint = if (isMonthExpanded) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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

    val dayNames = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")

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
                            Brush.verticalGradient(
                                colors = listOf(AppColor.Primary, AppColor.Secondary)
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Transparent)
                            )
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
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(AppDimen.p6))

                AppText(
                    text = date.dayOfMonth.toString(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(AppDimen.p4))

                val hasClasses = highlightDates.contains(date)
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected && hasClasses) Color.White
                            else if (!isSelected && hasClasses) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
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
    val dates = remember(selectedDate.year, selectedDate.monthNumber) {
        CalendarHelper.getMonthGridDates(selectedDate)
    }

    val rowsCount = remember(dates, selectedDate) {
        when {
            dates[28].monthNumber != selectedDate.monthNumber -> 4
            dates[35].monthNumber != selectedDate.monthNumber -> 5
            else -> 6
        }
    }

    val dayNames = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")

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
                    val isCurrentMonth = date.monthNumber == selectedDate.monthNumber

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .padding(AppDimen.p2)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> AppColor.Primary
                                    else -> Color.Transparent
                                }
                            )
                            .clickable { onDateSelected(date) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AppText(
                                text = date.dayOfMonth.toString(),
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = when {
                                    isSelected -> Color.White
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
                                            if (isSelected) Color.White
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

@Composable
private fun YearMonthPickerDialog(
    currentDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (year: Int, month: Int) -> Unit
) {
    var chosenYear by remember { mutableStateOf(currentDate.year) }
    var chosenMonth by remember { mutableStateOf(currentDate.monthNumber) }

    val monthNamesShort = listOf(
        "Th1", "Th2", "Th3", "Th4", "Th5", "Th6",
        "Th7", "Th8", "Th9", "Th10", "Th11", "Th12"
    )

    Dialog(onDismissRequest = onDismiss) {
        BoxWithConstraints {
            val isLandscape = maxHeight < 420.dp

            Surface(
                modifier = Modifier
                    .width(if (isLandscape) 480.dp else 320.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(AppDimen.p20)
                ) {
                    AppText(
                        text = "Chọn Thời Gian",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(AppDimen.p16))

                    if (isLandscape) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppDimen.p16),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(AppDimen.p16),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(
                                                alpha = 0.3f
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = AppDimen.p4, vertical = AppDimen.p2),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { chosenYear-- }) {
                                        Icon(
                                            painter = painterResource(Res.drawable.ic_arrow_back_24dp),
                                            contentDescription = "Previous Year",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    AppText(
                                        text = "$chosenYear",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    IconButton(onClick = { chosenYear++ }) {
                                        Icon(
                                            painter = painterResource(Res.drawable.ic_arrow_forward_ios_24dp),
                                            contentDescription = "Next Year",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(onClick = onDismiss) {
                                        AppText(
                                            text = "Hủy",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Button(
                                        onClick = { onConfirm(chosenYear, chosenMonth) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        contentPadding = PaddingValues(
                                            horizontal = AppDimen.p12,
                                            vertical = AppDimen.p8
                                        )
                                    ) {
                                        AppText(
                                            text = "Chọn",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1.3f)
                            ) {
                                for (row in 0 until 3) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(AppDimen.p6)
                                    ) {
                                        for (col in 0 until 4) {
                                            val monthIndex = row * 4 + col
                                            val monthNum = monthIndex + 1
                                            val isSelected = monthNum == chosenMonth
                                            val monthName = monthNamesShort[monthIndex]

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(
                                                        if (isSelected) {
                                                            Brush.verticalGradient(
                                                                colors = listOf(
                                                                    AppColor.Primary,
                                                                    AppColor.Secondary
                                                                )
                                                            )
                                                        } else {
                                                            Brush.verticalGradient(
                                                                colors = listOf(
                                                                    MaterialTheme.colorScheme.surfaceVariant.copy(
                                                                        alpha = 0.4f
                                                                    ),
                                                                    MaterialTheme.colorScheme.surfaceVariant.copy(
                                                                        alpha = 0.4f
                                                                    )
                                                                )
                                                            )
                                                        }
                                                    )
                                                    .clickable { chosenMonth = monthNum }
                                                    .padding(vertical = AppDimen.p8),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AppText(
                                                    text = monthName,
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                    if (row < 2) {
                                        Spacer(modifier = Modifier.height(AppDimen.p6))
                                    }
                                }
                            }
                        }
                     } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = AppDimen.p8, vertical = AppDimen.p4),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { chosenYear-- }) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_arrow_back_24dp),
                                    contentDescription = "Previous Year",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            AppText(
                                text = "Năm $chosenYear",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            IconButton(onClick = { chosenYear++ }) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_arrow_forward_ios_24dp),
                                    contentDescription = "Next Year",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(AppDimen.p20))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(AppDimen.p8)
                        ) {
                            for (row in 0 until 4) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(AppDimen.p8)
                                ) {
                                    for (col in 0 until 3) {
                                        val monthIndex = row * 3 + col
                                        val monthNum = monthIndex + 1
                                        val isSelected = monthNum == chosenMonth
                                        val monthName = monthNamesShort[monthIndex]

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    if (isSelected) {
                                                        Brush.verticalGradient(
                                                            colors = listOf(
                                                                AppColor.Primary,
                                                                AppColor.Secondary
                                                            )
                                                        )
                                                    } else {
                                                        Brush.verticalGradient(
                                                            colors = listOf(
                                                                MaterialTheme.colorScheme.surfaceVariant.copy(
                                                                    alpha = 0.4f
                                                                ),
                                                                MaterialTheme.colorScheme.surfaceVariant.copy(
                                                                    alpha = 0.4f
                                                                )
                                                            )
                                                        )
                                                    }
                                                )
                                                .clickable { chosenMonth = monthNum }
                                                .padding(vertical = AppDimen.p12),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AppText(
                                                text = monthName,
                                                fontSize = 14.sp,
                                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(AppDimen.p24))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = onDismiss) {
                                AppText(
                                    text = "Hủy",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.width(AppDimen.p8))

                            Button(
                                onClick = { onConfirm(chosenYear, chosenMonth) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                AppText(
                                    text = "Xác nhận",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
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
    var newMonth = date.monthNumber + offset
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
    val targetDay = date.dayOfMonth.coerceAtMost(lastDay)
    return LocalDate(newYear, newMonth, targetDay)
}
