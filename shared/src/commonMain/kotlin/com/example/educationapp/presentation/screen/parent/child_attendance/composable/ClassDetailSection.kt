package com.example.educationapp.presentation.screen.parent.child_attendance.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.AttendanceRate
import com.example.educationapp.domain.entity.SchoolClass
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.parent_attendance_class_course_format
import educationapp.shared.generated.resources.parent_attendance_class_no_data
import educationapp.shared.generated.resources.parent_attendance_class_teacher_format
import educationapp.shared.generated.resources.parent_attendance_detail_absent
import educationapp.shared.generated.resources.parent_attendance_detail_absent_sessions_format
import educationapp.shared.generated.resources.parent_attendance_detail_attended
import educationapp.shared.generated.resources.parent_attendance_detail_attended_sessions_format
import educationapp.shared.generated.resources.parent_attendance_detail_total_sessions_format
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ClassDetailSection(
    schoolClass: SchoolClass,
    rate: AttendanceRate?,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(AppDimen.p20),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            AppDimen.p1,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimen.p20),
            verticalArrangement = Arrangement.spacedBy(AppDimen.p16)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AppDimen.p4)) {
                AppText(
                    text = schoolClass.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                AppText(
                    text = stringResource(
                        Res.string.parent_attendance_class_course_format,
                        schoolClass.courseName
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AppText(
                    text = stringResource(
                        Res.string.parent_attendance_class_teacher_format,
                        schoolClass.teacherName
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            if (rate != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(AppDimen.p8)
                    ) {
                        AppText(
                            text = stringResource(
                                Res.string.parent_attendance_detail_total_sessions_format,
                                rate.totalSessions
                            ),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppDimen.p8)
                        ) {
                            AppText(
                                text = stringResource(Res.string.parent_attendance_detail_attended),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            AppText(
                                text = stringResource(
                                    Res.string.parent_attendance_detail_attended_sessions_format,
                                    rate.attendedSessions
                                ),
                                style = MaterialTheme.typography.titleSmall,
                                color = Color(0xFF4CAF50)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppDimen.p8)
                        ) {
                            AppText(
                                text = stringResource(Res.string.parent_attendance_detail_absent),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            AppText(
                                text = stringResource(
                                    Res.string.parent_attendance_detail_absent_sessions_format,
                                    rate.totalSessions - rate.attendedSessions
                                ),
                                style = MaterialTheme.typography.titleSmall,
                                color = Color(0xFFF44336)
                            )
                        }
                    }

                    AttendanceProgressRing(
                        rate = rate.attendanceRate,
                        modifier = Modifier.padding(start = AppDimen.p16)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppDimen.p100),
                    contentAlignment = Alignment.Center
                ) {
                    AppText(
                        text = stringResource(Res.string.parent_attendance_class_no_data),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
