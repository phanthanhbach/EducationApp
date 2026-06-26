package com.example.educationapp.presentation.screenmodel.dashboard

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.UiText
import com.example.educationapp.core.util.asUiText
import com.example.educationapp.domain.entity.TeacherCheckInResult
import com.example.educationapp.domain.entity.TeacherRatingSummary
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.domain.usecase.GetMyProfileUseCase
import com.example.educationapp.domain.usecase.GetMySchedulesUseCase
import com.example.educationapp.domain.usecase.GetTeacherCheckInsUseCase
import com.example.educationapp.domain.usecase.GetTeacherRatingSummaryUseCase
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleSessionUiModel
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.error_unknown
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

sealed interface TeacherDashboardState {
    object Loading : TeacherDashboardState
    data class Success(
        val ratingSummary: TeacherRatingSummary,
        val upcomingSchedules: List<ScheduleSessionUiModel>,
        val totalCheckIns: Int,
        val recentCheckIns: List<TeacherCheckInResult>
    ) : TeacherDashboardState

    data class Error(val error: UiText) : TeacherDashboardState
}

class TeacherDashboardScreenModel(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val getTeacherRatingSummaryUseCase: GetTeacherRatingSummaryUseCase,
    private val getMySchedulesUseCase: GetMySchedulesUseCase,
    private val getTeacherCheckInsUseCase: GetTeacherCheckInsUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<TeacherDashboardState>(TeacherDashboardState.Loading)
    val state: StateFlow<TeacherDashboardState> = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        screenModelScope.launch {
            _state.value = TeacherDashboardState.Loading

            // 1. Get profile to find teacherId
            when (val profileResult = getMyProfileUseCase(AppRole.TEACHER)) {
                is ApiResult.Error -> {
                    _state.value = TeacherDashboardState.Error(profileResult.asUiText())
                }

                is ApiResult.Success -> {
                    val profile = profileResult.data
                    if (profile is UserProfile.Teacher) {
                        val teacherId = profile.teacherId.toLong()

                        // Calculate dates (From now to 2 days later 23:59:59)
                        val now =
                            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        val today = now.date
                        val dayPlus2 = LocalDate.fromEpochDays(today.toEpochDays() + 2)

                        val fromTime = "${now.date}T" +
                                "${now.hour.toString().padStart(2, '0')}:" +
                                "${now.minute.toString().padStart(2, '0')}:" +
                                "${now.second.toString().padStart(2, '0')}"
                        val toTime = "${dayPlus2}T23:59:59"

                        // Fetch rating summary, schedule, and check-ins in parallel
                        val ratingDeferred = async { getTeacherRatingSummaryUseCase(teacherId) }
                        val scheduleDeferred = async { getMySchedulesUseCase(fromTime, toTime) }
                        val checkInDeferred =
                            async { getTeacherCheckInsUseCase(teacherId, page = 0, size = 3) }

                        val ratingResult = ratingDeferred.await()
                        val scheduleResult = scheduleDeferred.await()
                        val checkInResult = checkInDeferred.await()

                        if (ratingResult is ApiResult.Success && scheduleResult is ApiResult.Success && checkInResult is ApiResult.Success) {
                            // Map schedules DTO to ScheduleSessionUiModel
                            val uiSchedules = scheduleResult.data.map { item ->
                                val date = try {
                                    LocalDateTime.parse(item.startTime).date
                                } catch (e: Exception) {
                                    LocalDate(2026, 6, 1)
                                }

                                val roomText =
                                    if (item.roomName.isNotBlank() && item.roomName != "string") {
                                        item.roomName
                                    } else {
                                        "Phòng ${item.roomId}"
                                    }

                                ScheduleSessionUiModel(
                                    id = "${item.classId}_${item.sessionNumber}_${item.startTime}",
                                    classId = item.classId,
                                    sessionNumber = item.sessionNumber,
                                    subjectName = if (!item.notes.isNullOrBlank() && item.notes != "string") item.notes else item.className,
                                    className = item.className,
                                    room = roomText,
                                    startTimeRaw = item.startTime,
                                    endTimeRaw = item.endTime,
                                    attendanceText = "",
                                    date = date
                                )
                            }.sortedBy { it.startTimeRaw } // Sort by startTime chronological

                            val checkInPage = checkInResult.data
                            _state.value = TeacherDashboardState.Success(
                                ratingSummary = ratingResult.data,
                                upcomingSchedules = uiSchedules,
                                totalCheckIns = checkInPage.totalElements,
                                recentCheckIns = checkInPage.content
                            )
                        } else {
                            val firstError = listOf(ratingResult, scheduleResult, checkInResult)
                                .filterIsInstance<ApiResult.Error>()
                                .firstOrNull()

                            _state.value = TeacherDashboardState.Error(
                                firstError?.asUiText()
                                    ?: UiText.ResourceString(Res.string.error_unknown)
                            )
                        }
                    } else {
                        _state.value = TeacherDashboardState.Error(
                            UiText.DynamicString("Tài khoản không phải giáo viên.")
                        )
                    }
                }
            }
        }
    }
}
