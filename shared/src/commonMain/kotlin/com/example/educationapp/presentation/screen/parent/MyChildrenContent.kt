package com.example.educationapp.presentation.screen.parent

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.avatar.AppAvatar
import com.example.educationapp.core.ui.row.OptionRow
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.StudentStatus
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_calendar_month_filled_24dp
import educationapp.shared.generated.resources.ic_event_24dp

@Composable
fun ChildDetailCard(
    child: UserProfile.Student,
    modifier: Modifier = Modifier
) {
    // Child Info Card with actions inside
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimen.p16)
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
                            "Mã học sinh: ${child.studentCode}"
                        } else {
                            "Mã học sinh: #${child.studentId}"
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
                    StudentStatus.ACTIVE -> "Đang học (Active)"
                    StudentStatus.INACTIVE -> "Nghỉ học (Inactive)"
                    StudentStatus.SUSPENDED -> "Bảo lưu (Suspended)"
                    StudentStatus.GRADUATED -> "Tốt nghiệp (Graduated)"
                }

                DetailRow(
                    label = "Trạng thái",
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
                        label = "Trình độ hiện tại",
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
                    DetailRow(label = "Ngày sinh", value = child.dateOfBirth ?: "N/A")
                    DetailRow(label = "Giới tính", value = child.gender ?: "N/A")
                    DetailRow(label = "Địa chỉ", value = child.address ?: "N/A")
                }
            }

            // Learning Actions Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                AppText(
                    text = "Tiện ích học tập",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                // Action: Schedule
                val parentNavigator = LocalNavigator.currentOrThrow.parent
                OptionRow(
                    title = "Lịch học của con",
                    description = "Xem lịch học chi tiết các ngày trong tuần",
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
                    title = "Tỉ lệ tham gia buổi học",
                    description = "Theo dõi chuyên cần và số buổi nghỉ học",
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
