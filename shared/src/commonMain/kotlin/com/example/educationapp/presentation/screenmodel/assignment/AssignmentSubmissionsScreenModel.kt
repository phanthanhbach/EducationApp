package com.example.educationapp.presentation.screenmodel.assignment

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.asUiText
import com.example.educationapp.domain.usecase.FilterAssignmentSubmissionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AssignmentSubmissionsScreenModel(
    private val filterAssignmentSubmissionsUseCase: FilterAssignmentSubmissionsUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<AssignmentSubmissionsState>(AssignmentSubmissionsState.Loading)
    val state: StateFlow<AssignmentSubmissionsState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _submittedFilter = MutableStateFlow<Boolean>(true)
    val submittedFilter: StateFlow<Boolean> = _submittedFilter.asStateFlow()

    private var assignmentId: Int? = null
    private var classId: Int? = null
    private var isLoadingNextPage = false

    fun loadSubmissions(assignmentId: Int, classId: Int) {
        this.assignmentId = assignmentId
        this.classId = classId
        screenModelScope.launch {
            fetchSubmissions(page = 0, append = false)
        }
    }

    fun setFilter(submitted: Boolean) {
        if (_submittedFilter.value == submitted) return
        _submittedFilter.value = submitted
        screenModelScope.launch {
            fetchSubmissions(page = 0, append = false)
        }
    }

    fun retry() {
        screenModelScope.launch {
            fetchSubmissions(page = 0, append = false)
        }
    }

    fun loadNextPage() {
        val currentState = _state.value
        if (currentState is AssignmentSubmissionsState.Success && currentState.hasNextPage && !isLoadingNextPage) {
            screenModelScope.launch {
                fetchSubmissions(page = currentState.currentPage + 1, append = true)
            }
        }
    }

    fun refreshData() {
        _isRefreshing.value = true
        screenModelScope.launch {
            fetchSubmissions(page = 0, append = false, silent = true)
            _isRefreshing.value = false
        }
    }

    private suspend fun fetchSubmissions(page: Int, append: Boolean, silent: Boolean = false) {
        val currentAssignmentId = assignmentId ?: return
        val currentClassId = classId ?: return

        if (append) {
            isLoadingNextPage = true
        } else if (!silent) {
            _state.value = AssignmentSubmissionsState.Loading
        }

        val result = filterAssignmentSubmissionsUseCase(
            assignmentId = currentAssignmentId,
            classId = currentClassId,
            submitted = _submittedFilter.value,
            page = page,
            size = 20
        )

        when (result) {
            is ApiResult.Error -> {
                if (!append) {
                    _state.value = AssignmentSubmissionsState.Error(result.asUiText())
                }
            }
            is ApiResult.Success -> {
                val pagination = result.data
                val currentSubmissions = if (append && _state.value is AssignmentSubmissionsState.Success) {
                    (_state.value as AssignmentSubmissionsState.Success).submissions + pagination.content
                } else {
                    pagination.content
                }

                _state.value = AssignmentSubmissionsState.Success(
                    submissions = currentSubmissions,
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
