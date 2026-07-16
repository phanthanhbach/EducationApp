package com.example.educationapp.presentation.screen.assignment.composable

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.chip.AppChip
import com.example.educationapp.presentation.screen.main.LocalIsTablet
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.submission_filter_not_submitted
import educationapp.shared.generated.resources.submission_filter_submitted
import org.jetbrains.compose.resources.stringResource

@Composable
fun SubmissionFilterBar(
    selectedFilter: Boolean,
    onFilterSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val isTablet = LocalIsTablet.current
    val horizontalPadding = if (isTablet) AppDimen.p24 else AppDimen.p16

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = horizontalPadding, vertical = AppDimen.p8),
        horizontalArrangement = Arrangement.spacedBy(AppDimen.p8)
    ) {
        AppChip(
            text = stringResource(Res.string.submission_filter_submitted),
            selected = selectedFilter,
            onClick = { onFilterSelected(true) }
        )
        AppChip(
            text = stringResource(Res.string.submission_filter_not_submitted),
            selected = !selectedFilter,
            onClick = { onFilterSelected(false) }
        )
    }
}
