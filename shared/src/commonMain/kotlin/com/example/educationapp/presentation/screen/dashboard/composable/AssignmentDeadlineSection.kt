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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.AssignmentReminder
import educationapp.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import kotlinx.datetime.LocalDateTime

import org.jetbrains.compose.resources.painterResource

@Composable
fun AssignmentDeadlineSection(
    reminders: List<AssignmentReminder>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppDimen.p10)
    ) {
        reminders.forEach { reminder ->
            AssignmentReminderCard(reminder = reminder)
        }
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
        } ${ldt.dayOfMonth.toString().padStart(2, '0')}/${
            ldt.monthNumber.toString().padStart(2, '0')
        }"
    } catch (e: Exception) {
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
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_docs_24dp),
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

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
                                    text = stringResource(Res.string.dashboard_assignment_hours_remaining, hoursLeft.toInt()),
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
                                text = stringResource(Res.string.dashboard_assignment_class_name, reminder.className),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            AppText(
                                text = stringResource(Res.string.dashboard_assignment_due_date, formattedTime),
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
                            text = stringResource(Res.string.dashboard_assignment_class_name, reminder.className),
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
                                text = stringResource(Res.string.dashboard_assignment_due_date, formattedTime),
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
                                    text = stringResource(Res.string.dashboard_assignment_hours_remaining, hoursLeft.toInt()),
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
