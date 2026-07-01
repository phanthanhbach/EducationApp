package com.example.educationapp.presentation.screen.schedule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.button.AppButton
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.layout.AppScaffold
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.shimmer.skeleton.InfoRowSkeleton
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.ui.toast.LocalToastController
import com.example.educationapp.domain.entity.TeacherCheckInResult
import com.example.educationapp.domain.enums.SessionStatus
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleSessionUiModel
import com.example.educationapp.presentation.screenmodel.schedule.SessionDetailScreenModel
import com.example.educationapp.presentation.screenmodel.schedule.SessionDetailState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.btn_check_in_now
import educationapp.shared.generated.resources.btn_check_out_session
import educationapp.shared.generated.resources.btn_retry
import educationapp.shared.generated.resources.btn_take_attendance
import educationapp.shared.generated.resources.btn_view_attendance
import educationapp.shared.generated.resources.date_time_format
import educationapp.shared.generated.resources.ic_assignment_filled_24dp
import educationapp.shared.generated.resources.ic_calendar_month_filled_24dp
import educationapp.shared.generated.resources.msg_error_occurred
import educationapp.shared.generated.resources.session_checkin_status_title
import educationapp.shared.generated.resources.session_class_prefix
import educationapp.shared.generated.resources.session_desc_completed
import educationapp.shared.generated.resources.session_desc_ongoing
import educationapp.shared.generated.resources.session_desc_upcoming
import educationapp.shared.generated.resources.session_detail_title
import educationapp.shared.generated.resources.session_info_title
import educationapp.shared.generated.resources.session_label_checkin_status
import educationapp.shared.generated.resources.session_label_checkin_time
import educationapp.shared.generated.resources.session_label_checkout_time
import educationapp.shared.generated.resources.session_not_checked_in
import educationapp.shared.generated.resources.session_number_prefix
import educationapp.shared.generated.resources.session_room
import educationapp.shared.generated.resources.session_status_checked_in_success
import educationapp.shared.generated.resources.session_status_checked_out_success
import educationapp.shared.generated.resources.session_status_completed
import educationapp.shared.generated.resources.session_status_late_minutes
import educationapp.shared.generated.resources.session_status_on_time
import educationapp.shared.generated.resources.session_status_ongoing
import educationapp.shared.generated.resources.session_status_upcoming
import educationapp.shared.generated.resources.session_time
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

