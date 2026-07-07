package com.example.educationapp.presentation.screen.dashboard.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.AssignmentReminder
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.dashboard_assignment_class_name
import educationapp.shared.generated.resources.dashboard_assignment_due_date
import educationapp.shared.generated.resources.dashboard_assignment_hours_remaining
import educationapp.shared.generated.resources.dashboard_assignments_empty_desc
import educationapp.shared.generated.resources.dashboard_assignments_empty_title
import educationapp.shared.generated.resources.ic_check_circle_filled_24dp
import educationapp.shared.generated.resources.ic_docs_24dp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import org.jetbrains.compose.resources.stringResource

@Composable
fun AssignmentDeadlineSection(
    reminders: List<AssignmentReminder>,
    modifier: Modifier = Modifier
) {
    if (reminders.isEmpty()) {
        NoAssignmentsView(modifier = modifier)
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppDimen.p10)
        ) {
            reminders.forEach { reminder ->
                AssignmentReminderCard(reminder = reminder)
            }
        }
    }
}

@Composable
private fun NoAssignmentsView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AppDimen.p16, horizontal = AppDimen.p20),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppIcon(
            drawableRes = Res.drawable.ic_check_circle_filled_24dp,
            boxModifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
            iconModifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(AppDimen.p12))

        AppText(
            text = stringResource(Res.string.dashboard_assignments_empty_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(AppDimen.p6))

        AppText(
            text = stringResource(Res.string.dashboard_assignments_empty_desc),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = AppDimen.p16)
        )
    }
}

@Composable
private fun AssignmentReminderCard(
    reminder: AssignmentReminder,
    modifier: Modifier = Modifier
) {
    val hoursLeft = reminder.hoursRemaining
    val (badgeBgColor, badgeTextColor, iconTint) = when {
        hoursLeft < 12 -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), Color(0xFFC62828)) // Red
        hoursLeft < 24 -> Triple(Color(0xFFFFF3E0), Color(0xFFEF6C00), Color(0xFFEF6C00)) // Orange
        else -> Triple(Color(0xFFFFFDE7), Color(0xFFFBC02D), Color(0xFFFBC02D)) // Yellow
    }

    val formattedTime = try {
        val ldt = LocalDateTime.parse(reminder.dueDate)
        "${ldt.hour.toString().padStart(2, '0')}:${
            ldt.minute.toString().padStart(2, '0')
        } ${ldt.day.toString().padStart(2, '0')}/${
            ldt.month.number.toString().padStart(2, '0')
        }"
    } catch (_: Exception) {
        reminder.dueDate
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val isTablet = maxWidth >= 600.dp

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimen.p12),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimen.p12)
            ) {
                // Icon docs
                AppIcon(
                    drawableRes = Res.drawable.ic_docs_24dp,
                    boxModifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconTint.copy(alpha = 0.12f)),
                    iconModifier = Modifier.size(20.dp),
                    tint = iconTint
                )

                if (isTablet) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppText(
                                text = reminder.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(badgeBgColor)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                AppText(
                                    text = stringResource(
                                        Res.string.dashboard_assignment_hours_remaining,
                                        hoursLeft.toInt()
                                    ),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeTextColor
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppText(
                                text = stringResource(
                                    Res.string.dashboard_assignment_class_name,
                                    reminder.className
                                ),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            AppText(
                                text = stringResource(
                                    Res.string.dashboard_assignment_due_date,
                                    formattedTime
                                ),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AppText(
                            text = reminder.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        AppText(
                            text = stringResource(
                                Res.string.dashboard_assignment_class_name,
                                reminder.className
                            ),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppText(
                                text = stringResource(
                                    Res.string.dashboard_assignment_due_date,
                                    formattedTime
                                ),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(badgeBgColor)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                AppText(
                                    text = stringResource(
                                        Res.string.dashboard_assignment_hours_remaining,
                                        hoursLeft.toInt()
                                    ),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeTextColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
