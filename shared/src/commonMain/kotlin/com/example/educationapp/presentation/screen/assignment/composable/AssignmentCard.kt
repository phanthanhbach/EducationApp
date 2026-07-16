package com.example.educationapp.presentation.screen.assignment.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.badge.AppBadge
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.Assignment
import com.example.educationapp.presentation.screen.main.LocalIsTablet
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.assignment_active
import educationapp.shared.generated.resources.assignment_due_date
import educationapp.shared.generated.resources.assignment_final_exam
import educationapp.shared.generated.resources.assignment_inactive
import educationapp.shared.generated.resources.assignment_not_submitted_count
import educationapp.shared.generated.resources.assignment_submitted_count
import educationapp.shared.generated.resources.ic_check_circle_filled_24dp
import educationapp.shared.generated.resources.ic_docs_24dp
import educationapp.shared.generated.resources.ic_error_outline_24dp
import educationapp.shared.generated.resources.ic_schedule_24dp
import org.jetbrains.compose.resources.stringResource

@Composable
fun AssignmentCard(
    assignment: Assignment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTablet = LocalIsTablet.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppDimen.p12))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(AppDimen.p12),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            AppDimen.p1,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimen.p2)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimen.p16),
            verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
        ) {
            // Header: Responsive Title and Status Badges
            if (isTablet) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    AppText(
                        text = assignment.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(AppDimen.p6),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = AppDimen.p12)
                    ) {
                        StatusBadgesContent(assignment)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AppDimen.p6)
                ) {
                    AppText(
                        text = assignment.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(AppDimen.p6),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadgesContent(assignment)
                    }
                }
            }

            // Description
            if (!assignment.description.isNullOrBlank()) {
                AppText(
                    text = assignment.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // File Attachment Card (directly below description)
            if (!assignment.fileAttachment.isNullOrBlank()) {
                AttachmentCard(url = assignment.fileAttachment)
            }

            // Subtle divider line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimen.p1)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            )

            // Bottom Metadata: Responsive Due Date and Counts
            if (isTablet) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DueDateContent(assignment.dueDate)
                    CountsRow(assignment)
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AppDimen.p8)
                ) {
                    DueDateContent(assignment.dueDate)
                    CountsRow(assignment)
                }
            }
        }
    }
}

@Composable
private fun StatusBadgesContent(assignment: Assignment) {
    AppBadge(
        text = stringResource(
            if (assignment.active) {
                Res.string.assignment_active
            } else {
                Res.string.assignment_inactive
            }
        ),
        color = if (assignment.active) AppColor.Success else MaterialTheme.colorScheme.onSurfaceVariant
    )
    if (assignment.finalExam) {
        AppBadge(
            text = stringResource(Res.string.assignment_final_exam),
            color = AppColor.Primary
        )
    }
}

@Composable
private fun DueDateContent(dueDate: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppDimen.p6),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIcon(
            drawableRes = Res.drawable.ic_schedule_24dp,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            iconModifier = Modifier.size(AppDimen.p16)
        )
        AppText(
            text = stringResource(
                Res.string.assignment_due_date,
                formatAssignmentDate(dueDate)
            ),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CountsRow(assignment: Assignment, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppDimen.p16),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppDimen.p4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon(
                drawableRes = Res.drawable.ic_check_circle_filled_24dp,
                tint = AppColor.Success,
                iconModifier = Modifier.size(AppDimen.p16)
            )
            AppText(
                text = stringResource(
                    Res.string.assignment_submitted_count,
                    assignment.submittedCount
                ),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppDimen.p4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon(
                drawableRes = Res.drawable.ic_error_outline_24dp,
                tint = AppColor.Warning,
                iconModifier = Modifier.size(AppDimen.p16)
            )
            AppText(
                text = stringResource(
                    Res.string.assignment_not_submitted_count,
                    assignment.notSubmittedCount
                ),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AttachmentCard(
    url: String,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    Row(
        modifier = modifier
            .widthIn(max = AppDimen.p400)
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppDimen.p8))
            .clickable {
                try {
                    uriHandler.openUri(url)
                } catch (_: Exception) {
                }
            }
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
            .border(
                BorderStroke(
                    AppDimen.p1,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ), RoundedCornerShape(AppDimen.p8)
            )
            .padding(horizontal = AppDimen.p12, vertical = AppDimen.p8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimen.p8)
    ) {
        AppIcon(
            drawableRes = Res.drawable.ic_docs_24dp,
            tint = AppColor.Primary,
            iconModifier = Modifier.size(AppDimen.p20)
        )
        AppText(
            text = getFileName(url),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun getFileName(url: String): String {
    return try {
        val decoded = url.substringBefore('?').substringAfterLast('/')
        if (decoded.isBlank()) "Attachment" else decoded.replace("%20", " ").replace("+", " ")
    } catch (_: Exception) {
        "Attachment"
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
