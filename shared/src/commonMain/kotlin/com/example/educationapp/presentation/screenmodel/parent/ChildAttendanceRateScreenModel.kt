package com.example.educationapp.presentation.screenmodel.parent

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.asUiText
import com.example.educationapp.domain.entity.AttendanceRate
import com.example.educationapp.domain.entity.SchoolClass
import com.example.educationapp.domain.usecase.GetAttendanceRateUseCase
import com.example.educationapp.domain.usecase.GetStudentClassesNoPaginationUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ChildAttendanceRateScreenModel(
    private val studentId: Long,
    private val getStudentClassesNoPaginationUseCase: GetStudentClassesNoPaginationUseCase,
    private val getAttendanceRateUseCase: GetAttendanceRateUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<ChildAttendanceRateState>(ChildAttendanceRateState.Loading)
    val state: StateFlow<ChildAttendanceRateState> = _state.asStateFlow()

    private val _selectedClass = MutableStateFlow<SchoolClass?>(null)
    val selectedClass: StateFlow<SchoolClass?> = _selectedClass.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        screenModelScope.launch {
            _state.value = ChildAttendanceRateState.Loading
            when (val classesResult = getStudentClassesNoPaginationUseCase(studentId)) {
                is ApiResult.Error -> {
                    _state.value = ChildAttendanceRateState.Error(classesResult.asUiText())
                }
                is ApiResult.Success -> {
                    val classes = classesResult.data
                    if (classes.isEmpty()) {
                        _state.value = ChildAttendanceRateState.Success(
                            classes = emptyList(),
                            rates = emptyMap(),
                            summaryTotal = 0,
                            summaryAttended = 0,
                            summaryAbsent = 0,
                            summaryRate = 0.0
                        )
                        _selectedClass.value = null
                        return@launch
                    }

                    // Fetch attendance rates in parallel for all student classes
                    val deferreds = classes.map { clazz ->
                        async {
                            try {
                                val result = getAttendanceRateUseCase(studentId.toInt(), clazz.id.toInt())
                                clazz.id to result
                            } catch (e: Exception) {
                                clazz.id to ApiResult.Error.UnknownError(
                                    message = e.message ?: "Lỗi tải tỉ lệ tham gia",
                                    exception = e
                                )
                            }
                        }
                    }

                    val results = deferreds.awaitAll()
                    val ratesMap = mutableMapOf<Long, AttendanceRate>()
                    results.forEach { (classId, result) ->
                        if (result is ApiResult.Success) {
                            ratesMap[classId] = result.data
                        }
                    }

                    var total = 0
                    var attended = 0
                    ratesMap.values.forEach { rate ->
                        total += rate.totalSessions
                        attended += rate.attendedSessions
                    }
                    val absent = total - attended
                    val overallRate = if (total > 0) (attended.toDouble() / total) * 100.0 else 0.0

                    _state.value = ChildAttendanceRateState.Success(
                        classes = classes,
                        rates = ratesMap,
                        summaryTotal = total,
                        summaryAttended = attended,
                        summaryAbsent = absent,
                        summaryRate = overallRate
                    )
                    _selectedClass.value = classes.firstOrNull()
                }
            }
        }
    }

    fun selectClass(schoolClass: SchoolClass) {
        _selectedClass.value = schoolClass
    }
}
