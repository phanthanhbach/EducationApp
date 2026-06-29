package com.example.educationapp.presentation.screenmodel.parent

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.SchoolClass
import com.example.educationapp.domain.usecase.GetStudentClassesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

sealed interface FeedbackClassesState {
    object Loading : FeedbackClassesState
    data class Success(
        val classes: List<SchoolClass>,
        val currentPage: Int,
        val totalPages: Int,
        val totalElements: Int,
        val hasNextPage: Boolean,
        val isSearchingOrFiltering: Boolean = false
    ) : FeedbackClassesState
    data class Error(val error: UiText) : FeedbackClassesState
}

class FeedbackScreenModel(
    private val getStudentClassesUseCase: GetStudentClassesUseCase
) : ScreenModel {

    private var currentStudentId: Long? = null
    val studentId: Long? get() = currentStudentId

    private val _classesState = MutableStateFlow<FeedbackClassesState>(FeedbackClassesState.Loading)
    val classesState: StateFlow<FeedbackClassesState> = _classesState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatus = MutableStateFlow<String?>(null) // null means "ALL"
    val selectedStatus: StateFlow<String?> = _selectedStatus.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var fetchJob: Job? = null

    fun loadClasses(studentId: Long, forceRefresh: Boolean = false) {
        val currentState = _classesState.value
        val isSameStudent = currentStudentId == studentId

        if (!forceRefresh && isSameStudent && currentState is FeedbackClassesState.Success) {
            launchFetchClasses(page = 0, append = false, silent = true)
            return
        }

        currentStudentId = studentId
        if (!forceRefresh) {
            _classesState.value = FeedbackClassesState.Loading
        }
        launchFetchClasses(page = 0, append = false)
    }

    fun searchClasses(query: String) {
        _searchQuery.value = query
        currentStudentId?.let {
            launchFetchClasses(page = 0, append = false)
        }
    }

    fun filterByStatus(status: String?) {
        _selectedStatus.value = if (status == "ALL" || status.isNullOrEmpty()) null else status
        currentStudentId?.let {
            launchFetchClasses(page = 0, append = false)
        }
    }

    fun loadNextPage() {
        val currentState = _classesState.value
        if (currentState is FeedbackClassesState.Success && currentState.hasNextPage) {
            launchFetchClasses(page = currentState.currentPage + 1, append = true)
        }
    }

    fun refreshData() {
        val studentId = currentStudentId ?: return
        if (fetchJob?.isActive == true) return
        fetchJob?.cancel()
        fetchJob = screenModelScope.launch {
            _isRefreshing.value = true
            fetchClasses(studentId, page = 0, append = false, silent = true)
            _isRefreshing.value = false
        }
    }

    private fun launchFetchClasses(page: Int, append: Boolean, silent: Boolean = false) {
        val studentId = currentStudentId ?: return
        if (append && fetchJob?.isActive == true) {
            return
        }
        fetchJob?.cancel()
        fetchJob = screenModelScope.launch {
            fetchClasses(studentId, page, append, silent)
        }
    }

    private suspend fun fetchClasses(studentId: Long, page: Int, append: Boolean, silent: Boolean = false) {
        if (!append && !silent) {
            _classesState.value = FeedbackClassesState.Loading
        }

        val result = getStudentClassesUseCase(
            studentId = studentId,
            status = _selectedStatus.value,
            page = page,
            size = 20
        )

        when (result) {
            is ApiResult.Error -> {
                if (!append) {
                    _classesState.value = FeedbackClassesState.Error(
                        UiText.DynamicString(result.message ?: "Không thể tải danh sách lớp học.")
                    )
                }
            }
            is ApiResult.Success -> {
                val pagination = result.data
                val newClasses = pagination.content

                val searchQueryVal = searchQuery.value
                val filteredNewClasses = if (searchQueryVal.isNotBlank()) {
                    newClasses.filter { schoolClass ->
                        schoolClass.name.contains(searchQueryVal, ignoreCase = true) ||
                                schoolClass.courseName.contains(searchQueryVal, ignoreCase = true)
                    }
                } else {
                    newClasses
                }

                val currentClasses = if (append && _classesState.value is FeedbackClassesState.Success) {
                    (_classesState.value as FeedbackClassesState.Success).classes + filteredNewClasses
                } else {
                    filteredNewClasses
                }

                _classesState.value = FeedbackClassesState.Success(
                    classes = currentClasses,
                    currentPage = pagination.number,
                    totalPages = pagination.totalPages,
                    totalElements = pagination.totalElements,
                    hasNextPage = !pagination.last && pagination.content.isNotEmpty(),
                    isSearchingOrFiltering = searchQuery.value.isNotBlank() || _selectedStatus.value != null
                )
            }
        }
    }
}
