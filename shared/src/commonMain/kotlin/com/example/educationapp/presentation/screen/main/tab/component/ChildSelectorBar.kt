package com.example.educationapp.presentation.screen.main.tab.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.UserProfile
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_person_filled_24dp

@Composable
fun ChildSelectorBar(
    children: List<UserProfile.Student>,
    selectedChild: UserProfile.Student?,
    onChildSelected: (UserProfile.Student) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        val isTablet = maxWidth >= 600.dp

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            AppText(
                text = "Danh sách học sinh",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(children) { child ->
                    val isSelected = child.studentId == selectedChild?.studentId
                    ChildChip(
                        child = child,
                        isSelected = isSelected,
                        isTablet = isTablet,
                        onClick = { onChildSelected(child) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChildChip(
    child: UserProfile.Student,
    isSelected: Boolean,
    isTablet: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    }

    val avatarSize = if (isTablet) 44.dp else 36.dp
    val textPadding = if (isTablet) 12.dp else 8.dp

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = textPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Child Avatar Placeholder
        Box(
            modifier = Modifier
                .size(avatarSize)
                .background(
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(
                drawableRes = Res.drawable.ic_person_filled_24dp,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                iconModifier = Modifier.size(avatarSize * 0.5f)
            )
        }

        Column(
            verticalArrangement = Arrangement.Center
        ) {
            AppText(
                text = child.fullName,
                fontSize = if (isTablet) 15.sp else 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(if (isTablet) 140.dp else 100.dp)
            )
            if (isTablet && !child.currentLevel.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                AppText(
                    text = child.currentLevel,
                    fontSize = 11.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
