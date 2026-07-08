package com.example.educationapp.presentation.screen.my_classes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.button.AppButton
import com.example.educationapp.core.ui.button.AppOutlinedButton
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.SchoolClass
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_assignment_filled_24dp
import educationapp.shared.generated.resources.ic_book_24dp
import educationapp.shared.generated.resources.ic_event_24dp
import educationapp.shared.generated.resources.ic_location_on_24dp
import educationapp.shared.generated.resources.my_classes_branch
import educationapp.shared.generated.resources.my_classes_btn_assignment
import educationapp.shared.generated.resources.my_classes_btn_feedback
import educationapp.shared.generated.resources.my_classes_btn_invoice
import educationapp.shared.generated.resources.my_classes_course
import educationapp.shared.generated.resources.my_classes_enrolled
import educationapp.shared.generated.resources.my_classes_result
import educationapp.shared.generated.resources.my_classes_student_count
import educationapp.shared.generated.resources.my_classes_students_count
import educationapp.shared.generated.resources.my_classes_time
import org.jetbrains.compose.resources.stringResource

@Composable
fun ClassCard(
    schoolClass: SchoolClass,
    statusText: String,
    statusColor: Color,
    onAssignmentsClick: (() -> Unit)? = null,
    onFeedbacksClick: (() -> Unit)? = null,
    onInvoiceClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val formattedStartDate = formatDate(schoolClass.startDate)
    val formattedEndDate = formatDate(schoolClass.endDate)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = statusColor.copy(alpha = 0.12f),
                spotColor = statusColor.copy(alpha = 0.18f)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimen.p16),
            verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(
                    text = schoolClass.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    AppText(
                        text = statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppIcon(
                        drawableRes = Res.drawable.ic_book_24dp,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        iconModifier = Modifier.size(16.dp)
                    )
                    AppText(
                        text = stringResource(Res.string.my_classes_course, schoolClass.courseName),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (schoolClass.branchName.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppIcon(
                            drawableRes = Res.drawable.ic_location_on_24dp,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            iconModifier = Modifier.size(16.dp)
                        )
                        AppText(
                            text = stringResource(
                                Res.string.my_classes_branch,
                                schoolClass.branchName
                            ),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (schoolClass.endDate.isBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppIcon(
                            drawableRes = Res.drawable.ic_event_24dp,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            iconModifier = Modifier.size(16.dp)
                        )
                        AppText(
                            text = stringResource(
                                Res.string.my_classes_enrolled,
                                formattedStartDate
                            ),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppIcon(
                            drawableRes = Res.drawable.ic_event_24dp,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            iconModifier = Modifier.size(16.dp)
                        )
                        AppText(
                            text = stringResource(
                                Res.string.my_classes_time,
                                formattedStartDate,
                                formattedEndDate
                            ),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (!schoolClass.finalResult.isNullOrBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppIcon(
                            drawableRes = Res.drawable.ic_assignment_filled_24dp,
                            tint = AppColor.Primary,
                            iconModifier = Modifier.size(16.dp)
                        )
                        AppText(
                            text = stringResource(
                                Res.string.my_classes_result,
                                schoolClass.finalResult
                            ),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColor.Primary
                        )
                    }
                }
            }

            if (schoolClass.maxStudents > 0) {
                val ratio =
                    schoolClass.currentStudents.toFloat() / schoolClass.maxStudents.toFloat()

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppText(
                            text = stringResource(Res.string.my_classes_student_count),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        AppText(
                            text = stringResource(
                                Res.string.my_classes_students_count,
                                schoolClass.currentStudents,
                                schoolClass.maxStudents
                            ),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    LinearProgressIndicator(
                        progress = { ratio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = AppColor.Primary,
                        trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                )
            }

            if (onInvoiceClick != null) {
                AppButton(
                    text = stringResource(Res.string.my_classes_btn_invoice),
                    onClick = onInvoiceClick,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                )
            } else if (onFeedbacksClick != null && onAssignmentsClick != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppOutlinedButton(
                        text = stringResource(Res.string.my_classes_btn_feedback),
                        onClick = onFeedbacksClick,
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    )

                    AppButton(
                        text = stringResource(Res.string.my_classes_btn_assignment),
                        onClick = onAssignmentsClick,
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    )
                }
            } else if (onFeedbacksClick != null) {
                AppButton(
                    text = stringResource(Res.string.my_classes_btn_feedback),
                    onClick = onFeedbacksClick,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                )
            } else {
                AppButton(
                    text = stringResource(Res.string.my_classes_btn_assignment),
                    onClick = onAssignmentsClick ?: {},
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                )
            }
        }
    }
}

private fun formatDate(dateStr: String?): String {
    if (dateStr.isNullOrBlank()) return "--/--/----"
    return try {
        val datePart = if (dateStr.contains('T')) {
            dateStr.split('T')[0]
        } else {
            dateStr
        }
        val parts = datePart.split('-')
        if (parts.size == 3) {
            val year = parts[0]
            val month = parts[1]
            val day = parts[2]
            "$day/$month/$year"
        } else {
            dateStr
        }
    } catch (_: Exception) {
        dateStr
    }
}
