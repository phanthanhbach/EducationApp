package com.example.educationapp.presentation.screen.session.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.button.AppButton
import com.example.educationapp.core.ui.shimmer.skeleton.InfoRowSkeleton
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.util.DateTimeFormatter
import com.example.educationapp.domain.entity.TeacherCheckInResult
import com.example.educationapp.domain.enums.SessionStatus
import com.example.educationapp.presentation.model.ScheduleSessionUiModel
import com.example.educationapp.presentation.screenmodel.session_detail.SessionDetailState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.btn_check_in_now
import educationapp.shared.generated.resources.btn_check_out_session
import educationapp.shared.generated.resources.btn_retry
import educationapp.shared.generated.resources.btn_take_attendance
import educationapp.shared.generated.resources.btn_view_attendance
import educationapp.shared.generated.resources.date_time_format
import educationapp.shared.generated.resources.msg_error_occurred
import educationapp.shared.generated.resources.session_desc_completed
import educationapp.shared.generated.resources.session_desc_ongoing
import educationapp.shared.generated.resources.session_desc_upcoming
import educationapp.shared.generated.resources.session_label_checkin_status
import educationapp.shared.generated.resources.session_label_checkin_time
import educationapp.shared.generated.resources.session_label_checkout_time
import educationapp.shared.generated.resources.session_not_checked_in
import educationapp.shared.generated.resources.session_status_checked_in_success
import educationapp.shared.generated.resources.session_status_checked_out_success
import educationapp.shared.generated.resources.session_status_late_minutes
import educationapp.shared.generated.resources.session_status_on_time
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

@Composable
fun CheckInInteractionSection(
    state: SessionDetailState,
    session: ScheduleSessionUiModel,
    onCheckInClick: () -> Unit,
    onAttendanceClick: (isCheckedOut: Boolean) -> Unit,
    onCheckOutClick: (checkInId: Long) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is SessionDetailState.Loading -> {
            LoadingStateView()
        }

        is SessionDetailState.Error -> {
            ErrorStateView(message = state.message.asString(), onRetry = onRetry)
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
        shape = RoundedCornerShape(AppDimen.p16),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
        ),
        border = BorderStroke(AppDimen.p1, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
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
                shape = RoundedCornerShape(AppDimen.p8)
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
        shape = RoundedCornerShape(AppDimen.p16),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimen.p2),
        border = BorderStroke(
            width = AppDimen.p1,
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
                        .size(AppDimen.p12)
                        .clip(RoundedCornerShape(AppDimen.p6))
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
                    modifier = Modifier.fillMaxWidth().height(AppDimen.p50)
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
    onCheckOutClick: (checkInId: Long) -> Unit
) {
    val isCheckedOut = checkInInfo.checkedOut == true

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = AppDimen.p12),
        shape = RoundedCornerShape(AppDimen.p16),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimen.p2),
        border = BorderStroke(
            AppDimen.p1,
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
                        .size(AppDimen.p12)
                        .clip(RoundedCornerShape(AppDimen.p6))
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
                    .clip(RoundedCornerShape(AppDimen.p12))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    .border(
                        BorderStroke(
                            AppDimen.p1,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(AppDimen.p12)
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
                        text = DateTimeFormatter.parseDateTimeParts(checkInInfo.checkinTime)
                            ?.let { (time, date) ->
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
                            text = DateTimeFormatter.parseDateTimeParts(checkInInfo.checkoutTime)
                                ?.let { (time, date) ->
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
