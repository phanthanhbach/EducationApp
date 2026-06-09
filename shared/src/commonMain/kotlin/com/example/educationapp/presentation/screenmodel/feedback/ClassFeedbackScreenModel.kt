package com.example.educationapp.presentation.screenmodel.feedback

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.StudentClassFeedback
import com.example.educationapp.domain.usecase.GetClassFeedbacksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ClassFeedbackState {
    object Loading : ClassFeedbackState
    data class Success(
        val feedbacks: List<StudentClassFeedback>,
        val currentPage: Int,
        val totalPages: Int,
        val totalElements: Int,
        val hasNextPage: Boolean
    ) : ClassFeedbackState
    data class Error(val message: String) : ClassFeedbackState
}

class ClassFeedbackScreenModel(
    private val getClassFeedbacksUseCase: GetClassFeedbacksUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<ClassFeedbackState>(ClassFeedbackState.Loading)
    val state: StateFlow<ClassFeedbackState> = _state.asStateFlow()

    private var classId: Long? = null
    private var isLoadingNextPage = false

    fun loadFeedbacks(classId: Long) {
        this.classId = classId
        fetchFeedbacks(page = 0, append = false)
    }

    fun retry() {
        classId?.let { fetchFeedbacks(page = 0, append = false) }
    }

    fun loadNextPage() {
        val currentState = _state.value
        if (currentState is ClassFeedbackState.Success && currentState.hasNextPage && !isLoadingNextPage) {
            fetchFeedbacks(page = currentState.currentPage + 1, append = true)
        }
    }

    private fun fetchFeedbacks(page: Int, append: Boolean) {
        val currentClassId = classId ?: return
        screenModelScope.launch {
            if (append) {
                isLoadingNextPage = true
            } else {
                _state.value = ClassFeedbackState.Loading
            }

            when (val result = getClassFeedbacksUseCase(currentClassId, page, 20)) {
                is ApiResult.Error -> {
                    if (!append) {
                        _state.value = ClassFeedbackState.Error(
                            result.message ?: "Lỗi tải danh sách feedback."
                        )
                    }
                }
                is ApiResult.Success -> {
                    val pagination = result.data
                    val currentFeedbacks = if (append && _state.value is ClassFeedbackState.Success) {
                        (_state.value as ClassFeedbackState.Success).feedbacks + pagination.content
                    } else {
                        pagination.content
                    }

                    _state.value = ClassFeedbackState.Success(
                        feedbacks = currentFeedbacks,
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