class SessionDetailScreen(
    private val session: ScheduleSessionUiModel
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<SessionDetailScreenModel>()
        val state by screenModel.state.collectAsState()
        val isRefreshing by screenModel.isRefreshing.collectAsState()
        val scrollState = rememberScrollState()

        val toastController = LocalToastController.current

        // Fetch check-in status when screen launches
        LaunchedEffect(session) {
            screenModel.loadCheckInStatus(session)
        }

        AppScaffold(
            topBar = {
                AppTopBar(
                    title = stringResource(Res.string.session_detail_title),
                    onBackClick = { navigator.pop() }
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
            isRefreshing = isRefreshing,
            onRefresh = { screenModel.loadCheckInStatus(session, isRefresh = true) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val isTablet = maxWidth >= 600.dp

                    if (isTablet) {
                        // Tablet Side-by-Side Bipartite Layout
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(AppDimen.p24),
                            horizontalArrangement = Arrangement.spacedBy(AppDimen.p24)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1.1f)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(AppDimen.p20)
                            ) {
                                SessionHeaderCard(session = session)
                                SessionInfoSection(session = session)
                                Spacer(modifier = Modifier.height(AppDimen.p12))
                            }

                            Column(
                                modifier = Modifier
                                    .weight(0.9f)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(AppDimen.p20)
                            ) {
                                AppText(
                                    text = stringResource(Res.string.session_checkin_status_title),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )

                                CheckInInteractionSection(
                                    state = state,
                                    session = session,
                                    onCheckInClick = {
                                        screenModel.performCheckIn(session) { msg ->
                                            toastController.show(msg)
                                        }
                                    },
                                    onAttendanceClick = { isCheckedOut ->
                                        navigator.push(
                                            AttendanceScreen(
                                                classId = session.classId,
                                                sessionNumber = session.sessionNumber,
                                                className = session.className,
                                                subjectName = session.subjectName,
                                                readOnly = isCheckedOut
                                            )
                                        )
                                    },
                                    onCheckOutClick = { checkinId ->
                                        screenModel.performCheckOut(checkinId, session) { msg ->
                                            toastController.show(msg)
                                        }
                                    },
                                    onRetry = {
                                        screenModel.loadCheckInStatus(session)
                                    }
                                )
                                Spacer(modifier = Modifier.height(AppDimen.p12))
                            }
                        }
                    } else {
                        // Mobile Stacked Layout
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(AppDimen.p16),
                            verticalArrangement = Arrangement.spacedBy(AppDimen.p16)
                        ) {
                            SessionHeaderCard(session = session)
                            SessionInfoSection(session = session)
                            CheckInInteractionSection(
                                state = state,
                                session = session,
                                onCheckInClick = {
                                    screenModel.performCheckIn(session) { msg ->
                                        toastController.show(msg)
                                    }
                                },
                                onAttendanceClick = { isCheckedOut ->
                                    navigator.push(
                                        AttendanceScreen(
                                            classId = session.classId,
                                            sessionNumber = session.sessionNumber,
                                            className = session.className,
                                            subjectName = session.subjectName,
                                            readOnly = isCheckedOut
                                        )
                                    )
                                },
                                onCheckOutClick = { checkinId ->
                                    screenModel.performCheckOut(checkinId, session) { msg ->
                                        toastController.show(msg)
                                    }
                                },
                                onRetry = {
                                    screenModel.loadCheckInStatus(session)
                                }
                            )
                            Spacer(modifier = Modifier.height(AppDimen.p16))
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun SessionHeaderCard(session: ScheduleSessionUiModel) {
        val now = remember { Clock.System.now() }
        val status = session.getStatus(now)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF475AD7), Color(0xFF8E97FD))
                        )
                    )
                    .padding(AppDimen.p20)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AppDimen.p12)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppText(
                            text = stringResource(
                                Res.string.session_number_prefix,
                                session.sessionNumber
                            ),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        SessionStatusBadge(status = status)
                    }

                    AppText(
                        text = session.subjectName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    AppText(
                        text = stringResource(Res.string.session_class_prefix, session.className),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }

    @Composable
    private fun SessionInfoSection(session: ScheduleSessionUiModel) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AppDimen.p12),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(AppDimen.p20),
                verticalArrangement = Arrangement.spacedBy(AppDimen.p16)
            ) {
                AppText(
                    text = stringResource(Res.string.session_info_title),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimen.p16)
                ) {
                    // Time block
                    InfoBlock(
                        icon = Res.drawable.ic_calendar_month_filled_24dp,
                        iconTint = AppColor.Primary,
                        title = stringResource(Res.string.session_time),
                        value = "${session.startTimeFormatted} - ${session.endTimeFormatted}",
                        modifier = Modifier.weight(1f)
                    )

                    // Room block
                    InfoBlock(
                        icon = Res.drawable.ic_assignment_filled_24dp,
                        iconTint = AppColor.Primary,
                        title = stringResource(Res.string.session_room),
                        value = session.room,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    @Composable
    private fun InfoBlock(
        icon: org.jetbrains.compose.resources.DrawableResource,
        iconTint: Color,
        title: String,
        value: String,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(AppDimen.p12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon(
                drawableRes = icon,
                iconModifier = Modifier.size(20.dp),
                boxModifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconTint.copy(alpha = 0.15f)),
                tint = iconTint
            )
            Spacer(modifier = Modifier.width(AppDimen.p12))
            Column {
                AppText(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AppText(
                    text = value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    @Composable
    private fun CheckInInteractionSection(
        state: SessionDetailState,
        session: ScheduleSessionUiModel,
        onCheckInClick: () -> Unit,
        onAttendanceClick: (isCheckedOut: Boolean) -> Unit,
        onCheckOutClick: (checkinId: Long) -> Unit,
        onRetry: () -> Unit
    ) {
        when (state) {
            is SessionDetailState.Loading -> {
                LoadingStateView()
            }

            is SessionDetailState.Error -> {
                ErrorStateView(message = state.message, onRetry = onRetry)
            }

            is SessionDetailState.NotCheckedIn -> {
                NotCheckedInView(session = session, onCheckInClick = onCheckInClick)
            }

            is SessionDetailState.CheckedIn -> {
                val isCheckedOut = state.checkInInfo.checkedOut == true
                CheckedInView(
                    session = session,
                    checkInInfo = state.checkInInfo,
                    onAttendanceClick = { onAttendanceClick(isCheckedOut) },
                    onCheckOutClick = onCheckOutClick
                )
            }
        }
    }

    @Composable
    private fun LoadingStateView() {
        InfoRowSkeleton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimen.p16),
            rowCount = 3,
            showIcons = true
        )
    }

    @Composable
    private fun ErrorStateView(message: String, onRetry: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AppDimen.p12),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(AppDimen.p20),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
            ) {
                AppText(
                    text = stringResource(Res.string.msg_error_occurred),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                AppText(
                    text = message,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(AppDimen.p4))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    AppText(
                        text = stringResource(Res.string.btn_retry),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    @Composable
    private fun NotCheckedInView(
        session: ScheduleSessionUiModel,
        onCheckInClick: () -> Unit
    ) {
        val now = remember { Clock.System.now() }
        val status = session.getStatus(now)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AppDimen.p12),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(
                width = 1.dp,
                color = if (status == SessionStatus.ONGOING) AppColor.Warning.copy(alpha = 0.7f) else MaterialTheme.colorScheme.outlineVariant.copy(
                    alpha = 0.4f
                )
            )
        ) {
            Column(
                modifier = Modifier.padding(AppDimen.p20),
                verticalArrangement = Arrangement.spacedBy(AppDimen.p16)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimen.p12)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (status == SessionStatus.ONGOING) AppColor.Warning else MaterialTheme.colorScheme.outline)
                    )
                    AppText(
                        text = stringResource(Res.string.session_not_checked_in),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                AppText(
                    text = when (status) {
                        SessionStatus.ONGOING -> stringResource(Res.string.session_desc_ongoing)
                        SessionStatus.UPCOMING -> stringResource(Res.string.session_desc_upcoming)
                        SessionStatus.COMPLETED -> stringResource(Res.string.session_desc_completed)
                    },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )

                if (status == SessionStatus.ONGOING) {
                    AppButton(
                        text = stringResource(Res.string.btn_check_in_now),
                        onClick = onCheckInClick,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun CheckedInView(
        session: ScheduleSessionUiModel,
        checkInInfo: TeacherCheckInResult,
        onAttendanceClick: () -> Unit,
        onCheckOutClick: (checkinId: Long) -> Unit
    ) {
        val isCheckedOut = checkInInfo.checkedOut == true

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AppDimen.p12),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(
                1.dp,
                if (isCheckedOut) AppColor.Primary.copy(alpha = 0.5f) else AppColor.Success.copy(
                    alpha = 0.5f
                )
            )
        ) {
            Column(
                modifier = Modifier.padding(AppDimen.p20),
                verticalArrangement = Arrangement.spacedBy(AppDimen.p20)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimen.p12)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isCheckedOut) AppColor.Primary else AppColor.Success)
                    )
                    AppText(
                        text = if (isCheckedOut) stringResource(Res.string.session_status_checked_out_success) else stringResource(
                            Res.string.session_status_checked_in_success
                        ),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCheckedOut) AppColor.Primary else AppColor.Success
                    )
                }

                // Check-in & Check-out Info table
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                        .border(
                            BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(AppDimen.p16),
                    verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppText(
                            text = stringResource(Res.string.session_label_checkin_time),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        AppText(
                            text = parseDateTimeParts(checkInInfo.checkinTime)?.let { (time, date) ->
                                stringResource(Res.string.date_time_format, time, date)
                            } ?: "--:--",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppText(
                            text = stringResource(Res.string.session_label_checkin_status),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        val isLate = checkInInfo.lateMinutes != null && checkInInfo.lateMinutes > 0
                        AppText(
                            text = if (isLate) stringResource(
                                Res.string.session_status_late_minutes,
                                checkInInfo.lateMinutes
                            ) else stringResource(Res.string.session_status_on_time),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLate) AppColor.Error else AppColor.Success
                        )
                    }

                    if (isCheckedOut) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppText(
                                text = stringResource(Res.string.session_label_checkout_time),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            AppText(
                                text = parseDateTimeParts(checkInInfo.checkoutTime)?.let { (time, date) ->
                                    stringResource(Res.string.date_time_format, time, date)
                                } ?: "--:--",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Actions
                Column(
                    verticalArrangement = Arrangement.spacedBy(AppDimen.p12),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppButton(
                        text = if (isCheckedOut) stringResource(Res.string.btn_view_attendance) else stringResource(
                            Res.string.btn_take_attendance
                        ),
                        onClick = onAttendanceClick,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (!isCheckedOut) {
                        AppButton(
                            text = stringResource(Res.string.btn_check_out_session),
                            onClick = { onCheckOutClick(checkInInfo.checkinId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColor.Error,
                                contentColor = Color.White,
                                disabledContainerColor = AppColor.Error.copy(alpha = 0.5f),
                                disabledContentColor = Color.White.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun SessionStatusBadge(status: SessionStatus) {
        val (text, badgeBg, textColor) = when (status) {
            SessionStatus.COMPLETED -> Triple(
                stringResource(Res.string.session_status_completed),
                Color(0xFFE8F5E9),
                Color(0xFF2E7D32)
            )

            SessionStatus.ONGOING -> Triple(
                stringResource(Res.string.session_status_ongoing),
                Color(0xFFFFF3E0),
                Color(0xFFE65100)
            )

            SessionStatus.UPCOMING -> Triple(
                stringResource(Res.string.session_status_upcoming),
                Color(0xFFECEFF1),
                Color(0xFF37474F)
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(badgeBg)
                .padding(horizontal = AppDimen.p8, vertical = AppDimen.p4),
            contentAlignment = Alignment.Center
        ) {
            AppText(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }

    private fun parseDateTimeParts(dateTimeStr: String?): Pair<String, String>? {
        if (dateTimeStr.isNullOrBlank()) return null
        return try {
            val tIndex = dateTimeStr.indexOf('T')
            if (tIndex != -1) {
                val datePart = dateTimeStr.substring(0, tIndex)
                val timePart =
                    dateTimeStr.substring(tIndex + 1, minOf(tIndex + 9, dateTimeStr.length))
                Pair(timePart, datePart)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}
