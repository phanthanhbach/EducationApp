package com.example.educationapp.core.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.theme.screenPadding
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.ui.textfield.SearchTextField
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.roundToInt

/**
 * A premium collapsible and sticky top bar layout that handles nested scrolling calculations,
 * status bar clipping inside edge-to-edge/immersive mode windows, and snap-to-edge gravity.
 *
 * @param title The main screen title displayed in the AppTopBar.
 * @param searchQuery Current search query text.
 * @param onSearch Callback triggered when search query changes.
 * @param lazyListState Shared scroll state of the list content.
 * @param modifier Custom modifier for the root container.
 * @param placeholder Optional search placeholder text.
 * @param filterIcon Optional resource for filter action. If null, filter icon is hidden.
 * @param isFilterActive True if filter is currently active (styles icon differently).
 * @param onFilterClick Callback triggered when filter icon is clicked.
 * @param isRefreshing True if the data is refreshing.
 * @param onRefresh Callback to trigger refreshing.
 * @param extraContent Slot for collapsible headers below the AppTopBar (e.g., ChildSelectorBar).
 * @param content Slot to host list body. Receives `maxScrollDp`, `totalHeaderHeightDp`, and `listTopPaddingDp`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBarLayout(
    title: String,
    searchQuery: String,
    onSearch: (String) -> Unit,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    filterIcon: DrawableResource? = null,
    isFilterActive: Boolean = false,
    onFilterClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    isTitleCentered: Boolean = onBackClick != null,
    isRefreshing: Boolean = false,
    onRefresh: (() -> Unit)? = null,
    extraContent: @Composable (() -> Unit)? = null,
    content: @Composable (maxScrollDp: Dp, totalHeaderHeightDp: Dp, listTopPaddingDp: Dp) -> Unit
) {
    val density = LocalDensity.current
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val statusBarHeightPx = with(density) { statusBarHeight.toPx() }

    var sectionAHeightPx by remember { mutableStateOf(0f) }
    var searchRowHeightPx by remember { mutableStateOf(0f) }

    val maxScrollPx = maxOf(0f, sectionAHeightPx - statusBarHeightPx)
    val maxScrollDp = with(density) { maxScrollPx.toDp() }
    val searchRowHeightDp = with(density) { searchRowHeightPx.toDp() }
    val totalHeaderHeightDp = statusBarHeight + searchRowHeightDp + maxScrollDp
    val listTopPaddingDp = statusBarHeight + searchRowHeightDp + 12.dp

    val headerOffset by remember(lazyListState, maxScrollPx) {
        derivedStateOf {
            if (maxScrollPx <= 0f) 0f
            else {
                val scrolled = if (lazyListState.firstVisibleItemIndex == 0) {
                    lazyListState.firstVisibleItemScrollOffset.toFloat()
                } else {
                    maxScrollPx
                }
                -scrolled.coerceIn(0f, maxScrollPx)
            }
        }
    }

    val collapseProgress by remember(maxScrollPx, headerOffset) {
        derivedStateOf {
            if (maxScrollPx > 0f) {
                (-headerOffset / maxScrollPx).coerceIn(0f, 1f)
            } else {
                0f
            }
        }
    }

    val maxScrollRoundedPx = maxScrollPx.roundToInt()
    LaunchedEffect(lazyListState, maxScrollRoundedPx) {
        snapshotFlow { lazyListState.isScrollInProgress }
            .collect { isScrollInProgress ->
                if (!isScrollInProgress && lazyListState.firstVisibleItemIndex == 0 && maxScrollRoundedPx > 0) {
                    val currentOffset = lazyListState.firstVisibleItemScrollOffset
                    if (currentOffset in 1 until maxScrollRoundedPx) {
                        if (currentOffset < maxScrollRoundedPx / 2) {
                            lazyListState.animateScrollToItem(0, 0)
                        } else {
                            lazyListState.animateScrollToItem(1, 0)
                        }
                    }
                }
            }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 1. List / State Content
        if (onRefresh != null) {
            val pullToRefreshState = rememberPullToRefreshState()
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                state = pullToRefreshState,
                modifier = Modifier.fillMaxSize(),
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = pullToRefreshState,
                        isRefreshing = isRefreshing,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = totalHeaderHeightDp)
                    )
                }
            ) {
                content(maxScrollDp, totalHeaderHeightDp, listTopPaddingDp)
            }
        } else {
            content(maxScrollDp, totalHeaderHeightDp, listTopPaddingDp)
        }

        // 2. Fixed Status Bar Background Mask with higher zIndex (4f) to hide top bar sliding underneath
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(statusBarHeight)
                .background(MaterialTheme.colorScheme.surface)
                .zIndex(4f)
        )

        // 3. Floating Collapsible Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(0, headerOffset.roundToInt()) }
                .zIndex(3f)
        ) {
            // Section A: Collapsible part (Top Bar & Extra Content)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .onSizeChanged { sectionAHeightPx = it.height.toFloat() }
                    .graphicsLayer {
                        alpha = 1f - collapseProgress
                    }
            ) {
                AppTopBar(
                    title = if (isTitleCentered) title else null,
                    titleContent = if (!isTitleCentered) {
                        {
                            AppText(
                                text = title,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else null,
                    isTitleCentered = isTitleCentered,
                    onBackClick = onBackClick
                )

                if (extraContent != null) {
                    extraContent()
                }
            }

            // Section B: Pinned part (Search Bar + Filter)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { searchRowHeightPx = it.height.toFloat() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = AppDimen.screenPadding, vertical = AppDimen.p8),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchTextField(
                        value = searchQuery,
                        onSearch = onSearch,
                        containerColor = MaterialTheme.colorScheme.surface,
                        placeholder = placeholder,
                        modifier = Modifier.weight(1f)
                    )

                    if (filterIcon != null && onFilterClick != null) {
                        Spacer(modifier = Modifier.width(AppDimen.p16))
                        AppIcon(
                            drawableRes = filterIcon,
                            tint = if (isFilterActive) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            onClick = onFilterClick
                        )
                    }
                }

                if (headerOffset < 0f) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}
