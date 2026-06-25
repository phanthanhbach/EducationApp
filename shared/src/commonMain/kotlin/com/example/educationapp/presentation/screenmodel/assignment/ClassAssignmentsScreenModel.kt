package com.example.educationapp.presentation.screenmodel.assignment

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.Assignment
import com.example.educationapp.domain.usecase.FilterAssignmentsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ClassAssignmentsState {
    object Loading : ClassAssignmentsState
    data class Success(
        val assignments: List<Assignment>,
        val currentPage: Int,
        val totalPages: Int,
        val totalElements: Int,
        val hasNextPage: Boolean
    ) : ClassAssignmentsState
    data class Error(val message: String) : ClassAssignmentsState
}

class ClassAssignmentsScreenModel(
    private val filterAssignmentsUseCase: FilterAssignmentsUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<ClassAssignmentsState>(ClassAssignmentsState.Loading)
    val state: StateFlow<ClassAssignmentsState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var classId: Int? = null
    private var isLoadingNextPage = false

    fun loadAssignments(classId: Int) {
        this.classId = classId
        screenModelScope.launch {
            fetchAssignments(page = 0, append = false)
        }
    }

    fun retry() {
        classId?.let {
            screenModelScope.launch {
                fetchAssignments(page = 0, append = false)
            }
        }
    }

    fun loadNextPage() {
        val currentState = _state.value
        if (currentState is ClassAssignmentsState.Success && currentState.hasNextPage && !isLoadingNextPage) {
            screenModelScope.launch {
                fetchAssignments(page = currentState.currentPage + 1, append = true)
            }
        }
    }

    fun refreshData() {
        _isRefreshing.value = true
        screenModelScope.launch {
            fetchAssignments(page = 0, append = false, silent = true)
            _isRefreshing.value = false
        }
    }

    private suspend fun fetchAssignments(page: Int, append: Boolean, silent: Boolean = false) {
        val currentClassId = classId ?: return
        if (append) {
            isLoadingNextPage = true
        } else if (!silent) {
            _state.value = ClassAssignmentsState.Loading
        }

        when (val result = filterAssignmentsUseCase(currentClassId, page, 20)) {
            is ApiResult.Error -> {
                if (!append) {
                    _state.value = ClassAssignmentsState.Error(
                        result.message ?: "Lỗi tải danh sách bài tập."
                    )
                }
            }
            is ApiResult.Success -> {
                val pagination = result.data
                val currentAssignments = if (append && _state.value is ClassAssignmentsState.Success) {
                    (_state.value as ClassAssignmentsState.Success).assignments + pagination.content
                } else {
                    pagination.content
                }

                _state.value = ClassAssignmentsState.Success(
                    assignments = currentAssignments,
                    currentPage = pagination.number,
                    totalPages = pagination.totalPages,
                    totalElements = pagination.totalElements,
                    hasNextPage = !pagination.last && pagination.content.isNotEmpty()
                )
            }
        }

        isLoadingNextPage = false
    }
}
