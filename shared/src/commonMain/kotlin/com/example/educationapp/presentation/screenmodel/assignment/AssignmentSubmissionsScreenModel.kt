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

import com.example.educationapp.domain.usecase.GradeSubmissionUseCase
import com.example.educationapp.domain.entity.SubmissionDetail
import com.example.educationapp.domain.enums.AssignmentFilter

class AssignmentSubmissionsScreenModel(
    private val filterAssignmentSubmissionsUseCase: FilterAssignmentSubmissionsUseCase,
    private val gradeSubmissionUseCase: GradeSubmissionUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<AssignmentSubmissionsState>(AssignmentSubmissionsState.Loading)
    val state: StateFlow<AssignmentSubmissionsState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _submittedFilter = MutableStateFlow(AssignmentFilter.SUBMITTED)
    val submittedFilter: StateFlow<AssignmentFilter> = _submittedFilter.asStateFlow()

    private val _gradeState = MutableStateFlow<GradeDialogState>(GradeDialogState.Idle)
    val gradeState: StateFlow<GradeDialogState> = _gradeState.asStateFlow()

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

    fun setFilter(filter: AssignmentFilter) {
        if (_submittedFilter.value == filter) return
        _submittedFilter.value = filter
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
            submitted = _submittedFilter.value.toBoolean(),
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

    fun showGradeDialog(submission: SubmissionDetail) {
        _gradeState.value = GradeDialogState.Visible(submission = submission)
    }

    fun dismissGradeDialog() {
        _gradeState.value = GradeDialogState.Idle
    }

    fun gradeSubmission(score: Double, comment: String) {
        val currentState = _gradeState.value
        if (currentState !is GradeDialogState.Visible) return

        _gradeState.value = currentState.copy(isLoading = true, errorMessage = null)

        screenModelScope.launch {
            val result = gradeSubmissionUseCase(
                classId = currentState.submission.classId,
                studentId = currentState.submission.studentId,
                assignmentId = currentState.submission.assignmentId,
                score = score,
                comment = comment
            )

            when (result) {
                is ApiResult.Error -> {
                    _gradeState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = result.asUiText()
                    )
                }
                is ApiResult.Success -> {
                    val graded = result.data
                    // Update submissions list
                    val listState = _state.value
                    if (listState is AssignmentSubmissionsState.Success) {
                        val updatedList = listState.submissions.map {
                            if (it.studentId == graded.studentId && it.assignmentId == graded.assignmentId) {
                                it.copy(
                                    score = graded.score,
                                    teacherComment = graded.teacherComment,
                                    submitted = true,
                                    submissionStatus = graded.status ?: it.submissionStatus
                                )
                            } else {
                                it
                            }
                        }
                        _state.value = listState.copy(submissions = updatedList)
                    }
                    _gradeState.value = GradeDialogState.Idle
                }
            }
        }
    }
}
