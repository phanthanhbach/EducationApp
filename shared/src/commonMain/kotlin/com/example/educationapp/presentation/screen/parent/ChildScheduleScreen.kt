package com.example.educationapp.presentation.screen.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Row
import com.example.educationapp.core.ui.shimmer.ShimmerBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.educationapp.presentation.screen.parent.composable.ClassChipsRow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.util.UiText
import com.example.educationapp.core.util.CalendarHelper
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screen.schedule.CommonScheduleMobileLayout
import com.example.educationapp.presentation.screen.schedule.CommonScheduleTabletLayout
import com.example.educationapp.presentation.screenmodel.parent.ChildClassesState
import com.example.educationapp.presentation.screenmodel.parent.ChildScheduleScreenModel
import org.koin.core.parameter.parametersOf

class ChildScheduleScreen(
    private val studentId: Long,
    private val studentName: String
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ChildScheduleScreenModel> { parametersOf(studentId) }

        val classesState by screenModel.classesState.collectAsState()
        val selectedClass by screenModel.selectedClass.collectAsState()
        val selectedDate by screenModel.selectedDate.collectAsState()
        val isMonthExpanded by screenModel.isMonthExpanded.collectAsState()
        val schedulesState by screenModel.schedulesState.collectAsState()
        val filteredSchedules by screenModel.filteredSchedules.collectAsState()
        val highlightDates by screenModel.highlightDates.collectAsState()

        val today = remember { CalendarHelper.getCurrentDate() }

        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Lịch học của $studentName",
                    onBackClick = { navigator.pop() },
                    containerColor = MaterialTheme.colorScheme.surface,
                    isTitleCentered = false
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Class Selector (Horizontal Chips)
                when (val state = classesState) {
                    is ChildClassesState.Loading -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(3) {
                                ShimmerBox(
                                    width = 80.dp,
                                    height = 32.dp,
                                    shape = RoundedCornerShape(16.dp)
                                )
                            }
                        }
                    }

                    is ChildClassesState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AppText(
                                text = state.message.asString(),
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                        }
                    }

                    is ChildClassesState.Success -> {
                        if (state.classes.isNotEmpty()) {
                            ClassChipsRow(
                                classes = state.classes,
                                selectedClass = selectedClass,
                                onClassSelected = { screenModel.selectClass(it) }
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AppText(
                                    text = "Con chưa tham gia lớp học nào.",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Divider between selector and calendar
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    thickness = 1.dp
                )

                // Calendar + Schedules layout (Responsive: mobile / tablet)
                if (selectedClass != null) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        val isTablet = maxWidth >= 600.dp

                        if (isTablet) {
                            CommonScheduleTabletLayout(
                                role = AppRole.STUDENT,
                                selectedDate = selectedDate,
                                isMonthExpanded = isMonthExpanded,
                                schedulesState = schedulesState,
                                filteredSchedules = filteredSchedules,
                                highlightDates = highlightDates,
                                today = today,
                                onDateSelected = { screenModel.selectedDate.value = it },
                                onToggleExpand = {
                                    screenModel.isMonthExpanded.value = !isMonthExpanded
                                },
                                onSessionClick = null,
                                onRetry = { screenModel.retryLoadSchedules() }
                            )
                        } else {
                            CommonScheduleMobileLayout(
                                role = AppRole.STUDENT,
                                selectedDate = selectedDate,
                                isMonthExpanded = isMonthExpanded,
                                schedulesState = schedulesState,
                                filteredSchedules = filteredSchedules,
                                highlightDates = highlightDates,
                                today = today,
                                onDateSelected = { screenModel.selectedDate.value = it },
                                onToggleExpand = {
                                    screenModel.isMonthExpanded.value = !isMonthExpanded
                                },
                                onSessionClick = null,
                                onRetry = { screenModel.retryLoadSchedules() }
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        AppText(
                            text = "Vui lòng chọn lớp học để xem lịch.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
