package com.example.educationapp.core.ui.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.example.educationapp.core.theme.AppDimen

@Composable
fun ThreeSectionColumn(
    first: @Composable ColumnScope.() -> Unit,
    second: @Composable ColumnScope.() -> Unit,
    third: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    spacing: Dp = AppDimen.zero,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing),
        horizontalAlignment = horizontalAlignment
    ) {
        first()
        second()
        third()
    }
}

/**
 * A vertical layout that arranges exactly three sections with configurable spacing.
 *
 * Typical use cases:
 * - **Header – Body – Footer**: top section for a title/toolbar, middle section
 *   with `.weight(1f)` for scrollable content, bottom section for action buttons.
 * - **Top – Center – Bottom**: any three vertically stacked regions.
 *
 * Each section lambda receives [ColumnScope], so callers can use `.weight()` and
 * other column-scoped modifiers directly inside their content blocks.
 *
 * Example:
 * ```
 * AppThreeSectionColumn(
 *     modifier = Modifier.fillMaxSize(),
 *     spacing = 16.dp,
 *     first = { Text("Header") },
 *     second = { Box(Modifier.weight(1f)) { /* scrollable content */ } },
 *     third = { Button(onClick = {}) { Text("Action") } }
 * )
 * ```
 */