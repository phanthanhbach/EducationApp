package com.example.educationapp.presentation.screen.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.data.TokenManager
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.domain.usecase.GetMyProfileUseCase
import com.example.educationapp.domain.usecase.UpdateStudentProfileUseCase
import com.example.educationapp.domain.usecase.UpdateTeacherProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

sealed interface EditProfileUiState {
    object Idle : EditProfileUiState
    object Loading : EditProfileUiState
    data class StudentLoadSuccess(
        val student: UserProfile.Student,
        val fullName: String,
        val dateOfBirth: LocalDate?,
        val gender: String,
        val address: String,
        val zaloLink: String
    ) : EditProfileUiState

    data class TeacherLoadSuccess(
        val teacher: UserProfile.Teacher,
        val fullName: String,
        val email: String,
        val phone: String,
        val certificates: List<String>,
        val experience: String
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
    private val updateStudentProfileUseCase: UpdateStudentProfileUseCase,
    private val updateTeacherProfileUseCase: UpdateTeacherProfileUseCase
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

            when (val result = getMyProfileUseCase(role)) {
                is ApiResult.Success -> {
                    val profile = result.data
                    when {
                        role == AppRole.STUDENT && profile is UserProfile.Student -> {
                            val dob = profile.dateOfBirth?.let {
                                try {
                                    LocalDate.parse(it)
                                } catch (e: Exception) {
                                    null
                                }
                            }
                            _uiState.value = EditProfileUiState.StudentLoadSuccess(
                                student = profile,
                                fullName = profile.fullName,
                                dateOfBirth = dob,
                                gender = profile.gender ?: "MALE",
                                address = profile.address ?: "",
                                zaloLink = profile.zaloLink ?: ""
                            )
                        }
                        role == AppRole.TEACHER && profile is UserProfile.Teacher -> {
                            val initialCertificates = if (profile.certificates.isNotEmpty()) {
                                profile.certificates.toList()
                            } else {
                                listOf("")
                            }
                            _uiState.value = EditProfileUiState.TeacherLoadSuccess(
                                teacher = profile,
                                fullName = profile.fullName,
                                email = profile.email ?: "",
                                phone = profile.phone ?: "",
                                certificates = initialCertificates,
                                experience = profile.experience ?: ""
                            )
                        }
                        else -> {
                            _uiState.value = EditProfileUiState.Error("This role does not support profile editing.")
                        }
                    }
                }
                is ApiResult.Error -> {
                    _uiState.value = EditProfileUiState.Error(result.message ?: "Failed to load profile.")
                }
            }
        }
    }

    // ── Student field handlers ───────────────────────────────────────────

    fun onFullNameChanged(name: String) {
        val state = _uiState.value
        when (state) {
            is EditProfileUiState.StudentLoadSuccess -> {
                _uiState.value = state.copy(fullName = name)
            }
            is EditProfileUiState.TeacherLoadSuccess -> {
                _uiState.value = state.copy(fullName = name)
            }
            else -> {}
        }
    }

    fun onDateOfBirthChanged(date: LocalDate) {
        val state = _uiState.value
        if (state is EditProfileUiState.StudentLoadSuccess) {
            _uiState.value = state.copy(dateOfBirth = date)
        }
    }

    fun onGenderChanged(gender: String) {
        val state = _uiState.value
        if (state is EditProfileUiState.StudentLoadSuccess) {
            _uiState.value = state.copy(gender = gender)
        }
    }

    fun onAddressChanged(address: String) {
        val state = _uiState.value
        if (state is EditProfileUiState.StudentLoadSuccess) {
            _uiState.value = state.copy(address = address)
        }
    }

    fun onZaloLinkChanged(link: String) {
        val state = _uiState.value
        if (state is EditProfileUiState.StudentLoadSuccess) {
            _uiState.value = state.copy(zaloLink = link)
        }
    }

    // ── Teacher field handlers ──────────────────────────────────────────

    fun onEmailChanged(email: String) {
        val state = _uiState.value
        if (state is EditProfileUiState.TeacherLoadSuccess) {
            _uiState.value = state.copy(email = email)
        }
    }

    fun onPhoneChanged(phone: String) {
        val state = _uiState.value
        if (state is EditProfileUiState.TeacherLoadSuccess) {
            _uiState.value = state.copy(phone = phone)
        }
    }

    fun onExperienceChanged(experience: String) {
        val state = _uiState.value
        if (state is EditProfileUiState.TeacherLoadSuccess) {
            _uiState.value = state.copy(experience = experience)
        }
    }

    fun onCertificateChanged(index: Int, value: String) {
        val state = _uiState.value
        if (state is EditProfileUiState.TeacherLoadSuccess) {
            val updated = state.certificates.toMutableList()
            if (index in updated.indices) {
                updated[index] = value
                _uiState.value = state.copy(certificates = updated)
            }
        }
    }

    fun addCertificate() {
        val state = _uiState.value
        if (state is EditProfileUiState.TeacherLoadSuccess) {
            _uiState.value = state.copy(certificates = state.certificates + "")
        }
    }

    fun removeCertificate(index: Int) {
        val state = _uiState.value
        if (state is EditProfileUiState.TeacherLoadSuccess) {
            val updated = state.certificates.toMutableList()
            if (index in updated.indices) {
                updated.removeAt(index)
                // Always keep at least one empty field
                if (updated.isEmpty()) {
                    updated.add("")
                }
                _uiState.value = state.copy(certificates = updated)
            }
        }
    }

    // ── Save ────────────────────────────────────────────────────────────

    fun saveProfile() {
        val state = _uiState.value

        screenModelScope.launch {
            _saveStatus.value = SaveStatus.Saving

            when (state) {
                is EditProfileUiState.StudentLoadSuccess -> saveStudentProfile(state)
                is EditProfileUiState.TeacherLoadSuccess -> saveTeacherProfile(state)
                else -> {
                    _saveStatus.value = SaveStatus.Error("Cannot save: invalid state.")
                }
            }
        }
    }

    private suspend fun saveStudentProfile(state: EditProfileUiState.StudentLoadSuccess) {
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

    private suspend fun saveTeacherProfile(state: EditProfileUiState.TeacherLoadSuccess) {
        // Filter out blank/empty certificates before sending
        val cleanCertificates = state.certificates
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val result = updateTeacherProfileUseCase(
            teacherId = state.teacher.teacherId,
            fullName = state.fullName,
            email = state.email,
            phoneNumber = state.phone,
            img = state.teacher.img ?: "",
            teacherCode = state.teacher.teacherCode ?: "",
            certificates = cleanCertificates,
            experience = state.experience
        )

        when (result) {
            is ApiResult.Success -> {
                _saveStatus.value = SaveStatus.Saved
                val updatedCertificates = if (result.data.certificates.isNotEmpty()) {
                    result.data.certificates.toList()
                } else {
                    listOf("")
                }
                _uiState.value = state.copy(
                    teacher = result.data,
                    fullName = result.data.fullName,
                    email = result.data.email ?: "",
                    phone = result.data.phone ?: "",
                    certificates = updatedCertificates,
                    experience = result.data.experience ?: ""
                )
            }
            is ApiResult.Error -> {
                _saveStatus.value = SaveStatus.Error(result.message ?: "Failed to save profile.")
            }
        }
    }

    fun resetSaveStatus() {
        _saveStatus.value = SaveStatus.Idle
    }
}
