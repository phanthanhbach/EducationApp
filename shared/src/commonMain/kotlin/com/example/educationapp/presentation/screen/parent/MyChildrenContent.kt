package com.example.educationapp.presentation.screen.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.ui.avatar.AppAvatar
import com.example.educationapp.core.ui.layout.LocalTopBarHazeState
import com.example.educationapp.core.ui.modifier.GlassBox
import com.example.educationapp.core.ui.row.OptionRow
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.StudentStatus
import com.example.educationapp.presentation.screen.main.LocalIsTablet
import com.example.educationapp.presentation.screen.parent.child_attendance.ChildAttendanceRateScreen
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.gender_female
import educationapp.shared.generated.resources.gender_male
import educationapp.shared.generated.resources.ic_calendar_month_filled_24dp
import educationapp.shared.generated.resources.ic_event_24dp
import educationapp.shared.generated.resources.parent_child_attendance_desc
import educationapp.shared.generated.resources.parent_child_attendance_title
import educationapp.shared.generated.resources.parent_child_schedule_desc
import educationapp.shared.generated.resources.parent_child_schedule_title
import educationapp.shared.generated.resources.parent_learning_utilities
import educationapp.shared.generated.resources.parent_student_code_format
import educationapp.shared.generated.resources.parent_student_code_id_format
import educationapp.shared.generated.resources.profile_address
import educationapp.shared.generated.resources.profile_current_level
import educationapp.shared.generated.resources.profile_dob
import educationapp.shared.generated.resources.profile_gender
import educationapp.shared.generated.resources.profile_not_available
import educationapp.shared.generated.resources.profile_status
import educationapp.shared.generated.resources.student_status_active
import educationapp.shared.generated.resources.student_status_graduated
import educationapp.shared.generated.resources.student_status_inactive
import educationapp.shared.generated.resources.student_status_suspended
import org.jetbrains.compose.resources.stringResource


@Composable
fun ChildDetailCard(
    child: UserProfile.Student,
    modifier: Modifier = Modifier
) {
    val isTablet = LocalIsTablet.current
    val horizontalPadding = if (isTablet) 24.dp else 16.dp

    // Child Info Card with actions inside, styled with premium GlassBox
    GlassBox(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
        hazeState = LocalTopBarHazeState.current,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Info section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppAvatar(
                        name = child.fullName,
                        imageUrl = child.img,
                        modifier = Modifier
                            .size(72.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                shape = CircleShape
                            ),
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AppText(
                            text = child.fullName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        val studentCodeText = if (!child.studentCode.isNullOrBlank()) {
                            stringResource(Res.string.parent_student_code_format, child.studentCode)
                        } else {
                            stringResource(Res.string.parent_student_code_id_format, child.studentId.toString())
                        }
                        AppText(
                            text = studentCodeText,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Status and Level DetailRows above the divider
                val studentStatus = StudentStatus.fromString(child.status)
                val statusBgColor = when (studentStatus) {
                    StudentStatus.ACTIVE -> Color(0xFFE8F5E9)
                    StudentStatus.INACTIVE -> Color(0xFFECEFF1)
                    StudentStatus.SUSPENDED -> Color(0xFFFFF3E0)
                    StudentStatus.GRADUATED -> Color(0xFFE3F2FD)
                }
                val statusTextColor = when (studentStatus) {
                    StudentStatus.ACTIVE -> Color(0xFF2E7D32)
                    StudentStatus.INACTIVE -> Color(0xFF546E7A)
                    StudentStatus.SUSPENDED -> Color(0xFFE65100)
                    StudentStatus.GRADUATED -> Color(0xFF1565C0)
                }
                val statusText = when (studentStatus) {
                    StudentStatus.ACTIVE -> stringResource(Res.string.student_status_active)
                    StudentStatus.INACTIVE -> stringResource(Res.string.student_status_inactive)
                    StudentStatus.SUSPENDED -> stringResource(Res.string.student_status_suspended)
                    StudentStatus.GRADUATED -> stringResource(Res.string.student_status_graduated)
                }

                DetailRow(
                    label = stringResource(Res.string.profile_status),
                    valueContent = {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(statusBgColor)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            AppText(
                                text = statusText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusTextColor
                            )
                        }
                    }
                )

                if (!child.currentLevel.isNullOrBlank()) {
                    DetailRow(
                        label = stringResource(Res.string.profile_current_level),
                        value = child.currentLevel
                    )
                }

                // Divider
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    thickness = 1.dp
                )

                // Details section
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val notAvailableText = stringResource(Res.string.profile_not_available)
                    val genderText = when (child.gender?.lowercase()) {
                        "male" -> stringResource(Res.string.gender_male)
                        "female" -> stringResource(Res.string.gender_female)
                        else -> child.gender ?: notAvailableText
                    }
                    DetailRow(label = stringResource(Res.string.profile_dob), value = child.dateOfBirth ?: notAvailableText)
                    DetailRow(label = stringResource(Res.string.profile_gender), value = genderText)
                    DetailRow(label = stringResource(Res.string.profile_address), value = child.address ?: notAvailableText)
                }
            }

            // Learning Actions Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                AppText(
                    text = stringResource(Res.string.parent_learning_utilities),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                // Action: Schedule
                val parentNavigator = LocalNavigator.currentOrThrow.parent
                OptionRow(
                    title = stringResource(Res.string.parent_child_schedule_title),
                    description = stringResource(Res.string.parent_child_schedule_desc),
                    iconRes = Res.drawable.ic_calendar_month_filled_24dp,
                    iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                    iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = {
                        parentNavigator?.push(
                            ChildScheduleScreen(
                                studentId = child.studentId.toLong(),
                                studentName = child.fullName
                            )
                        )
                    }
                )

                // Action: Attendance Rate
                OptionRow(
                    title = stringResource(Res.string.parent_child_attendance_title),
                    description = stringResource(Res.string.parent_child_attendance_desc),
                    iconRes = Res.drawable.ic_event_24dp,
                    iconBgColor = MaterialTheme.colorScheme.secondaryContainer,
                    iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                    onClick = {
                        parentNavigator?.push(
                            ChildAttendanceRateScreen(
                                studentId = child.studentId.toLong(),
                                studentName = child.fullName
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    modifier: Modifier = Modifier,
    valueContent: @Composable () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppText(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier.weight(2f),
            contentAlignment = Alignment.CenterEnd
        ) {
            valueContent()
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    DetailRow(
        label = label,
        modifier = modifier
    ) {
        AppText(
            text = value,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
        )
    }
}
