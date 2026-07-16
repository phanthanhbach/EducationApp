package com.example.educationapp.presentation.screen.assignment.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.badge.AppBadge
import com.example.educationapp.core.ui.button.AppButton
import com.example.educationapp.core.ui.button.AppTextButton
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.StudentAssignment
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.assignment_brief_btn
import educationapp.shared.generated.resources.assignment_due
import educationapp.shared.generated.resources.assignment_final_exam_badge
import educationapp.shared.generated.resources.assignment_not_submitted
import educationapp.shared.generated.resources.assignment_overdue
import educationapp.shared.generated.resources.assignment_score
import educationapp.shared.generated.resources.assignment_submit_btn
import educationapp.shared.generated.resources.assignment_submitted
import educationapp.shared.generated.resources.assignment_submitting_btn
import educationapp.shared.generated.resources.assignment_view_submission_btn
import educationapp.shared.generated.resources.ic_check_circle_filled_24dp
import educationapp.shared.generated.resources.ic_docs_24dp
import educationapp.shared.generated.resources.ic_error_outline_24dp
import educationapp.shared.generated.resources.ic_open_in_new_24dp
import educationapp.shared.generated.resources.ic_upload_24dp
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun StudentAssignmentCard(
    assignment: StudentAssignment,
    isSubmitting: Boolean,
    onSubmitAssignmentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val isOverdue = remember(assignment.dueDate) {
        try {
            val dueInstant = Instant.parse(assignment.dueDate)
            dueInstant < Clock.System.now()
        } catch (e: Exception) {
            false
        }
    }

    val isWarning = isOverdue && !assignment.submitted

    // Premium styling colors matching mockup and theme
    val borderStrokeColor = if (isWarning) {
        Color(0xFFFFCDD2) // Light red border
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    }

    val containerColor = if (isWarning) {
        Color(0xFFFFEBEE).copy(alpha = 0.6f) // Very light red background
    } else {
        MaterialTheme.colorScheme.surface
    }

    val iconBgColor = when {
        isWarning -> Color(0xFFFFEBEE)
        assignment.submitted -> Color(0xFFE8F5E9)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val iconColor = when {
        isWarning -> Color(0xFFD32F2F)
        assignment.submitted -> Color(0xFF388E3C)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val iconRes = when {
        isWarning -> Res.drawable.ic_error_outline_24dp
        assignment.submitted -> Res.drawable.ic_check_circle_filled_24dp
        else -> Res.drawable.ic_docs_24dp
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderStrokeColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimen.p16),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon status indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(
                    drawableRes = iconRes,
                    tint = iconColor,
                    iconModifier = Modifier.size(24.dp)
                )
            }

            // Description column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppText(
                    text = assignment.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!assignment.description.isNullOrBlank()) {
                    AppText(
                        text = assignment.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Due date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AppText(
                        text = stringResource(
                            Res.string.assignment_due,
                            formatAssignmentDate(assignment.dueDate)
                        ),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isWarning) {
                        AppText(
                            text = stringResource(Res.string.assignment_overdue),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                    }
                }

                // Badges Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status Badge
                    AppBadge(
                        text = if (assignment.submitted) {
                            stringResource(Res.string.assignment_submitted)
                        } else {
                            stringResource(Res.string.assignment_not_submitted)
                        },
                        color = if (assignment.submitted) AppColor.Success else AppColor.Warning
                    )

                    // Final Exam Badge
                    if (assignment.finalExam) {
                        AppBadge(
                            text = stringResource(Res.string.assignment_final_exam_badge),
                            color = Color(0xFF7E57C2) // Purple color
                        )
                    }

                    // Score Badge
                    if (assignment.score != null) {
                        AppBadge(
                            text = stringResource(
                                Res.string.assignment_score,
                                assignment.score.toString()
                            ),
                            color = AppColor.Primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Actions row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Assignment brief Link
                    AppTextButton(
                        text = stringResource(Res.string.assignment_brief_btn),
                        onClick = {
                            if (!assignment.assignmentFileAttachment.isNullOrBlank()) {
                                try {
                                    val url =
                                        if (assignment.assignmentFileAttachment.startsWith("http://") ||
                                            assignment.assignmentFileAttachment.startsWith("https://")
                                        ) {
                                            assignment.assignmentFileAttachment
                                        } else {
                                            "http://${assignment.assignmentFileAttachment}"
                                        }
                                    uriHandler.openUri(url)
                                } catch (e: Exception) {
                                    // Swallow error silently
                                }
                            }
                        },
                        leadingIcon = {
                            AppIcon(
                                drawableRes = Res.drawable.ic_open_in_new_24dp,
                                tint = MaterialTheme.colorScheme.primary,
                                iconModifier = Modifier.size(16.dp)
                            )
                        }
                    )

                    // Submit or View Submission button
                    if (!assignment.submitted) {
                        AppButton(
                            text = if (isSubmitting) {
                                stringResource(Res.string.assignment_submitting_btn)
                            } else {
                                stringResource(Res.string.assignment_submit_btn)
                            },
                            onClick = onSubmitAssignmentClick,
                            enabled = !isSubmitting,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            leadingIcon = {
                                AppIcon(
                                    drawableRes = Res.drawable.ic_upload_24dp,
                                    tint = Color.White,
                                    iconModifier = Modifier.size(16.dp)
                                )
                            },
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .height(38.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFE65100), Color(0xFFC2185B))
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )
                    } else if (!assignment.fileAttachment.isNullOrBlank()) {
                        AppTextButton(
                            text = stringResource(Res.string.assignment_view_submission_btn),
                            onClick = {
                                try {
                                    val url = if (assignment.fileAttachment.startsWith("http://") ||
                                        assignment.fileAttachment.startsWith("https://")
                                    ) {
                                        assignment.fileAttachment
                                    } else {
                                        "http://${assignment.fileAttachment}"
                                    }
                                    uriHandler.openUri(url)
                                } catch (_: Exception) {
                                    // Swallow silently
                                }
                            },
                            leadingIcon = {
                                AppIcon(
                                    drawableRes = Res.drawable.ic_open_in_new_24dp,
                                    tint = MaterialTheme.colorScheme.primary,
                                    iconModifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}


private fun formatAssignmentDate(dateStr: String): String {
    if (dateStr.isBlank()) return "--/--/----"
    return try {
        val parts = dateStr.split('T')
        val datePart = parts[0]
        val datePieces = datePart.split('-')
        val formattedDate = if (datePieces.size == 3) {
            "${datePieces[2]}/${datePieces[1]}/${datePieces[0]}"
        } else {
            datePart
        }

        val timePart = parts.getOrNull(1)?.take(5)
        if (!timePart.isNullOrBlank()) "$formattedDate $timePart" else formattedDate
    } catch (_: Exception) {
        dateStr
    }
}
