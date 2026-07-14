package com.example.educationapp.presentation.screen.parent.child_attendance.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.parent_attendance_summary_absent_format
import educationapp.shared.generated.resources.parent_attendance_summary_attended_format
import educationapp.shared.generated.resources.parent_attendance_summary_title
import educationapp.shared.generated.resources.parent_attendance_summary_total_format
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SummaryCard(
    total: Int,
    attended: Int,
    absent: Int,
    rate: Double,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)
        )
    )

    Card(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(AppDimen.p24),
        border = BorderStroke(
            AppDimen.p1,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(gradientBrush)
                .padding(AppDimen.p20)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AppDimen.p10)
                ) {
                    AppText(
                        text = stringResource(Res.string.parent_attendance_summary_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(AppDimen.p6)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppDimen.p8)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(AppDimen.p8)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            AppText(
                                text = stringResource(
                                    Res.string.parent_attendance_summary_total_format,
                                    total
                                ),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppDimen.p8)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(AppDimen.p8)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50))
                            )
                            AppText(
                                text = stringResource(
                                    Res.string.parent_attendance_summary_attended_format,
                                    attended
                                ),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppDimen.p8)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(AppDimen.p8)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF44336))
                            )
                            AppText(
                                text = stringResource(
                                    Res.string.parent_attendance_summary_absent_format,
                                    absent
                                ),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                AttendanceProgressRing(
                    rate = rate,
                    modifier = Modifier.padding(start = AppDimen.p16)
                )
            }
        }
    }
}
