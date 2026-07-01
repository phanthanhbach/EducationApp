package com.example.educationapp.presentation.screenmodel.schedule

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.TeacherCheckInResult
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.domain.usecase.GetCheckInStatusUseCase
import com.example.educationapp.domain.usecase.GetMyProfileUseCase
import com.example.educationapp.domain.usecase.TeacherCheckInUseCase
import com.example.educationapp.domain.usecase.TeacherCheckOutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SessionDetailState {
    object Loading : SessionDetailState
    data class NotCheckedIn(val session: ScheduleSessionUiModel) : SessionDetailState
    data class CheckedIn(
        val session: ScheduleSessionUiModel,
        val checkInInfo: TeacherCheckInResult
    ) : SessionDetailState
    data class Error(val message: String) : SessionDetailState
}

class SessionDetailScreenModel(
    private val getCheckInStatusUseCase: GetCheckInStatusUseCase,
    private val teacherCheckInUseCase: TeacherCheckInUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val teacherCheckOutUseCase: TeacherCheckOutUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<SessionDetailState>(SessionDetailState.Loading)
    val state: StateFlow<SessionDetailState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun loadCheckInStatus(session: ScheduleSessionUiModel, isRefresh: Boolean = false) {
        screenModelScope.launch {
            if (isRefresh) {
                _isRefreshing.value = true
            } else {
                _state.value = SessionDetailState.Loading
            }
            try {
                when (val profileResult = getMyProfileUseCase(AppRole.TEACHER)) {
                    is ApiResult.Error -> {
                        _state.value = SessionDetailState.Error(
                            profileResult.message ?: "Không thể lấy thông tin profile giáo viên."
                        )
                    }
                    is ApiResult.Success -> {
                        val profile = profileResult.data
                        if (profile is UserProfile.Teacher) {
                            val teacherId = profile.teacherId.toLong()
                            val result = getCheckInStatusUseCase(
                                classId = session.classId,
                                sessionNumber = session.sessionNumber,
                                teacherId = teacherId
                            )
                            when (result) {
                                is ApiResult.Success -> {
                                    _state.value = SessionDetailState.CheckedIn(
                                        session = session,
                                        checkInInfo = result.data
                                    )
                                }
                                is ApiResult.Error -> {
                                    if (result is ApiResult.Error.HttpError && result.code == 404) {
                                        _state.value = SessionDetailState.NotCheckedIn(session)
                                    } else {
                                        _state.value = SessionDetailState.Error(
                                            result.message ?: "Đã có lỗi xảy ra khi lấy trạng thái check-in."
                                        )
                                    }
                                }
                            }
                        } else {
                            _state.value = SessionDetailState.Error("Tài khoản không phải giáo viên.")
                        }
                    }
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun performCheckIn(session: ScheduleSessionUiModel, onToast: (String) -> Unit = {}) {
        screenModelScope.launch {
            _state.value = SessionDetailState.Loading
            when (val profileResult = getMyProfileUseCase(AppRole.TEACHER)) {
                is ApiResult.Error -> {
                    onToast(profileResult.message ?: "Không thể lấy thông tin profile giáo viên.")
                    _state.value = SessionDetailState.NotCheckedIn(session)
                }
                is ApiResult.Success -> {
                    val profile = profileResult.data
                    if (profile is UserProfile.Teacher) {
                        val teacherId = profile.teacherId.toLong()
                        val result = teacherCheckInUseCase(
                            teacherId = teacherId,
                            classId = session.classId,
                            sessionNumber = session.sessionNumber
                        )
                        when (result) {
                            is ApiResult.Success -> {
                                onToast("Check-in thành công!")
                                _state.value = SessionDetailState.CheckedIn(
                                    session = session,
                                    checkInInfo = result.data
                                )
                            }
                            is ApiResult.Error -> {
                                val errMsg = result.message ?: "Check-in thất bại. Vui lòng thử lại."
                                onToast(errMsg)
                                _state.value = SessionDetailState.NotCheckedIn(session)
                            }
                        }
                    } else {
                        onToast("Tài khoản không phải giáo viên.")
                        _state.value = SessionDetailState.NotCheckedIn(session)
                    }
                }
            }
        }
    }

    fun performCheckOut(checkinId: Long, session: ScheduleSessionUiModel, onToast: (String) -> Unit = {}) {
        screenModelScope.launch {
            _state.value = SessionDetailState.Loading
            val result = teacherCheckOutUseCase(checkinId)
            when (result) {
                is ApiResult.Success -> {
                    onToast("Check-out thành công!")
                    _state.value = SessionDetailState.CheckedIn(
                        session = session,
                        checkInInfo = result.data
                    )
                }
                is ApiResult.Error -> {
                    val errMsg = result.message ?: "Check-out thất bại. Vui lòng thử lại."
                    onToast(errMsg)
                    // Reload status to get back to correct CheckedIn/NotCheckedIn state
                    loadCheckInStatus(session)
                }
            }
        }
    }
}
