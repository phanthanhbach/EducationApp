package com.example.educationapp.presentation.screenmodel.feedback

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.StudentClassFeedback
import com.example.educationapp.domain.usecase.GetStudentFeedbackUseCase
import com.example.educationapp.domain.usecase.SubmitStudentFeedbackUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface StudentFeedbackState {
    object Loading : StudentFeedbackState
    data class Success(
        val feedback: StudentClassFeedback?,
        val isSubmitting: Boolean = false,
        val submitError: String? = null
    ) : StudentFeedbackState
    data class Error(val message: String) : StudentFeedbackState
}

class StudentFeedbackScreenModel(
    private val getStudentFeedbackUseCase: GetStudentFeedbackUseCase,
    private val submitStudentFeedbackUseCase: SubmitStudentFeedbackUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<StudentFeedbackState>(StudentFeedbackState.Loading)
    val state: StateFlow<StudentFeedbackState> = _state.asStateFlow()

    private var studentId: Long? = null
    private var classId: Long? = null

    fun loadFeedback(studentId: Long, classId: Long) {
        this.studentId = studentId
        this.classId = classId
        fetchFeedback()
    }

    fun retry() {
        fetchFeedback()
    }

    private fun fetchFeedback() {
        val currentStudentId = studentId ?: return
        val currentClassId = classId ?: return

        screenModelScope.launch {
            _state.value = StudentFeedbackState.Loading
            when (val result = getStudentFeedbackUseCase(currentStudentId, currentClassId)) {
                is ApiResult.Error -> {
                    _state.value = StudentFeedbackState.Error(
                        result.message ?: "Lỗi tải thông tin phản hồi."
                    )
                }
                is ApiResult.Success -> {
                    _state.value = StudentFeedbackState.Success(feedback = result.data)
                }
            }
        }
    }

    fun submitFeedback(classId: Long, rating: Int, comment: String) {
        val currentState = _state.value as? StudentFeedbackState.Success ?: return
        if (currentState.isSubmitting) return

        _state.value = currentState.copy(isSubmitting = true, submitError = null)

        screenModelScope.launch {
            when (val result = submitStudentFeedbackUseCase(classId, rating, comment.trim())) {
                is ApiResult.Error -> {
                    val latestState = _state.value as? StudentFeedbackState.Success ?: return@launch
                    _state.value = latestState.copy(
                        isSubmitting = false,
                        submitError = result.message ?: "Lỗi gửi phản hồi."
                    )
                }
                is ApiResult.Success -> {
                    val latestState = _state.value as? StudentFeedbackState.Success ?: return@launch
                    val newFeedback = result.data

                    val updatedFeedback = latestState.feedback?.copy(
                        feedbackRating = newFeedback.feedbackRating.toString(),
                        feedbackComment = newFeedback.feedbackComment,
                        feedbackAt = newFeedback.feedbackAt
                    ) ?: StudentClassFeedback(
                        classId = classId,
                        studentId = studentId ?: 0L,
                        studentName = newFeedback.studentName,
                        className = newFeedback.className,
                        courseName = "",
                        enrolledDate = null,
                        status = "",
                        feedbackRating = newFeedback.feedbackRating.toString(),
                        feedbackComment = newFeedback.feedbackComment,
                        feedbackAt = newFeedback.feedbackAt,
                        teacherFeedback = newFeedback.teacherFeedback,
                        teacherFeedbackDate = newFeedback.teacherFeedbackDate,
                        finalResult = null,
                        resultNote = null,
                        resultDate = null
                    )

                    _state.value = StudentFeedbackState.Success(
                        feedback = updatedFeedback,
                        isSubmitting = false,
                        submitError = null
                    )
                }
            }
        }
    }
}
