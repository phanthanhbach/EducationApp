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
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_sort_24dp
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
 * @param isFilterActive True if filter is currently active (styles icon differently).
 * @param onFilterClick Callback triggered when filter icon is clicked. If null, filter icon is hidden.
 * @param onBackClick Callback for back action. If provided, AppTopBar remains pinned; otherwise it collapses.
 * @param isTitleCentered True if title should be centered.
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
    val defaultAppTopBarHeightPx = with(density) { 56.dp.toPx() }
    val defaultSearchRowHeightPx = with(density) { 72.dp.toPx() }

    var appTopBarHeightPx by remember { mutableStateOf(statusBarHeightPx + defaultAppTopBarHeightPx) }
    var extraContentHeightPx by remember { mutableStateOf(0f) }
    var searchRowHeightPx by remember { mutableStateOf(defaultSearchRowHeightPx) }

    val shouldPinTopBar = onBackClick != null

    val maxScrollPx = if (shouldPinTopBar) {
        extraContentHeightPx
    } else {
        maxOf(0f, appTopBarHeightPx + extraContentHeightPx - statusBarHeightPx)
    }

    val maxScrollDp = with(density) { maxScrollPx.toDp() }
    val appTopBarHeightDp = with(density) { appTopBarHeightPx.toDp() }
    val searchRowHeightDp = with(density) { searchRowHeightPx.toDp() }

    val totalHeaderHeightDp = if (shouldPinTopBar) {
        appTopBarHeightDp + searchRowHeightDp + maxScrollDp
    } else {
        statusBarHeight + searchRowHeightDp + maxScrollDp
    }

    val listTopPaddingDp = if (shouldPinTopBar) {
        appTopBarHeightDp + searchRowHeightDp + 12.dp
    } else {
        statusBarHeight + searchRowHeightDp + 12.dp
    }

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

        // 2. Fixed Status Bar Background Mask with highest zIndex (5f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(statusBarHeight)
                .background(MaterialTheme.colorScheme.surface)
                .zIndex(5f)
        )

        if (shouldPinTopBar) {
            // Pinned AppTopBar (zIndex = 4f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .onSizeChanged { appTopBarHeightPx = it.height.toFloat() }
                    .zIndex(4f)
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
            }

            // Collapsible extraContent (zIndex = 3f)
            if (extraContent != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(0, (appTopBarHeightPx + headerOffset).roundToInt()) }
                        .background(MaterialTheme.colorScheme.surface)
                        .onSizeChanged { extraContentHeightPx = it.height.toFloat() }
                        .graphicsLayer {
                            alpha = 1f - collapseProgress
                        }
                        .zIndex(3f)
                ) {
                    extraContent()
                }
            }

            // Pinned Search Row (zIndex = 4f)
            val visibleExtraContentHeightPx = if (extraContent != null) {
                extraContentHeightPx + headerOffset
            } else {
                0f
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(0, (appTopBarHeightPx + visibleExtraContentHeightPx).roundToInt()) }
                    .onSizeChanged { searchRowHeightPx = it.height.toFloat() }
                    .zIndex(4f)
            ) {
                SearchRowContent(
                    searchQuery = searchQuery,
                    onSearch = onSearch,
                    placeholder = placeholder,
                    onFilterClick = onFilterClick,
                    isFilterActive = isFilterActive,
                    showDivider = headerOffset < 0f
                )
            }
        } else {
            // Collapsible TopBar + extraContent block (zIndex = 3f)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(0, headerOffset.roundToInt()) }
                    .background(MaterialTheme.colorScheme.surface)
                    .graphicsLayer {
                        alpha = 1f - collapseProgress
                    }
                    .zIndex(3f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { appTopBarHeightPx = it.height.toFloat() }
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
                }

                if (extraContent != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onSizeChanged { extraContentHeightPx = it.height.toFloat() }
                    ) {
                        extraContent()
                    }
                }
            }

            // Search Row (zIndex = 4f)
            val visibleCollapsibleHeightPx = maxOf(0f, maxScrollPx + headerOffset)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(0, (statusBarHeightPx + visibleCollapsibleHeightPx).roundToInt()) }
                    .onSizeChanged { searchRowHeightPx = it.height.toFloat() }
                    .zIndex(4f)
            ) {
                SearchRowContent(
                    searchQuery = searchQuery,
                    onSearch = onSearch,
                    placeholder = placeholder,
                    onFilterClick = onFilterClick,
                    isFilterActive = isFilterActive,
                    showDivider = headerOffset < 0f || lazyListState.firstVisibleItemScrollOffset > 0 || lazyListState.firstVisibleItemIndex > 0
                )
            }
        }
    }
}

@Composable
private fun SearchRowContent(
    searchQuery: String,
    onSearch: (String) -> Unit,
    placeholder: String,
    onFilterClick: (() -> Unit)?,
    isFilterActive: Boolean,
    showDivider: Boolean
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

        if (onFilterClick != null) {
            Spacer(modifier = Modifier.width(AppDimen.p16))
            AppIcon(
                drawableRes = Res.drawable.ic_sort_24dp,
                tint = if (isFilterActive) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                onClick = onFilterClick
            )
        }
    }

    if (showDivider) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            thickness = 0.5.dp
        )
    }
}
