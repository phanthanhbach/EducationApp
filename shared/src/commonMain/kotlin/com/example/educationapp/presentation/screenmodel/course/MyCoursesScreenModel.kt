package com.example.educationapp.presentation.screenmodel.course

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.Course
import com.example.educationapp.domain.repository.StudentDashboardRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MyCoursesState {
    object Loading : MyCoursesState
    data class Success(
        val courses: List<Course>,
        val currentPage: Int,
        val totalPages: Int,
        val totalElements: Int,
        val hasNextPage: Boolean
    ) : MyCoursesState
    data class Error(val message: String) : MyCoursesState
}

class MyCoursesScreenModel(
    private val dashboardRepository: StudentDashboardRepository
) : ScreenModel {

    private val _state = MutableStateFlow<MyCoursesState>(MyCoursesState.Loading)
    val state: StateFlow<MyCoursesState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatus = MutableStateFlow<Boolean?>(null) // null for All, true for Active, false for Inactive
    val selectedStatus: StateFlow<Boolean?> = _selectedStatus.asStateFlow()

    private var fetchJob: Job? = null

    fun loadCourses(append: Boolean = false) {
        val currentState = _state.value
        val page = if (append && currentState is MyCoursesState.Success) {
            if (!currentState.hasNextPage) return
            currentState.currentPage + 1
        } else {
            0
        }

        if (append && fetchJob?.isActive == true) return
        fetchJob?.cancel()

        fetchJob = screenModelScope.launch {
            if (!append) {
                _state.value = MyCoursesState.Loading
            }

            val result = dashboardRepository.getMyCourses(
                search = _searchQuery.value.takeIf { it.isNotBlank() },
                isActive = _selectedStatus.value,
                page = page,
                size = 20
            )

            when (result) {
                is ApiResult.Error -> {
                    if (!append) {
                        _state.value = MyCoursesState.Error(result.message ?: "Lỗi tải danh sách khóa học.")
                    }
                }
                is ApiResult.Success -> {
                    val pagination = result.data
                    val currentCourses = if (append && _state.value is MyCoursesState.Success) {
                        (_state.value as MyCoursesState.Success).courses + pagination.content
                    } else {
                        pagination.content
                    }

                    _state.value = MyCoursesState.Success(
                        courses = currentCourses,
                        currentPage = pagination.number,
                        totalPages = pagination.totalPages,
                        totalElements = pagination.totalElements,
                        hasNextPage = !pagination.last && pagination.content.isNotEmpty()
                    )
                }
            }
        }
    }

    fun searchCourses(query: String) {
        _searchQuery.value = query
        loadCourses(append = false)
    }

    fun filterByStatus(isActive: Boolean?) {
        _selectedStatus.value = isActive
        loadCourses(append = false)
    }
}
