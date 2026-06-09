package com.example.educationapp.presentation.screen.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.util.CalendarHelper
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screen.dashboard.composable.AssignmentDeadlineSection
import com.example.educationapp.presentation.screen.dashboard.composable.AttendanceByClassSection
import com.example.educationapp.presentation.screen.dashboard.composable.CurrentCoursesSection
import com.example.educationapp.presentation.screen.dashboard.composable.TeacherContactSection
import com.example.educationapp.presentation.screen.dashboard.composable.UpcomingSchedulesSection
import com.example.educationapp.presentation.screen.schedule.SessionDetailScreen
import com.example.educationapp.presentation.screenmodel.dashboard.StudentDashboardScreenModel
import com.example.educationapp.presentation.screenmodel.dashboard.StudentDashboardState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.dashboard_assignments_empty
import educationapp.shared.generated.resources.dashboard_attendance_empty
import educationapp.shared.generated.resources.dashboard_attendance_title
import educationapp.shared.generated.resources.dashboard_btn_retry
import educationapp.shared.generated.resources.dashboard_courses_empty
import educationapp.shared.generated.resources.dashboard_courses_title
import educationapp.shared.generated.resources.dashboard_teacher_contact_empty
import educationapp.shared.generated.resources.dashboard_teacher_contact_title
import educationapp.shared.generated.resources.lb_dashboard_assignments
import educationapp.shared.generated.resources.lb_dashboard_schedules
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun StudentDashboardContent(
    screenModel: StudentDashboardScreenModel,
    onViewScheduleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val parentNavigator = LocalNavigator.currentOrThrow.parent
    val state by screenModel.state.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    var toastMessage by remember { mutableStateOf<String?>(null) }

    fun showToast(message: String) {
        coroutineScope.launch {
            toastMessage = message
            delay(2000.milliseconds)
            if (toastMessage == message) {
                toastMessage = null
            }
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        when (val currentState = state) {
            is StudentDashboardState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColor.Primary)
                }
            }

            is StudentDashboardState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(
                            alpha = 0.1f
                        )
                    ),
                    border = BorderStroke(1.dp, AppColor.Error.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(AppDimen.p16),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AppText(
                            text = currentState.message,
                            fontSize = 14.sp,
                            color = AppColor.Error,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { screenModel.loadDashboardData() },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColor.Primary)
                        ) {
                            AppText(
                                text = stringResource(Res.string.dashboard_btn_retry),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            is StudentDashboardState.Success -> {
                val today = remember { CalendarHelper.getCurrentDate() }

                Column(verticalArrangement = Arrangement.spacedBy(AppDimen.p4)) {

                    // 1. Upcoming Schedules
                    Column(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = AppDimen.p16, vertical = AppDimen.p16),
                        verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
                    ) {
                        SectionHeader(title = stringResource(Res.string.lb_dashboard_schedules))

                        UpcomingSchedulesSection(
                            role = AppRole.STUDENT,
                            schedules = currentState.upcomingSchedules,
                            today = today,
                            onScheduleClick = { schedule ->
                                parentNavigator?.push(SessionDetailScreen(schedule))
                            },
                            onViewScheduleClick = onViewScheduleClick
                        )
                    }

                    // 2. Assignment Deadlines (Due within 48 hours)
                    Column(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = AppDimen.p16, vertical = AppDimen.p16),
                        verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
                    ) {
                        SectionHeader(title = stringResource(Res.string.lb_dashboard_assignments))

                        if (currentState.assignmentReminders.isEmpty()) {
                            EmptySectionCard(message = stringResource(Res.string.dashboard_assignments_empty))
                        } else {
                            AssignmentDeadlineSection(reminders = currentState.assignmentReminders)
                        }
                    }

                    // 3. Attendance by Class
                    Column(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = AppDimen.p16, vertical = AppDimen.p16),
                        verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
                    ) {
                        SectionHeader(title = stringResource(Res.string.dashboard_attendance_title))

                        if (currentState.attendanceByClass.isEmpty()) {
                            EmptySectionCard(message = stringResource(Res.string.dashboard_attendance_empty))
                        } else {
                            AttendanceByClassSection(attendanceList = currentState.attendanceByClass)
                        }
                    }

                    // 4. Teacher Contact
                    Column(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = AppDimen.p16, vertical = AppDimen.p16),
                        verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
                    ) {
                        SectionHeader(title = stringResource(Res.string.dashboard_teacher_contact_title))

                        if (currentState.teacherContacts.isEmpty()) {
                            EmptySectionCard(message = stringResource(Res.string.dashboard_teacher_contact_empty))
                        } else {
                            TeacherContactSection(
                                contacts = currentState.teacherContacts,
                                onShowToast = { showToast(it) }
                            )
                        }
                    }

                    // 5. Current Courses
                    Column(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = AppDimen.p16, vertical = AppDimen.p16),
                        verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
                    ) {
                        SectionHeader(title = stringResource(Res.string.dashboard_courses_title))

                        if (currentState.currentCourses.isEmpty()) {
                            EmptySectionCard(message = stringResource(Res.string.dashboard_courses_empty))
                        } else {
                            CurrentCoursesSection(courses = currentState.currentCourses)
                        }
                    }
                }
            }
        }

        // Beautiful floating Toast
        AnimatedVisibility(
            visible = toastMessage != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            toastMessage?.let { msg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF323232)),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    AppText(
                        text = msg,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    AppText(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

@Composable
private fun EmptySectionCard(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.3f
            )
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimen.p20),
            contentAlignment = Alignment.Center
        ) {
            AppText(
                text = message,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
