package com.example.educationapp.presentation.screen.main.tab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.presentation.screen.parent.ChildScheduleScreen
import com.example.educationapp.presentation.screen.parent.ChildAttendanceRateScreen
import com.example.educationapp.core.ui.image.AppImage
import com.example.educationapp.core.ui.image.CoreMediaSource
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.row.OptionRow
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.presentation.screen.main.LocalParentMainScreenModel
import com.example.educationapp.presentation.screen.main.tab.component.ChildSelectorBar
import com.example.educationapp.presentation.screenmodel.parent.ParentChildrenState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_calendar_month_filled_24dp
import educationapp.shared.generated.resources.ic_event_24dp
import educationapp.shared.generated.resources.ic_group_24dp
import educationapp.shared.generated.resources.ic_person_filled_24dp
import educationapp.shared.generated.resources.tab_my_children
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class MyChildrenTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.tab_my_children)
            val icon = painterResource(Res.drawable.ic_group_24dp)

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
        val parentMainScreenModel = LocalParentMainScreenModel.current
        val childrenState by parentMainScreenModel.childrenState.collectAsState()
        val selectedChild by parentMainScreenModel.selectedChild.collectAsState()

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            AppTopBar(
                titleContent = {
                    AppText(
                        text = stringResource(Res.string.tab_my_children),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                isTitleCentered = false
            )

            when (val state = childrenState) {
                is ParentChildrenState.Loading -> {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColor.Primary)
                    }
                }

                is ParentChildrenState.Error -> {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppText(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }
                }

                is ParentChildrenState.Success -> {
                    val childrenList = state.children
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (childrenList.isNotEmpty()) {
                            ChildSelectorBar(
                                children = childrenList,
                                selectedChild = selectedChild,
                                onChildSelected = { parentMainScreenModel.selectChild(it) }
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(scrollState)
                                ) {
                                    selectedChild?.let { child ->
                                        ChildDetailCard(child = child)
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                AppText(
                                    text = "Không có thông tin học sinh nào.",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ChildDetailCard(
        child: UserProfile.Student,
        modifier: Modifier = Modifier
    ) {
        // Child Info Card with actions inside
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Info section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Avatar using AppImage
                        val imageSource = if (!child.img.isNullOrBlank()) {
                            CoreMediaSource.Url(child.img)
                        } else {
                            CoreMediaSource.ComposeResource(Res.drawable.ic_person_filled_24dp)
                        }

                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            AppImage(
                                source = imageSource,
                                modifier = Modifier.fillMaxSize(),
                                placeholder = painterResource(Res.drawable.ic_person_filled_24dp),
                                error = painterResource(Res.drawable.ic_person_filled_24dp)
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AppText(
                                    text = child.fullName,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                // Badge status
                                val isCompleted = child.status?.lowercase() == "completed"
                                val isCancelled =
                                    child.status?.lowercase() == "cancelled" || child.status?.lowercase() == "dropped"
                                val statusBgColor = when {
                                    isCompleted -> Color(0xFFE8F5E9)
                                    isCancelled -> Color(0xFFFFEBEE)
                                    else -> Color(0xFFE3F2FD)
                                }
                                val statusTextColor = when {
                                    isCompleted -> Color(0xFF2E7D32)
                                    isCancelled -> Color(0xFFC62828)
                                    else -> Color(0xFF1565C0)
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(statusBgColor)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    AppText(
                                        text = child.status ?: "Active",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = statusTextColor
                                    )
                                }
                            }

                            val studentCodeText = if (!child.studentCode.isNullOrBlank()) {
                                "Mã học sinh: ${child.studentCode}"
                            } else {
                                "Mã học sinh: #${child.studentId}"
                            }
                            AppText(
                                text = studentCodeText,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (!child.currentLevel.isNullOrBlank()) {
                                AppText(
                                    text = "Trình độ: ${child.currentLevel}",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Divider
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        thickness = 1.dp
                    )

                    // Details section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailRow(label = "Ngày sinh", value = child.dateOfBirth ?: "N/A")
                        DetailRow(label = "Giới tính", value = child.gender ?: "N/A")
                        DetailRow(label = "Địa chỉ", value = child.address ?: "N/A")
                    }
                }

                // Learning Actions Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    AppText(
                        text = "Tiện ích học tập",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )

                    // Action: Schedule
                    val parentNavigator = LocalNavigator.currentOrThrow.parent
                    OptionRow(
                        title = "Lịch học của con",
                        description = "Xem lịch học chi tiết các ngày trong tuần",
                        iconRes = Res.drawable.ic_calendar_month_filled_24dp,
                        iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                        iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                        onClick = {
                            parentNavigator?.push(
                                ChildScheduleScreen(
                                    studentId = child.studentId.toLong(),
                                    studentName = child.fullName
                                )
                            )
                        }
                    )

                    // Action: Attendance Rate
                    OptionRow(
                        title = "Tỉ lệ tham gia buổi học",
                        description = "Theo dõi chuyên cần và số buổi nghỉ học",
                        iconRes = Res.drawable.ic_event_24dp,
                        iconBgColor = MaterialTheme.colorScheme.secondaryContainer,
                        iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                        onClick = {
                            parentNavigator?.push(
                                ChildAttendanceRateScreen(
                                    studentId = child.studentId.toLong(),
                                    studentName = child.fullName
                                )
                            )
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun DetailRow(
        label: String,
        value: String,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier.fillMaxWidth().padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            AppText(
                text = label,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            AppText(
                text = value,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(2f),
                textAlign = TextAlign.End
            )
        }
    }
}
