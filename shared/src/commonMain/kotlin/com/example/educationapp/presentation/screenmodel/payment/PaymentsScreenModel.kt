package com.example.educationapp.presentation.screenmodel.payment

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.UiText
import com.example.educationapp.core.util.asUiText
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.domain.usecase.GetMyProfileUseCase
import com.example.educationapp.domain.usecase.GetStudentClassesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentsScreenModel(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val getStudentClassesUseCase: GetStudentClassesUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<PaymentsTabState>(PaymentsTabState.Loading)
    val state: StateFlow<PaymentsTabState> = _state.asStateFlow()

    private var currentRole: AppRole? = null
    private var userId: Long? = null

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatus = MutableStateFlow<String?>(null) // null means "ALL"
    val selectedStatus: StateFlow<String?> = _selectedStatus.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var fetchJob: Job? = null

    fun loadProfileAndClasses(role: AppRole, studentId: Long? = null) {
        val currentState = _state.value
        val isSameUser = if (role == AppRole.STUDENT) {
            currentRole == role && userId != null
        } else {
            currentRole == role && userId == studentId
        }

        if (isSameUser && currentState is PaymentsTabState.Success) {
            launchFetchClasses(page = 0, append = false, silent = true)
            return
        }

        currentRole = role
        screenModelScope.launch {
            _state.value = PaymentsTabState.Loading
            if (role == AppRole.STUDENT) {
                when (val profileResult = getMyProfileUseCase(role)) {
                    is ApiResult.Error -> {
                        _state.value = PaymentsTabState.Error(profileResult.asUiText())
                    }
                    is ApiResult.Success -> {
                        val profile = profileResult.data
                        if (profile is UserProfile.Student) {
                            userId = profile.studentId.toLong()
                            launchFetchClasses(page = 0, append = false)
                        } else {
                            _state.value = PaymentsTabState.Error(UiText.DynamicString("Tài khoản không đúng vai trò học sinh."))
                        }
                    }
                }
            } else if (role == AppRole.PARENT) {
                if (studentId != null) {
                    userId = studentId
                    launchFetchClasses(page = 0, append = false)
                } else {
                    _state.value = PaymentsTabState.Success(
                        classes = emptyList(),
                        currentPage = 0,
                        totalPages = 0,
                        totalElements = 0,
                        hasNextPage = false,
                        studentId = 0L
                    )
                }
            } else {
                _state.value = PaymentsTabState.Error(UiText.DynamicString("Vai trò này không được hỗ trợ thanh toán học phí."))
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
        _selectedStatus.value = if (status == "ALL" || status.isNullOrEmpty()) null else status
        userId?.let {
            launchFetchClasses(page = 0, append = false)
        }
    }

    fun loadNextPage() {
        val currentState = _state.value
        if (currentState is PaymentsTabState.Success && currentState.hasNextPage) {
            launchFetchClasses(page = currentState.currentPage + 1, append = true)
        }
    }

    fun refreshData() {
        val id = userId ?: return
        if (fetchJob?.isActive == true) return
        fetchJob?.cancel()
        fetchJob = screenModelScope.launch {
            _isRefreshing.value = true
            fetchClasses(id, page = 0, append = false, silent = true)
            _isRefreshing.value = false
        }
    }

    private fun launchFetchClasses(page: Int, append: Boolean, silent: Boolean = false) {
        val id = userId ?: return
        if (append && fetchJob?.isActive == true) {
            return
        }
        fetchJob?.cancel()
        fetchJob = screenModelScope.launch {
            fetchClasses(id, page, append, silent)
        }
    }

    private suspend fun fetchClasses(id: Long, page: Int, append: Boolean, silent: Boolean = false) {
        if (!append && !silent) {
            _state.value = PaymentsTabState.Loading
        }

        val result = getStudentClassesUseCase(
            studentId = id,
            status = _selectedStatus.value,
            page = page,
            size = 20
        )

        when (result) {
            is ApiResult.Error -> {
                if (!append) {
                    _state.value = PaymentsTabState.Error(result.asUiText())
                }
            }
            is ApiResult.Success -> {
                val pagination = result.data
                val newClasses = pagination.content
                val searchQueryVal = searchQuery.value
                val statusVal = _selectedStatus.value
                val filteredNewClasses = newClasses.filter { schoolClass ->
                    val matchesStatus = if (statusVal != null) {
                        schoolClass.status.equals(statusVal, ignoreCase = true)
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

                val currentClasses = if (append && _state.value is PaymentsTabState.Success) {
                    (_state.value as PaymentsTabState.Success).classes + filteredNewClasses
                } else {
                    filteredNewClasses
                }

                _state.value = PaymentsTabState.Success(
                    classes = currentClasses,
                    currentPage = pagination.number,
                    totalPages = pagination.totalPages,
                    totalElements = pagination.totalElements,
                    hasNextPage = !pagination.last && pagination.content.isNotEmpty(),
                    studentId = id,
                    isSearchingOrFiltering = searchQuery.value.isNotBlank() || _selectedStatus.value != null
                )
            }
        }
    }
}