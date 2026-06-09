package com.example.educationapp.presentation.screen.main.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.koin.koinScreenModel
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screen.dashboard.StudentDashboardContent
import com.example.educationapp.presentation.screen.dashboard.TeacherDashboardContent
import com.example.educationapp.presentation.screenmodel.dashboard.StudentDashboardScreenModel
import com.example.educationapp.presentation.screenmodel.dashboard.TeacherDashboardScreenModel
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_dashboard_filled_24dp
import educationapp.shared.generated.resources.tab_dashboard
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class DashboardTab(private val role: AppRole) : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.tab_dashboard)
            val icon = painterResource(Res.drawable.ic_dashboard_filled_24dp)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            AppTopBar(
                titleContent = {
                    AppText(
                        text = "Dashboard",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                isTitleCentered = false
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(AppDimen.p16)
                ) {
                    val tabNavigator = cafe.adriel.voyager.navigator.tab.LocalTabNavigator.current
                    val onViewScheduleClick = {
                        tabNavigator.current = ScheduleTab(role)
                    }

                    // Role-based portals
                    when (role) {
                        AppRole.STUDENT -> {
                            val screenModel = koinScreenModel<StudentDashboardScreenModel>()
                            StudentDashboardContent(
                                screenModel = screenModel,
                                onViewScheduleClick = onViewScheduleClick
                            )
                        }
                        AppRole.TEACHER -> {
                            val screenModel = koinScreenModel<TeacherDashboardScreenModel>()
                            TeacherDashboardContent(
                                screenModel = screenModel,
                                onViewScheduleClick = onViewScheduleClick
                            )
                        }
                        AppRole.PARENT -> ParentDashboard()
                        AppRole.UNKNOWN -> {}
                    }
                }
            }
        }
    }

    @Composable
    private fun ParentDashboard() {
        Card(
            shape = RoundedCornerShape(AppDimen.p12),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(AppDimen.p16)) {
                AppText(
                    text = "Parent Portal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppText(
                    text = "Monitor your child's academic performance, check outstanding school fees (Billing) and communicate with teachers.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}
