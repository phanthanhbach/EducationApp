package com.example.educationapp.presentation.screenmodel.assignment

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.SchoolClass
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.domain.usecase.FilterClassesUseCase
import com.example.educationapp.domain.usecase.GetMyProfileUseCase
import com.example.educationapp.domain.usecase.GetStudentClassesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

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
    private val filterClassesUseCase: FilterClassesUseCase,
    private val getStudentClassesUseCase: GetStudentClassesUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<AssignmentTabState>(AssignmentTabState.Loading)
    val state: StateFlow<AssignmentTabState> = _state.asStateFlow()

    private var currentRole: AppRole? = null
    private var userId: Long? = null
    val currentUserId: Long? get() = userId

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatus = MutableStateFlow<String?>(null) // null means "ALL"
    val selectedStatus: StateFlow<String?> = _selectedStatus.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var fetchJob: Job? = null

    fun loadProfileAndClasses(role: AppRole = currentRole ?: AppRole.TEACHER) {
        val currentState = _state.value
        val isSameUser = currentRole == role && userId != null

        if (isSameUser && currentState is AssignmentTabState.Success) {
            launchFetchClasses(page = 0, append = false, silent = true)
            return
        }

        currentRole = role
        screenModelScope.launch {
            _state.value = AssignmentTabState.Loading
            when (val profileResult = getMyProfileUseCase(role)) {
                is ApiResult.Error -> {
                    _state.value = AssignmentTabState.Error(
                        profileResult.message ?: "Không thể lấy thông tin profile."
                    )
                }
                is ApiResult.Success -> {
                    val profile = profileResult.data
                    when {
                        role == AppRole.TEACHER && profile is UserProfile.Teacher -> {
                            userId = profile.teacherId.toLong()
                            launchFetchClasses(page = 0, append = false)
                        }
                        role == AppRole.STUDENT && profile is UserProfile.Student -> {
                            userId = profile.studentId.toLong()
                            launchFetchClasses(page = 0, append = false)
                        }
                        else -> {
                            _state.value = AssignmentTabState.Error("Tài khoản không đúng vai trò.")
                        }
                    }
                }
            }
        }
    }

    fun searchClasses(query: String) {
        _searchQuery.value = query
        userId?.let {
            launchFetchClasses(page = 0, append = false)
        }
    }

    fun filterByStatus(status: String?) {
        _selectedStatus.value = if (status == "ALL") null else status
        userId?.let {
            launchFetchClasses(page = 0, append = false)
        }
    }

    fun loadNextPage() {
        val currentState = _state.value
        if (currentState is AssignmentTabState.Success && currentState.hasNextPage) {
            launchFetchClasses(page = currentState.currentPage + 1, append = true)
        }
    }

    fun refreshData() {
        val id = userId ?: return
        val role = currentRole ?: return
        if (fetchJob?.isActive == true) return
        fetchJob?.cancel()
        fetchJob = screenModelScope.launch {
            _isRefreshing.value = true
            fetchClasses(id, role, page = 0, append = false, silent = true)
            _isRefreshing.value = false
        }
    }

    private fun launchFetchClasses(page: Int, append: Boolean, silent: Boolean = false) {
        val id = userId ?: return
        val role = currentRole ?: return
        if (append && fetchJob?.isActive == true) {
            return
        }
        fetchJob?.cancel()
        fetchJob = screenModelScope.launch {
            fetchClasses(id, role, page, append, silent)
        }
    }

    private suspend fun fetchClasses(id: Long, role: AppRole, page: Int, append: Boolean, silent: Boolean = false) {
        if (!append && !silent) {
            _state.value = AssignmentTabState.Loading
        }

        val result = if (role == AppRole.TEACHER) {
            filterClassesUseCase(
                search = searchQuery.value.takeIf { it.isNotBlank() },
                teacherId = id,
                status = selectedStatus.value,
                page = page,
                size = 20
            )
        } else {
            getStudentClassesUseCase(
                studentId = id,
                status = selectedStatus.value,
                page = page,
                size = 20
            )
        }

        when (result) {
            is ApiResult.Error -> {
                if (!append) {
                    _state.value = AssignmentTabState.Error(result.message ?: "Lỗi tải danh sách lớp.")
                }
            }
            is ApiResult.Success -> {
                val pagination = result.data
                val newClasses = pagination.content
                
                val filteredNewClasses = if (role == AppRole.STUDENT) {
                    val searchQueryVal = searchQuery.value
                    newClasses.filter { schoolClass ->
                        val matchesStatus = if (selectedStatus.value != null) {
                            schoolClass.status.equals(selectedStatus.value, ignoreCase = true)
                        } else {
                            true
                        }
                        
                        val matchesSearch = if (searchQueryVal.isNotBlank()) {
                            schoolClass.name.contains(searchQueryVal, ignoreCase = true) ||
                                    schoolClass.courseName.contains(searchQueryVal, ignoreCase = true)
                        } else {
                            true
                        }
                        
                        matchesStatus && matchesSearch
                    }
                } else {
                    newClasses
                }

                val currentClasses = if (append && _state.value is AssignmentTabState.Success) {
                    (_state.value as AssignmentTabState.Success).classes + filteredNewClasses
                } else {
                    filteredNewClasses
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
