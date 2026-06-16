package com.example.educationapp.presentation.screen.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.data.TokenManager
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.domain.usecase.GetMyProfileUseCase
import com.example.educationapp.domain.usecase.UpdateStudentProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

sealed interface EditProfileUiState {
    object Idle : EditProfileUiState
    object Loading : EditProfileUiState
    data class LoadSuccess(
        val student: UserProfile.Student,
        val fullName: String,
        val dateOfBirth: LocalDate?,
        val gender: String,
        val address: String,
        val zaloLink: String
    ) : EditProfileUiState
    data class Error(val message: String) : EditProfileUiState
}

sealed interface SaveStatus {
    object Idle : SaveStatus
    object Saving : SaveStatus
    object Saved : SaveStatus
    data class Error(val message: String) : SaveStatus
}

class EditProfileScreenModel(
    private val tokenManager: TokenManager,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val updateStudentProfileUseCase: UpdateStudentProfileUseCase
) : ScreenModel {

    private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Idle)
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        screenModelScope.launch {
            _uiState.value = EditProfileUiState.Loading
            val role = tokenManager.getUserRole()
            if (role != AppRole.STUDENT) {
                _uiState.value = EditProfileUiState.Error("Only student accounts can update profiles.")
                return@launch
            }

            when (val result = getMyProfileUseCase(role)) {
                is ApiResult.Success -> {
                    val profile = result.data
                    if (profile is UserProfile.Student) {
                        val dob = profile.dateOfBirth?.let {
                            try {
                                LocalDate.parse(it)
                            } catch (e: Exception) {
                                null
                            }
                        }
                        _uiState.value = EditProfileUiState.LoadSuccess(
                            student = profile,
                            fullName = profile.fullName,
                            dateOfBirth = dob,
                            gender = profile.gender ?: "MALE",
                            address = profile.address ?: "",
                            zaloLink = profile.zaloLink ?: ""
                        )
                    } else {
                        _uiState.value = EditProfileUiState.Error("Could not retrieve student profile.")
                    }
                }
                is ApiResult.Error -> {
                    _uiState.value = EditProfileUiState.Error(result.message ?: "Failed to load profile.")
                }
            }
        }
    }

    fun onFullNameChanged(name: String) {
        val state = _uiState.value
        if (state is EditProfileUiState.LoadSuccess) {
            _uiState.value = state.copy(fullName = name)
        }
    }

    fun onDateOfBirthChanged(date: LocalDate) {
        val state = _uiState.value
        if (state is EditProfileUiState.LoadSuccess) {
            _uiState.value = state.copy(dateOfBirth = date)
        }
    }

    fun onGenderChanged(gender: String) {
        val state = _uiState.value
        if (state is EditProfileUiState.LoadSuccess) {
            _uiState.value = state.copy(gender = gender)
        }
    }

    fun onAddressChanged(address: String) {
        val state = _uiState.value
        if (state is EditProfileUiState.LoadSuccess) {
            _uiState.value = state.copy(address = address)
        }
    }

    fun onZaloLinkChanged(link: String) {
        val state = _uiState.value
        if (state is EditProfileUiState.LoadSuccess) {
            _uiState.value = state.copy(zaloLink = link)
        }
    }

    fun saveProfile() {
        val state = _uiState.value
        if (state !is EditProfileUiState.LoadSuccess) return

        screenModelScope.launch {
            _saveStatus.value = SaveStatus.Saving
            val dobStr = state.dateOfBirth?.toString() ?: ""
            
            val result = updateStudentProfileUseCase(
                studentId = state.student.studentId,
                fullName = state.fullName,
                dateOfBirth = dobStr,
                gender = state.gender,
                address = state.address,
                zaloLink = state.zaloLink
            )

            when (result) {
                is ApiResult.Success -> {
                    _saveStatus.value = SaveStatus.Saved
                    _uiState.value = state.copy(
                        student = result.data,
                        fullName = result.data.fullName,
                        dateOfBirth = result.data.dateOfBirth?.let { LocalDate.parse(it) },
                        gender = result.data.gender ?: "MALE",
                        address = result.data.address ?: "",
                        zaloLink = result.data.zaloLink ?: ""
                    )
                }
                is ApiResult.Error -> {
                    _saveStatus.value = SaveStatus.Error(result.message ?: "Failed to save profile.")
                }
            }
        }
    }

    fun resetSaveStatus() {
        _saveStatus.value = SaveStatus.Idle
    }
}
