package com.example.educationapp.presentation.screen.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.button.AppTextButton
import com.example.educationapp.core.ui.error.ErrorStateView
import com.example.educationapp.core.ui.shimmer.skeleton.DashboardSkeleton
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.ui.toast.LocalToastController
import com.example.educationapp.core.util.CalendarHelper
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screen.course.MyCoursesScreen
import com.example.educationapp.presentation.screen.dashboard.composable.AssignmentDeadlineSection
import com.example.educationapp.presentation.screen.dashboard.composable.AttendanceByClassSection
import com.example.educationapp.presentation.screen.dashboard.composable.CurrentCoursesSection
import com.example.educationapp.presentation.screen.dashboard.composable.SectionHeader
import com.example.educationapp.presentation.screen.dashboard.composable.TeacherContactSection
import com.example.educationapp.presentation.screen.dashboard.composable.UpcomingSchedulesSection
import com.example.educationapp.presentation.screen.session.SessionDetailScreen
import com.example.educationapp.presentation.screenmodel.dashboard.StudentDashboardScreenModel
import com.example.educationapp.presentation.screenmodel.dashboard.StudentDashboardState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.dashboard_assignments_empty
import educationapp.shared.generated.resources.dashboard_attendance_empty
import educationapp.shared.generated.resources.dashboard_attendance_title
import educationapp.shared.generated.resources.dashboard_btn_view_all
import educationapp.shared.generated.resources.dashboard_courses_empty
import educationapp.shared.generated.resources.dashboard_courses_title
import educationapp.shared.generated.resources.dashboard_teacher_contact_empty
import educationapp.shared.generated.resources.dashboard_teacher_contact_title
import educationapp.shared.generated.resources.lb_dashboard_assignments
import educationapp.shared.generated.resources.lb_dashboard_schedules
import org.jetbrains.compose.resources.stringResource

@Composable
fun StudentDashboardContent(
    screenModel: StudentDashboardScreenModel,
    onViewScheduleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val parentNavigator = LocalNavigator.currentOrThrow.parent
    val state by screenModel.state.collectAsState()

    val toastController = LocalToastController.current

    Box(modifier = modifier.fillMaxWidth()) {
        when (val currentState = state) {
            is StudentDashboardState.Loading -> {
                DashboardSkeleton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimen.p16)
                )
            }

            is StudentDashboardState.Error -> {
                ErrorStateView(
                    modifier = Modifier.padding(AppDimen.p16),
                    error = currentState.error,
                    onRetry = { screenModel.loadDashboardData() }
                )
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
                                onShowToast = { toastController.show(it) }
                            )
                        }
                    }

                    // 5. Current Courses
                    Column(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            .padding(AppDimen.p16),
                        verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
                    ) {
                        SectionHeader(title = stringResource(Res.string.dashboard_courses_title))

                        if (currentState.currentCourses.isEmpty()) {
                            EmptySectionCard(message = stringResource(Res.string.dashboard_courses_empty))
                        } else {
                            CurrentCoursesSection(courses = currentState.currentCourses)
                            AppTextButton(
                                text = stringResource(Res.string.dashboard_btn_view_all),
                                onClick = { parentNavigator?.push(MyCoursesScreen()) },
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                trailingIcon = {
                                    AppText(
                                        text = "→",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
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
