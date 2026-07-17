package com.example.educationapp.presentation.screen.assignment.composable

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.badge.AppBadge
import com.example.educationapp.core.ui.button.AppTextButton
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.SubmissionDetail
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_open_in_new_24dp
import educationapp.shared.generated.resources.submission_grade_btn
import educationapp.shared.generated.resources.submission_score
import educationapp.shared.generated.resources.submission_status_graded
import educationapp.shared.generated.resources.submission_status_not_submitted
import educationapp.shared.generated.resources.submission_status_submitted
import educationapp.shared.generated.resources.submission_submitted_at
import educationapp.shared.generated.resources.submission_teacher_comment_label
import educationapp.shared.generated.resources.submission_view_file
import org.jetbrains.compose.resources.stringResource

@Composable
fun SubmissionCard(
    submission: SubmissionDetail,
    onGradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isGraded = submission.submitted && submission.score != null
    val isSubmittedOnly = submission.submitted && submission.score == null
    val uriHandler = LocalUriHandler.current

    val avatarBgColor = when {
        isGraded -> AppColor.Success.copy(alpha = 0.12f)
        isSubmittedOnly -> AppColor.Primary.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val avatarTextColor = when {
        isGraded -> AppColor.Success
        isSubmittedOnly -> AppColor.Primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppDimen.p12),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            AppDimen.p1,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimen.p1)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Top section: Avatar and Student Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = AppDimen.p16,
                        end = AppDimen.p16,
                        top = AppDimen.p16,
                        bottom = AppDimen.p12
                    ),
                horizontalArrangement = Arrangement.spacedBy(AppDimen.p16),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Student Initials Avatar
                Box(
                    modifier = Modifier
                        .size(AppDimen.p40)
                        .clip(CircleShape)
                        .background(avatarBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    AppText(
                        text = getInitials(submission.studentName),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = avatarTextColor
                    )
                }

                // Middle Info Column (Name, Attachment, and Submission Time)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AppDimen.p4)
                ) {
                    AppText(
                        text = submission.studentName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Attachment
                    if (!submission.fileAttachment.isNullOrBlank()) {
                        AppTextButton(
                            text = stringResource(Res.string.submission_view_file),
                            onClick = {
                                try {
                                    val url = if (submission.fileAttachment.startsWith("http://") ||
                                        submission.fileAttachment.startsWith("https://")
                                    ) {
                                        submission.fileAttachment
                                    } else {
                                        "http://${submission.fileAttachment}"
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

                    // Submitted Date
                    if (!submission.submittedAt.isNullOrBlank()) {
                        AppText(
                            text = stringResource(
                                Res.string.submission_submitted_at,
                                formatSubmissionDate(submission.submittedAt)
                            ),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Teacher Comment Section (shown only if isGraded and comment exists)
            if (isGraded && !submission.teacherComment.isNullOrBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppDimen.p16)
                        .padding(bottom = AppDimen.p12),
                    verticalArrangement = Arrangement.spacedBy(AppDimen.p4)
                ) {
                    AppText(
                        text = stringResource(
                            Res.string.submission_teacher_comment_label,
                            submission.teacherComment
                        ),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Divider separating top info section and bottom badge/action section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimen.p1)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimen.p56)
                    .padding(horizontal = AppDimen.p16),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badges Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimen.p6),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when {
                        isGraded -> {
                            AppBadge(
                                text = stringResource(Res.string.submission_status_graded),
                                color = AppColor.Success
                            )
                            AppBadge(
                                text = stringResource(
                                    Res.string.submission_score,
                                    submission.score.toString()
                                ),
                                color = AppColor.Primary
                            )
                        }

                        isSubmittedOnly -> {
                            AppBadge(
                                text = stringResource(Res.string.submission_status_submitted),
                                color = AppColor.Primary
                            )
                        }

                        else -> {
                            AppBadge(
                                text = stringResource(Res.string.submission_status_not_submitted),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Trailing Action Button (only shown if not graded yet)
                if (!isGraded) {
                    AppTextButton(
                        text = stringResource(Res.string.submission_grade_btn),
                        onClick = onGradeClick,
                        enabled = true
                    )
                }
            }
        }
    }
}


private fun getInitials(name: String): String {
    if (name.isBlank()) return "?"
    val parts = name.trim().split("\\s+".toRegex())
    return when {
        parts.size >= 2 -> {
            val first = parts.first().firstOrNull()?.uppercaseChar() ?: ""
            val last = parts.last().firstOrNull()?.uppercaseChar() ?: ""
            "$first$last"
        }

        parts.isNotEmpty() -> {
            parts.first().take(2).uppercase()
        }

        else -> "?"
    }
}


private fun formatSubmissionDate(dateStr: String): String {
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
