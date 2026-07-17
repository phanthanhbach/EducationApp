package com.example.educationapp.presentation.screen.assignment.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.enums.AssignmentFilter
import org.jetbrains.compose.resources.stringResource

@Composable
fun SubmissionFilterBar(
    selectedFilter: AssignmentFilter,
    onFilterSelected: (AssignmentFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    // Sắp xếp thứ tự tab cho Giáo viên & Học sinh: Đã nộp trước, Chưa nộp sau
    val tabs = listOf(AssignmentFilter.SUBMITTED, AssignmentFilter.NOT_SUBMITTED)
    val selectedTabIndex = tabs.indexOf(selectedFilter)

    SecondaryTabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth(),
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier
                    .tabIndicatorOffset(selectedTabIndex)
                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)),
                color = MaterialTheme.colorScheme.primary,
                height = 3.dp
            )
        }
    ) {
        tabs.forEachIndexed { index, filterType ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onFilterSelected(filterType) },
                text = {
                    AppText(
                        text = stringResource(filterType.getLabelRes()),
                        fontSize = 14.sp,
                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (selectedTabIndex == index) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            )
        }
    }
}
