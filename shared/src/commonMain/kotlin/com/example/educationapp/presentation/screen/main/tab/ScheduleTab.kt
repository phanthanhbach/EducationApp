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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.enums.AppRole
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_calendar_month_filled_24dp
import educationapp.shared.generated.resources.tab_schedule
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class ScheduleTab(private val role: AppRole) : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.tab_schedule)
            val icon = painterResource(Res.drawable.ic_calendar_month_filled_24dp)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(AppDimen.p16)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(AppDimen.p16)
            ) {
                AppText(
                    text = stringResource(Res.string.tab_schedule),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                val displayRoleText = when (role) {
                    AppRole.STUDENT -> "Student Class Schedule"
                    AppRole.TEACHER -> "Teacher Class Sessions"
                    AppRole.PARENT -> "Children School Timetable"
                    AppRole.UNKNOWN -> "Schedule"
                }

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(AppDimen.p16)) {
                        AppText(
                            text = displayRoleText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AppText(
                            text = "No scheduled classes found for today.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
