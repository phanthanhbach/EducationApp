package com.example.educationapp.presentation.screenmodel.assignment

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.SchoolClass
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.domain.usecase.FilterClassesUseCase
import com.example.educationapp.domain.usecase.GetMyProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AssignmentTabState {
    object Loading : AssignmentTabState
    data class Success(
        val classes: List<SchoolClass>,
        val currentPage: Int,
        val totalPages: Int,
        val totalElements: Int,
        val hasNextPage: Boolean,
        val isSearchingOrFiltering: Boolean = false
    ) : AssignmentTabState
    data class Error(val message: String) : AssignmentTabState
}

class AssignmentTabScreenModel(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val filterClassesUseCase: FilterClassesUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<AssignmentTabState>(AssignmentTabState.Loading)
    val state: StateFlow<AssignmentTabState> = _state.asStateFlow()

    private var teacherId: Long? = null

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatus = MutableStateFlow<String?>(null) // null means "ALL"
    val selectedStatus: StateFlow<String?> = _selectedStatus.asStateFlow()

    init {
        loadProfileAndClasses()
    }

    fun loadProfileAndClasses() {
        screenModelScope.launch {
            _state.value = AssignmentTabState.Loading
            when (val profileResult = getMyProfileUseCase(AppRole.TEACHER)) {
                is ApiResult.Error -> {
                    _state.value = AssignmentTabState.Error(
                        profileResult.message ?: "Không thể lấy thông tin profile giáo viên."
                    )
                }
                is ApiResult.Success -> {
                    val profile = profileResult.data
                    if (profile is UserProfile.Teacher) {
                        teacherId = profile.teacherId.toLong()
                        fetchClasses(page = 0, append = false)
                    } else {
                        _state.value = AssignmentTabState.Error("Tài khoản không phải giáo viên.")
                    }
                }
            }
        }
    }

    fun searchClasses(query: String) {
        _searchQuery.value = query
        teacherId?.let {
            fetchClasses(page = 0, append = false)
        }
    }

    fun filterByStatus(status: String?) {
        _selectedStatus.value = if (status == "ALL") null else status
        teacherId?.let {
            fetchClasses(page = 0, append = false)
        }
    }

    fun loadNextPage() {
        val currentState = _state.value
        if (currentState is AssignmentTabState.Success && currentState.hasNextPage) {
            fetchClasses(page = currentState.currentPage + 1, append = true)
        }
    }

    private fun fetchClasses(page: Int, append: Boolean) {
        val tId = teacherId ?: return
        screenModelScope.launch {
            if (!append) {
                _state.value = AssignmentTabState.Loading
            }

            val result = filterClassesUseCase(
                search = searchQuery.value.takeIf { it.isNotBlank() },
                teacherId = tId,
                status = selectedStatus.value,
                page = page,
                size = 20
            )

            when (result) {
                is ApiResult.Error -> {
                    if (!append) {
                        _state.value = AssignmentTabState.Error(result.message ?: "Lỗi tải danh sách lớp.")
                    }
                }
                is ApiResult.Success -> {
                    val pagination = result.data
                    val newClasses = pagination.content
                    val currentClasses = if (append && _state.value is AssignmentTabState.Success) {
                        (_state.value as AssignmentTabState.Success).classes + newClasses
                    } else {
                        newClasses
                    }

                    _state.value = AssignmentTabState.Success(
                        classes = currentClasses,
                        currentPage = pagination.number,
                        totalPages = pagination.totalPages,
                        totalElements = pagination.totalElements,
                        hasNextPage = !pagination.last && pagination.content.isNotEmpty(),
                        isSearchingOrFiltering = searchQuery.value.isNotBlank() || selectedStatus.value != null
                    )
                }
            }
        }
    }
}
