package com.example.educationapp.presentation.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screen.main.tab.AssignmentTab
import com.example.educationapp.presentation.screen.main.tab.DashboardTab
import com.example.educationapp.presentation.screen.main.tab.ProfileTab
import com.example.educationapp.presentation.screen.main.tab.ScheduleTab

class MainScreen(private val role: AppRole) : Screen {

    @Composable
    override fun Content() {
        val tabs = remember(role) {
            listOf(
                DashboardTab(role),
                ScheduleTab(role),
                AssignmentTab(role),
                ProfileTab()
            )
        }

        TabNavigator(tabs.first()) { tabNavigator ->
            val selectedIndex = tabs.indexOfFirst { it.options.index == tabNavigator.current.options.index }.coerceAtLeast(0)

            val navItems = tabs.mapIndexed { index, tab ->
                BottomNavItem(
                    title = tab.options.title,
                    icon = tab.options.icon!!,
                    index = index
                )
            }

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isTablet = maxWidth >= 600.dp

                if (isTablet) {
                    // Tablet Layout: Side Navigation Rail + Main Content
                    Row(modifier = Modifier.fillMaxSize()) {
                        VerticalNavigationRail(
                            items = navItems,
                            selectedIndex = selectedIndex,
                            onItemSelected = { index ->
                                tabNavigator.current = tabs[index]
                            }
                        )
                        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                            CurrentTab()
                        }
                    }
                } else {
                    // Mobile Layout: Scaffold with Curved Bottom Navigation
                    Scaffold(
                        bottomBar = {
                            CurvedBottomNavigation(
                                items = navItems,
                                selectedIndex = selectedIndex,
                                onItemSelected = { index ->
                                    tabNavigator.current = tabs[index]
                                }
                            )
                        }
                    ) { padding ->
                        Box(modifier = Modifier.padding(padding)) {
                            CurrentTab()
                        }
                    }
                }
            }
        }
    }
}
