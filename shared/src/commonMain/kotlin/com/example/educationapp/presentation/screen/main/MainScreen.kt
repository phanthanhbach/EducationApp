package com.example.educationapp.presentation.screen.main

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.example.educationapp.core.ui.toast.LocalToastController
import com.example.educationapp.core.util.AppBackHandler
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screen.main.tab.ClassesTab
import com.example.educationapp.presentation.screen.main.tab.DashboardTab
import com.example.educationapp.presentation.screen.main.tab.FeedbackTab
import com.example.educationapp.presentation.screen.main.tab.MyChildrenTab
import com.example.educationapp.presentation.screen.main.tab.PaymentsTab
import com.example.educationapp.presentation.screen.main.tab.ProfileTab
import com.example.educationapp.presentation.screen.main.tab.ScheduleTab
import com.example.educationapp.presentation.screenmodel.parent.ParentMainScreenModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.press_back_again_to_exit
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

class MainScreen(private val role: AppRole) : Screen {

    @Composable
    override fun Content() {
        val toastController = LocalToastController.current
        val coroutineScope = rememberCoroutineScope()
        var backPressedOnce by remember { mutableStateOf(false) }
        val exitToastMessage = stringResource(Res.string.press_back_again_to_exit)

        AppBackHandler(enabled = !backPressedOnce) {
            backPressedOnce = true
            toastController.show(exitToastMessage)
            coroutineScope.launch {
                delay(2000)
                backPressedOnce = false
            }
        }

        val sharedHazeState = remember { HazeState() }
        val tabs =
                remember(role) {
                    when (role) {
                        AppRole.PARENT ->
                                listOf(MyChildrenTab(), FeedbackTab(), PaymentsTab(), ProfileTab())
                        AppRole.TEACHER ->
                                listOf(DashboardTab(), ScheduleTab(), ClassesTab(), ProfileTab())
                        else ->
                                listOf( // STUDENT or other roles if any
                                        DashboardTab(),
                                        ScheduleTab(),
                                        ClassesTab(),
                                        PaymentsTab(),
                                        ProfileTab()
                                )
                    }
                }

        @Composable
        fun ParentWrapper(content: @Composable () -> Unit) {
            if (role == AppRole.PARENT) {
                val parentScreenModel = koinScreenModel<ParentMainScreenModel>()
                CompositionLocalProvider(LocalParentMainScreenModel provides parentScreenModel) {
                    content()
                }
            } else {
                content()
            }
        }

        CompositionLocalProvider(
                LocalAppRole provides role,
                LocalSharedHazeState provides sharedHazeState,
                LocalBottomBarHeight provides 64.dp
        ) {
            ParentWrapper {
                TabNavigator(tabs.first()) { tabNavigator ->
                    val selectedIndex =
                            tabs
                                    .indexOfFirst {
                                        it.options.index == tabNavigator.current.options.index
                                    }
                                    .coerceAtLeast(0)

                    val navItems =
                            tabs.mapIndexed { index, tab ->
                                BottomNavItem(
                                        title = tab.options.title,
                                        icon = tab.options.icon!!,
                                        index = index
                                )
                            }

                    Box(modifier = Modifier.fillMaxSize()) {
                        val isTablet = LocalIsTablet.current
                        var isNavigationRailExpanded by rememberSaveable { mutableStateOf(false) }
                        val layoutDirection = LocalLayoutDirection.current

                        Scaffold(
                                contentWindowInsets =
                                        if (isTablet) WindowInsets(0) else WindowInsets.systemBars,
                                containerColor = MaterialTheme.colorScheme.surface,
                                bottomBar = {
                                    if (!isTablet) {
                                        BottomNavigation(
                                                items = navItems,
                                                selectedIndex = selectedIndex,
                                                onItemSelected = { index ->
                                                    tabNavigator.current = tabs[index]
                                                },
                                                barColor =
                                                        MaterialTheme.colorScheme.surfaceContainer,
                                                hazeState = sharedHazeState
                                        )
                                    }
                                }
                        ) { innerPadding ->
                            if (isTablet) {
                                // Tablet / landscape layout.
                                // The navigation rail background extends edge-to-edge
                                // (behind the status bar and left system inset) while
                                // its interactive content stays within the safe area.
                                // Tab content is padded for top (except ProfileTab),
                                // end and bottom system insets.
                                val systemInsets = WindowInsets.systemBars.asPaddingValues()
                                val topInset = systemInsets.calculateTopPadding()
                                val endInset = systemInsets.calculateEndPadding(layoutDirection)
                                val bottomInset = systemInsets.calculateBottomPadding()
                                val startInset = systemInsets.calculateStartPadding(layoutDirection)
                                val isBleedingTab =
                                        tabNavigator.current is ProfileTab ||
                                                tabNavigator.current is ClassesTab ||
                                                tabNavigator.current is ScheduleTab ||
                                                tabNavigator.current is DashboardTab ||
                                                tabNavigator.current is MyChildrenTab ||
                                                tabNavigator.current is FeedbackTab ||
                                                tabNavigator.current is PaymentsTab

                                val tabContentPadding =
                                        PaddingValues(
                                                start = 76.dp + startInset, // space for the
                                                // collapsed rail + left
                                                // safe area
                                                top = if (isBleedingTab) 0.dp else topInset,
                                                end = endInset,
                                                bottom = bottomInset
                                        )

                                Box(modifier = Modifier.fillMaxSize()) {
                                    // Tab content
                                    Box(
                                            modifier =
                                                    Modifier.fillMaxSize()
                                                            .background(
                                                                    MaterialTheme.colorScheme
                                                                            .background
                                                            )
                                                            .hazeSource(state = sharedHazeState)
                                                            .padding(tabContentPadding)
                                    ) {
                                        CompositionLocalProvider(
                                            com.example.educationapp.core.ui.layout.LocalAppScaffoldApplyInsets provides false
                                        ) {
                                            Crossfade(
                                                    targetState = tabNavigator.current,
                                                    animationSpec = tween(durationMillis = 220)
                                            ) { tab -> tab.Content() }
                                        }
                                    }

                                    // Scrim when rail is expanded
                                    if (isNavigationRailExpanded) {
                                        Box(
                                                modifier =
                                                        Modifier.fillMaxSize()
                                                                .padding(start = 76.dp + startInset)
                                                                .zIndex(0.5f)
                                                                .background(
                                                                        Color.Black.copy(
                                                                                alpha = 0.02f
                                                                        )
                                                                )
                                                                .clickable(
                                                                        interactionSource =
                                                                                remember {
                                                                                    MutableInteractionSource()
                                                                                },
                                                                        indication = null
                                                                ) {
                                                                    isNavigationRailExpanded = false
                                                                }
                                        )
                                    }

                                    // Navigation rail – full-bleed, handles its own insets
                                    VerticalNavigationRail(
                                            items = navItems,
                                            selectedIndex = selectedIndex,
                                            onItemSelected = { index ->
                                                tabNavigator.current = tabs[index]
                                            },
                                            isExpanded = isNavigationRailExpanded,
                                            onExpandedChange = { isNavigationRailExpanded = it },
                                            barColor = MaterialTheme.colorScheme.surfaceContainer,
                                            hazeState = sharedHazeState,
                                            modifier =
                                                    Modifier.align(Alignment.CenterStart)
                                                            .zIndex(1f)
                                                            .fillMaxHeight()
                                    )
                                }
                            } else {
                                // Mobile layout
                                val isBleedingTab =
                                        tabNavigator.current is ProfileTab ||
                                                tabNavigator.current is ClassesTab ||
                                                tabNavigator.current is ScheduleTab ||
                                                tabNavigator.current is DashboardTab ||
                                                tabNavigator.current is MyChildrenTab ||
                                                tabNavigator.current is FeedbackTab ||
                                                tabNavigator.current is PaymentsTab
                                val contentPadding =
                                        if (isBleedingTab) {
                                            PaddingValues(
                                                    start =
                                                            innerPadding.calculateStartPadding(
                                                                    layoutDirection
                                                            ),
                                                    top = 0.dp,
                                                    end =
                                                            innerPadding.calculateEndPadding(
                                                                    layoutDirection
                                                            ),
                                                    bottom = 0.dp
                                            )
                                        } else {
                                            innerPadding
                                        }

                                Box(
                                        modifier =
                                                Modifier.fillMaxSize()
                                                        .background(
                                                                MaterialTheme.colorScheme.background
                                                        )
                                                        .hazeSource(state = sharedHazeState)
                                                        .padding(contentPadding)
                                ) {
                                    Crossfade(
                                            targetState = tabNavigator.current,
                                            animationSpec = tween(durationMillis = 220)
                                    ) { tab -> tab.Content() }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
