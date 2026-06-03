package com.example.educationapp.core.ui.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.example.educationapp.core.theme.AppDimen

@Composable
fun ThreeSectionRow(
    first: @Composable RowScope.() -> Unit,
    second: @Composable RowScope.() -> Unit,
    third: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    spacing: Dp = AppDimen.zero,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = verticalAlignment
    ) {
        first()
        second()
        third()
    }
}

/**
 * A horizontal layout that arranges exactly three sections with configurable spacing.
 *
 * Typical use cases:
 * - **Leading – Center – Trailing**: left icon/back button, centered title,
 *   right action icons — a common top-bar pattern.
 * - **Start – Middle – End**: any three horizontally placed regions.
 *
 * Each section lambda receives [RowScope], so callers can use `.weight()` and
 * other row-scoped modifiers directly inside their content blocks.
 *
 * Example:
 * ```
 * AppThreeSectionRow(
 *     modifier = Modifier.fillMaxWidth().height(56.dp),
 *     first = { Icon(Icons.Default.ArrowBack, null) },
 *     second = { Text("Title", Modifier.weight(1f), textAlign = TextAlign.Center) },
 *     third = { Icon(Icons.Default.Settings, null) }
 * )
 * ```
 */