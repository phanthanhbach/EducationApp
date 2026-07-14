package com.example.educationapp.presentation.screenmodel.parent

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.asUiText
import com.example.educationapp.domain.entity.AttendanceRate
import com.example.educationapp.domain.entity.SchoolClass
import com.example.educationapp.domain.usecase.GetAttendanceRateUseCase
import com.example.educationapp.domain.usecase.GetStudentClassesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChildAttendanceRateScreenModel(
    private val studentId: Long,
    private val getStudentClassesUseCase: GetStudentClassesUseCase,
    private val getAttendanceRateUseCase: GetAttendanceRateUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<ChildAttendanceRateState>(ChildAttendanceRateState.Loading)
    val state: StateFlow<ChildAttendanceRateState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var fetchJob: Job? = null

    init {
        loadData(append = false)
    }

    fun searchClasses(query: String) {
        _searchQuery.value = query
        loadData(append = false)
    }

    fun loadData(append: Boolean, silent: Boolean = false) {
        if (append && fetchJob?.isActive == true) {
            return
        }
        fetchJob?.cancel()
        fetchJob = screenModelScope.launch {
            val currentState = _state.value
            val nextPage = if (append && currentState is ChildAttendanceRateState.Success) {
                currentState.currentPage + 1
            } else {
                0
            }

            if (!append && !silent) {
                _state.value = ChildAttendanceRateState.Loading
            } else if (append && currentState is ChildAttendanceRateState.Success) {
                _state.value = currentState.copy(isLoadingMore = true)
            }

            val classesResult = getStudentClassesUseCase(
                studentId = studentId,
                status = null,
                page = nextPage,
                size = 10
            )

            when (classesResult) {
                is ApiResult.Error -> {
                    if (!append) {
                        _state.value = ChildAttendanceRateState.Error(classesResult.asUiText())
                    } else if (currentState is ChildAttendanceRateState.Success) {
                        _state.value = currentState.copy(isLoadingMore = false)
                    }
                }
                is ApiResult.Success -> {
                    val pagination = classesResult.data
                    val newClasses = pagination.content

                    val query = _searchQuery.value
                    val filteredNewClasses = if (query.isNotBlank()) {
                        newClasses.filter { schoolClass ->
                            schoolClass.name.contains(query, ignoreCase = true) ||
                                    schoolClass.courseName.contains(query, ignoreCase = true)
                        }
                    } else {
                        newClasses
                    }

                    val currentSuccessState = _state.value as? ChildAttendanceRateState.Success
                    val previousClasses = if (append && currentSuccessState != null) {
                        currentSuccessState.classes
                    } else {
                        emptyList()
                    }
                    val accumulatedClasses = previousClasses + filteredNewClasses

                    if (accumulatedClasses.isEmpty()) {
                        _state.value = ChildAttendanceRateState.Success(
                            classes = emptyList(),
                            rates = emptyMap(),
                            summaryTotal = 0,
                            summaryAttended = 0,
                            summaryAbsent = 0,
                            summaryRate = 0.0,
                            currentPage = pagination.number,
                            totalPages = pagination.totalPages,
                            hasNextPage = !pagination.last && pagination.content.isNotEmpty(),
                            isLoadingMore = false
                        )
                        return@launch
                    }

                    val previousRates = if (append && currentSuccessState != null) {
                        currentSuccessState.rates
                    } else {
                        emptyMap()
                    }

                    // Only fetch attendance rates for new classes we haven't fetched yet
                    val classesToFetch = filteredNewClasses.filter { it.id !in previousRates }

                    val deferreds = classesToFetch.map { clazz ->
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

                    val fetchResults = deferreds.awaitAll()
                    val updatedRatesMap = previousRates.toMutableMap()
                    fetchResults.forEach { (classId, result) ->
                        if (result is ApiResult.Success) {
                            updatedRatesMap[classId] = result.data
                        }
                    }

                    var total = 0
                    var attended = 0
                    updatedRatesMap.values.forEach { rate ->
                        total += rate.totalSessions
                        attended += rate.attendedSessions
                    }
                    val absent = total - attended
                    val overallRate = if (total > 0) (attended.toDouble() / total) * 100.0 else 0.0

                    _state.value = ChildAttendanceRateState.Success(
                        classes = accumulatedClasses,
                        rates = updatedRatesMap,
                        summaryTotal = total,
                        summaryAttended = attended,
                        summaryAbsent = absent,
                        summaryRate = overallRate,
                        currentPage = pagination.number,
                        totalPages = pagination.totalPages,
                        hasNextPage = !pagination.last && pagination.content.isNotEmpty(),
                        isLoadingMore = false
                    )
                }
            }
        }
    }

    fun refreshData() {
        if (fetchJob?.isActive == true) return
        fetchJob?.cancel()
        fetchJob = screenModelScope.launch {
            _isRefreshing.value = true
            loadData(append = false, silent = true)
            _isRefreshing.value = false
        }
    }
}
