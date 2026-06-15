package com.example.educationapp.core.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import com.example.educationapp.presentation.screen.main.LocalSharedHazeState
import com.example.educationapp.presentation.screen.main.LocalBottomBarHeight

/**
 * CompositionLocal to expose the local HazeState used for AppScaffold's content blur.
 */
val LocalTopBarHazeState = staticCompositionLocalOf<HazeState?> { null }

/**
 * A premium, unified scaffold for the application.
 * Integrates optional Top Bar measuring, Haze backdrop blur, and custom-positioned Pull-to-Refresh.
 * Provides paddingValues to content so it positions itself cleanly below the top bar
 * and respects system navigation bars for edge-to-edge layouts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    hazeState: HazeState? = null,
    isRefreshing: Boolean = false,
    onRefresh: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable BoxScope.(PaddingValues) -> Unit
) {
    var topBarHeightPx by remember { mutableStateOf(0) }
    val topBarHeightDp = with(LocalDensity.current) { topBarHeightPx.toDp() }
    val pullToRefreshState = rememberPullToRefreshState()

    val bottomBarHeight = LocalBottomBarHeight.current
    // Calculate system navigation bar padding for edge-to-edge safety at the bottom
    val bottomNavigationBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val paddingValues = remember(topBarHeightDp, bottomNavigationBarPadding, bottomBarHeight) {
        PaddingValues(
            start = 0.dp,
            top = topBarHeightDp,
            end = 0.dp,
            bottom = bottomNavigationBarPadding + bottomBarHeight
        )
    }

    val localHazeState = remember { HazeState() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(containerColor)
    ) {
        val contentModifier = Modifier
            .fillMaxSize()
            .hazeSource(state = localHazeState)

        // Core Content Container (with Pull-to-refresh)
        if (onRefresh != null) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                state = pullToRefreshState,
                modifier = contentModifier,
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = pullToRefreshState,
                        isRefreshing = isRefreshing,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = topBarHeightDp)
                    )
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    content = { content(paddingValues) }
                )
            }
        } else {
            Box(
                modifier = contentModifier,
                content = { content(paddingValues) }
            )
        }

        // Overlay Top Bar so it draws on top of the content (supporting backdrop blur)
        CompositionLocalProvider(LocalTopBarHazeState provides localHazeState) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .onGloballyPositioned { coordinates ->
                        topBarHeightPx = coordinates.size.height
                    }
            ) {
                topBar()
            }
        }
    }
}
