package com.example.educationapp.presentation.screen.main.tab.component

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.theme.screenPadding
import com.example.educationapp.core.ui.avatar.AppAvatar
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.presentation.screen.dashboard.composable.SectionHeader
import com.example.educationapp.presentation.screen.main.LocalIsTablet
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.parent_student_list_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChildSelectorBar(
    children: List<UserProfile.Student>,
    selectedChild: UserProfile.Student?,
    onChildSelected: (UserProfile.Student) -> Unit,
    modifier: Modifier = Modifier
) {
    val isTablet = LocalIsTablet.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimen.screenPadding)
        ) {
            SectionHeader(
                title = stringResource(Res.string.parent_student_list_title),
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = AppDimen.p16),
                horizontalArrangement = Arrangement.spacedBy(AppDimen.p12)
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

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(AppDimen.p45))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(AppDimen.p45))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = AppDimen.p8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AppAvatar(
            name = child.fullName,
            imageUrl = child.img,
            modifier = Modifier.size(AppDimen.p36)
        )

        Column(
            verticalArrangement = Arrangement.Center
        ) {
            AppText(
                text = child.fullName,
                fontSize = if (isTablet) 15.sp else 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier
                    .width(if (isTablet) 150.dp else 120.dp)
                    .basicMarquee()
            )

            val studentCode = if (!child.studentCode.isNullOrBlank()) {
                child.studentCode
            } else {
                "#${child.studentId}"
            }
            AppText(
                text = studentCode,
                fontSize = 11.sp,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.6f
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
