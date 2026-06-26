package com.example.educationapp.presentation.screenmodel.parent

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.Feedback
import com.example.educationapp.domain.entity.SchoolClass
import com.example.educationapp.domain.usecase.GetFeedbackNoPaginationUseCase
import com.example.educationapp.domain.usecase.GetStudentClassesNoPaginationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface FeedbackClassesState {
    object Idle : FeedbackClassesState
    object Loading : FeedbackClassesState
    data class Success(val classes: List<SchoolClass>) : FeedbackClassesState
    data class Error(val error: UiText) : FeedbackClassesState
}

sealed interface FeedbackDetailState {
    object Idle : FeedbackDetailState
    object Loading : FeedbackDetailState
    data class Success(val feedback: Feedback?) : FeedbackDetailState
    data class Error(val error: UiText) : FeedbackDetailState
}

class FeedbackScreenModel(
    private val getStudentClassesNoPaginationUseCase: GetStudentClassesNoPaginationUseCase,
    private val getFeedbackNoPaginationUseCase: GetFeedbackNoPaginationUseCase
) : ScreenModel {

    private var currentStudentId: Long? = null

    private val _classesState = MutableStateFlow<FeedbackClassesState>(FeedbackClassesState.Idle)
    val classesState: StateFlow<FeedbackClassesState> = _classesState.asStateFlow()

    private val _selectedClass = MutableStateFlow<SchoolClass?>(null)
    val selectedClass: StateFlow<SchoolClass?> = _selectedClass.asStateFlow()

    private val _feedbackState = MutableStateFlow<FeedbackDetailState>(FeedbackDetailState.Idle)
    val feedbackState: StateFlow<FeedbackDetailState> = _feedbackState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        screenModelScope.launch {
            _selectedClass.collect { clazz ->
                val studentId = currentStudentId
                if (clazz != null && studentId != null) {
                    loadFeedback(clazz.id, studentId)
                } else {
                    _feedbackState.value = FeedbackDetailState.Idle
                }
            }
        }
    }

    fun loadClasses(studentId: Long, forceRefresh: Boolean = false) {
        if (!forceRefresh && currentStudentId == studentId && _classesState.value is FeedbackClassesState.Success) {
            return
        }
        currentStudentId = studentId

        screenModelScope.launch {
            if (!forceRefresh) {
                _selectedClass.value = null
                _feedbackState.value = FeedbackDetailState.Idle
                _classesState.value = FeedbackClassesState.Loading
            }
            when (val result = getStudentClassesNoPaginationUseCase(studentId)) {
                is ApiResult.Error -> {
                    _classesState.value = FeedbackClassesState.Error(
                        UiText.DynamicString(result.message ?: "Không thể tải danh sách lớp học.")
                    )
                }
                is ApiResult.Success -> {
                    val list = result.data
                    _classesState.value = FeedbackClassesState.Success(list)
                    
                    val currentSelected = _selectedClass.value
                    val matchingClass = list.find { it.id == currentSelected?.id }
                    if (matchingClass != null) {
                        _selectedClass.value = matchingClass
                        loadFeedback(matchingClass.id, studentId)
                    } else {
                        _selectedClass.value = list.firstOrNull()
                    }
                }
            }
        }
    }

    fun refreshData() {
        val studentId = currentStudentId ?: return
        screenModelScope.launch {
            _isRefreshing.value = true
            loadClasses(studentId, forceRefresh = true)
            _isRefreshing.value = false
        }
    }

    fun selectClass(schoolClass: SchoolClass) {
        _selectedClass.value = schoolClass
    }

    private fun loadFeedback(classId: Long, studentId: Long) {
        screenModelScope.launch {
            _feedbackState.value = FeedbackDetailState.Loading
            when (val result = getFeedbackNoPaginationUseCase(classId = classId, studentId = studentId, feedbackType = "CLASS")) {
                is ApiResult.Error -> {
                    _feedbackState.value = FeedbackDetailState.Error(
                        UiText.DynamicString(result.message ?: "Không thể tải nhận xét từ giáo viên.")
                    )
                }
                is ApiResult.Success -> {
                    _feedbackState.value = FeedbackDetailState.Success(result.data)
                }
            }
        }
    }
}
