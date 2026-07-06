package com.example.educationapp.presentation.screenmodel.attendance

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.asUiText
import com.example.educationapp.domain.enums.AttendanceStatus
import com.example.educationapp.domain.usecase.GetAttendancesUseCase
import com.example.educationapp.domain.usecase.SubmitAttendancesUseCase
import com.example.educationapp.presentation.model.AttendanceUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AttendanceScreenModel(
    private val getAttendancesUseCase: GetAttendancesUseCase,
    private val submitAttendancesUseCase: SubmitAttendancesUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<AttendanceState>(AttendanceState.Loading)
    val state: StateFlow<AttendanceState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun loadAttendances(classId: Long, sessionNumber: Int, isRefresh: Boolean = false) {
        screenModelScope.launch {
            if (isRefresh) {
                _isRefreshing.value = true
            } else {
                _state.value = AttendanceState.Loading
            }
            try {
                when (val result = getAttendancesUseCase(classId, sessionNumber)) {
                    is ApiResult.Success -> {
                        val students = result.data.map { record ->
                            AttendanceUiModel(
                                studentId = record.studentId,
                                studentName = record.studentName,
                                originalStatus = record.status,
                                status = record.status ?: AttendanceStatus.PRESENT,
                                reason = record.note
                            )
                        }
                        _state.value = AttendanceState.Loaded(
                            students = students,
                            hasChanges = false
                        )
                    }
                    is ApiResult.Error -> {
                        _state.value = AttendanceState.Error(result.asUiText())
                    }
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun updateStudentStatus(studentId: Long, newStatus: AttendanceStatus) {
        val currentState = _state.value
        if (currentState is AttendanceState.Loaded) {
            val updatedStudents = currentState.students.map { student ->
                if (student.studentId == studentId) {
                    student.copy(status = newStatus)
                } else {
                    student
                }
            }
            val hasChanges = checkHasChanges(updatedStudents)
            _state.value = currentState.copy(
                students = updatedStudents,
                hasChanges = hasChanges
            )
        }
    }

    fun updateStudentReason(studentId: Long, newReason: String?) {
        val currentState = _state.value
        if (currentState is AttendanceState.Loaded) {
            val updatedStudents = currentState.students.map { student ->
                if (student.studentId == studentId) {
                    student.copy(reason = if (newReason.isNullOrBlank()) null else newReason)
                } else {
                    student
                }
            }
            val hasChanges = checkHasChanges(updatedStudents)
            _state.value = currentState.copy(
                students = updatedStudents,
                hasChanges = hasChanges
            )
        }
    }

    fun saveAttendances(classId: Long, sessionNumber: Int, onToast: (String) -> Unit) {
        val currentState = _state.value
        if (currentState is AttendanceState.Loaded && !currentState.isSaving) {
            _state.value = currentState.copy(isSaving = true)
            screenModelScope.launch {
                val attendancesToSubmit = currentState.students.map { student ->
                    Triple(student.studentId, student.status, student.reason)
                }
                val result = submitAttendancesUseCase(
                    classId = classId,
                    sessionNumber = sessionNumber,
                    attendances = attendancesToSubmit
                )
                when (result) {
                    is ApiResult.Success -> {
                        onToast("Lưu điểm danh thành công!")
                        _state.value = AttendanceState.Saved
                    }
                    is ApiResult.Error -> {
                        onToast(result.message ?: "Lưu điểm danh thất bại. Vui lòng thử lại.")
                        _state.value = currentState.copy(isSaving = false)
                    }
                }
            }
        }
    }

    private fun checkHasChanges(students: List<AttendanceUiModel>): Boolean {
        return students.any { student ->
            val hasStatusChanged = student.status != (student.originalStatus ?: AttendanceStatus.PRESENT)
            val hasReasonChanged = student.reason != null // simple check for now: if a reason was input
            hasStatusChanged || hasReasonChanged
        }
    }
}
