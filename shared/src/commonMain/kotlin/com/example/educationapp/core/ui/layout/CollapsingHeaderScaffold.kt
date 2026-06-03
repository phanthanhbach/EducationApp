package com.example.educationapp.core.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.educationapp.core.theme.AppDimen
import kotlin.math.roundToInt

/**
 * A small SliverAppBar-style layout for screens with:
 * - a cover layer behind everything,
 * - a white sheet that moves up over the cover,
 * - overlay content that transforms from expanded to collapsed,
 * - a LazyColumn body that never scrolls under the status/app-bar area.
 *
 * The scroll animation is driven by the first LazyColumn item, which is only a
 * spacer. While that spacer scrolls away, collapseProgress moves from 0f to 1f.
 */
@Composable
fun CollapsingHeaderScaffold(
    cover: @Composable () -> Unit,
    headerActions: @Composable (collapseProgress: Float) -> Unit,
    collapsingContent: @Composable (collapseProgress: Float) -> Unit,
    modifier: Modifier = Modifier,
    // Y position of the top edge of the sheet when fully expanded.
    // Example: 164.dp means the white sheet starts 164.dp below the top screen edge.
    sheetTopExpanded: Dp = 164.dp,
    // Height of the invisible first LazyColumn item. This controls how much
    // scroll distance is available before the content reaches the collapsed app bar.
    expandedContentSpacer: Dp = 156.dp,
    // The collapsed "app bar" height below the status bar. The LazyColumn is
    // padded by statusBar + this value so body content cannot draw under it.
    collapsedContentHeight: Dp = AppDimen.p56,
    // Corner radius of the sheet while expanded. It is kept constant here; callers
    // can pass 0.dp if they want a flat sheet.
    sheetCornerRadius: Dp = AppDimen.p24,
    contentPadding: PaddingValues = PaddingValues(
        start = AppDimen.p16,
        end = AppDimen.p16,
        bottom = 24.dp
    ),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(AppDimen.p16),
    content: LazyListScope.() -> Unit
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current

    // LazyListState reports scroll offsets in pixels, while our constants are Dp.
    // Convert once so progress math stays correct across screen densities.
    val expandedContentSpacerPx = with(density) { expandedContentSpacer.toPx() }
    val expandedContentSpacerRoundedPx = expandedContentSpacerPx.roundToInt()

    // 0f = expanded: first spacer item is still fully visible.
    // 1f = collapsed: first spacer item has fully scrolled away, or later items
    // are visible. This value is passed to headerActions/collapsingContent so
    // they can lerp size, position, color, alpha, etc.
    val collapseProgress by remember(listState, expandedContentSpacerPx) {
        derivedStateOf {
            val firstItemOffset = if (listState.firstVisibleItemIndex == 0) {
                listState.firstVisibleItemScrollOffset.toFloat()
            } else {
                Float.MAX_VALUE
            }
            (firstItemOffset / expandedContentSpacerPx).coerceIn(0f, 1f)
        }
    }

    // Move the sheet from its expanded position to the very top as the first
    // spacer scrolls away. Content is a separate layer, so the sheet can slide
    // over the cover while the body scrolls normally.
    val sheetOffset = lerpDp(sheetTopExpanded, 0.dp, collapseProgress)

    // This is the top safe area for scrollable body content. Without it, a fast
    // scroll can draw the body under the status bar or under collapsed profile info.
    val collapsedContentTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + collapsedContentHeight

    // Snap behavior: when the user stops between expanded and collapsed states,
    // settle to the nearest side. This creates the "gravity" feel.
    // - offset < half spacer: expand back to item 0 offset 0
    // - offset >= half spacer: collapse by scrolling to the first real content item
    LaunchedEffect(listState, expandedContentSpacerRoundedPx) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrollInProgress ->
                if (!isScrollInProgress && listState.firstVisibleItemIndex == 0) {
                    val currentOffset = listState.firstVisibleItemScrollOffset
                    if (currentOffset in 1 until expandedContentSpacerRoundedPx) {
                        if (currentOffset < expandedContentSpacerRoundedPx / 2) {
                            listState.animateScrollToItem(0, 0)
                        } else {
                            listState.animateScrollToItem(1, 0)
                        }
                    }
                }
            }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        cover()

        val currentCornerRadius = lerpDp(sheetCornerRadius, 0.dp, collapseProgress)

        // The moving white surface. It is not part of the LazyColumn, so it can
        // visually cover the image while the body content keeps its own clipping.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = sheetOffset)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(
                        topStart = currentCornerRadius,
                        topEnd = currentCornerRadius
                    )
                )
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = collapsedContentTop),
            verticalArrangement = verticalArrangement,
            contentPadding = contentPadding
        ) {
            // Invisible header space. This item is the "scroll ruler" used to
            // compute collapseProgress and to give the expanded profile room.
            item {
                Spacer(modifier = Modifier.height(expandedContentSpacer))
            }
            content()
        }

        // Overlay layers are rendered after the body so they stay above content.
        // They receive progress instead of listState to keep animation decisions
        // local to each screen.
        collapsingContent(collapseProgress)
        headerActions(collapseProgress)
    }
}

// Simple Dp interpolation helper. fraction 0f returns start, 1f returns stop.
fun lerpDp(
    start: Dp,
    stop: Dp,
    fraction: Float
): Dp = start + (stop - start) * fraction
