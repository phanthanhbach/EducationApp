package com.example.educationapp.presentation.screenmodel.assignment

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.file.UploadFile
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.StudentAssignment
import com.example.educationapp.domain.usecase.GetMyAssignmentsFilteredUseCase
import com.example.educationapp.domain.usecase.SubmitAssignmentUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface StudentClassAssignmentsState {
    object Loading : StudentClassAssignmentsState
    data class Success(
        val assignments: List<StudentAssignment>,
        val currentPage: Int,
        val totalPages: Int,
        val totalElements: Int,
        val hasNextPage: Boolean
    ) : StudentClassAssignmentsState
    data class Error(val message: String) : StudentClassAssignmentsState
}

class StudentClassAssignmentsScreenModel(
    private val getMyAssignmentsFilteredUseCase: GetMyAssignmentsFilteredUseCase,
    private val submitAssignmentUseCase: SubmitAssignmentUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<StudentClassAssignmentsState>(StudentClassAssignmentsState.Loading)
    val state: StateFlow<StudentClassAssignmentsState> = _state.asStateFlow()

    private val _submittedFilter = MutableStateFlow(false)
    val submittedFilter: StateFlow<Boolean> = _submittedFilter.asStateFlow()

    private val _submittingAssignmentIds = MutableStateFlow<Set<Int>>(emptySet())
    val submittingAssignmentIds: StateFlow<Set<Int>> = _submittingAssignmentIds.asStateFlow()

    private var classId: Int? = null
    private var isLoadingNextPage = false

    fun init(classId: Int) {
        if (this.classId == classId) return
        this.classId = classId
        loadAssignments(submitted = _submittedFilter.value, append = false)
    }

    fun setSubmittedFilter(submitted: Boolean) {
        if (_submittedFilter.value == submitted) return
        _submittedFilter.value = submitted
        loadAssignments(submitted = submitted, append = false)
    }

    fun retry() {
        loadAssignments(submitted = _submittedFilter.value, append = false)
    }

    fun loadNextPage() {
        val currentState = _state.value
        if (currentState is StudentClassAssignmentsState.Success && currentState.hasNextPage && !isLoadingNextPage) {
            val nextPage = currentState.currentPage + 1
            loadAssignments(submitted = _submittedFilter.value, append = true, page = nextPage)
        }
    }

    fun submitAssignment(
        assignment: StudentAssignment,
        file: UploadFile,
        onResult: (Boolean, String) -> Unit
    ) {
        if (assignment.assignmentId in _submittingAssignmentIds.value) return

        screenModelScope.launch {
            _submittingAssignmentIds.value = _submittingAssignmentIds.value + assignment.assignmentId

            when (
                val result = submitAssignmentUseCase(
                    assignmentId = assignment.assignmentId,
                    classId = assignment.classId,
                    studentId = assignment.studentId,
                    file = file
                )
            ) {
                is ApiResult.Error -> {
                    onResult(false, result.message ?: "Không thể nộp bài.")
                }
                is ApiResult.Success -> {
                    onResult(true, "Đã nộp bài ${result.data.assignmentTitle}.")
                    loadAssignments(submitted = _submittedFilter.value, append = false)
                }
            }

            _submittingAssignmentIds.value = _submittingAssignmentIds.value - assignment.assignmentId
        }
    }

    private fun loadAssignments(submitted: Boolean, append: Boolean, page: Int = 0) {
        val currentClassId = classId ?: return
        screenModelScope.launch {
            if (append) {
                isLoadingNextPage = true
            } else {
                _state.value = StudentClassAssignmentsState.Loading
            }

            when (val result = getMyAssignmentsFilteredUseCase(currentClassId, submitted, page)) {
                is ApiResult.Error -> {
                    if (!append) {
                        _state.value = StudentClassAssignmentsState.Error(
                            result.message ?: "Không thể tải danh sách bài tập."
                        )
                    }
                }
                is ApiResult.Success -> {
                    val pagination = result.data
                    val currentList = if (append && _state.value is StudentClassAssignmentsState.Success) {
                        (_state.value as StudentClassAssignmentsState.Success).assignments + pagination.content
                    } else {
                        pagination.content
                    }

                    _state.value = StudentClassAssignmentsState.Success(
                        assignments = currentList,
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
}
