package com.example.educationapp.presentation.screenmodel

import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.data.TokenManager
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.decodeByteArrayToImageBitmap
import com.example.educationapp.core.util.UiText
import com.example.educationapp.core.util.asUiText
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.domain.usecase.GetMyProfileUseCase
import com.example.educationapp.domain.usecase.UpdateParentProfileUseCase
import com.example.educationapp.domain.usecase.UpdateStudentProfileUseCase
import com.example.educationapp.domain.usecase.UpdateTeacherProfileUseCase
import com.example.educationapp.domain.usecase.UploadAvatarUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class EditProfileScreenModel(
    private val tokenManager: TokenManager,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val updateStudentProfileUseCase: UpdateStudentProfileUseCase,
    private val updateTeacherProfileUseCase: UpdateTeacherProfileUseCase,
    private val updateParentProfileUseCase: UpdateParentProfileUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase
) : ScreenModel {

    private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Idle)
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus.asStateFlow()

    /**
     * Cropped avatar bytes pending upload. Only uploaded when user confirms save.
     */
    private var pendingAvatarBytes: ByteArray? = null

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
                    when (role) {
                        AppRole.STUDENT if profile is UserProfile.Student -> {
                            val dob = profile.dateOfBirth?.let {
                                try {
                                    LocalDate.parse(it)
                                } catch (_: Exception) {
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

                        AppRole.TEACHER if profile is UserProfile.Teacher -> {
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

                        AppRole.PARENT if profile is UserProfile.Parent -> {
                            _uiState.value = EditProfileUiState.ParentLoadSuccess(
                                parent = profile,
                                fullName = profile.fullName,
                                phone = profile.phoneNumber ?: "",
                                email = profile.email ?: "",
                                address = profile.address ?: ""
                            )
                        }

                        else -> {
                            _uiState.value =
                                EditProfileUiState.Error(UiText.DynamicString("This role does not support profile editing."))
                        }
                    }
                }

                is ApiResult.Error -> {
                    _uiState.value =
                        EditProfileUiState.Error(result.asUiText())
                }
            }
        }
    }

    // ── Avatar handler ──────────────────────────────────────────────────

    /**
     * Called when user has picked and cropped an image.
     * Stores bytes locally for later upload and updates the preview bitmap.
     */
    fun onAvatarCropped(bytes: ByteArray) {
        pendingAvatarBytes = bytes
        val preview = decodeByteArrayToImageBitmap(bytes)

        when (val state = _uiState.value) {
            is EditProfileUiState.StudentLoadSuccess -> {
                _uiState.value = state.copy(avatarPreview = preview)
            }

            is EditProfileUiState.TeacherLoadSuccess -> {
                _uiState.value = state.copy(avatarPreview = preview)
            }

            is EditProfileUiState.ParentLoadSuccess -> {
                _uiState.value = state.copy(avatarPreview = preview)
            }

            else -> {}
        }
    }

    // ── Student field handlers ───────────────────────────────────────────

    fun onFullNameChanged(name: String) {
        when (val state = _uiState.value) {
            is EditProfileUiState.StudentLoadSuccess -> {
                _uiState.value = state.copy(fullName = name)
            }

            is EditProfileUiState.TeacherLoadSuccess -> {
                _uiState.value = state.copy(fullName = name)
            }

            is EditProfileUiState.ParentLoadSuccess -> {
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
        when (val state = _uiState.value) {
            is EditProfileUiState.StudentLoadSuccess -> {
                _uiState.value = state.copy(address = address)
            }

            is EditProfileUiState.ParentLoadSuccess -> {
                _uiState.value = state.copy(address = address)
            }

            else -> {}
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
        when (val state = _uiState.value) {
            is EditProfileUiState.TeacherLoadSuccess -> {
                _uiState.value = state.copy(email = email)
            }

            is EditProfileUiState.ParentLoadSuccess -> {
                _uiState.value = state.copy(email = email)
            }

            else -> {}
        }
    }

    fun onPhoneChanged(phone: String) {
        when (val state = _uiState.value) {
            is EditProfileUiState.TeacherLoadSuccess -> {
                _uiState.value = state.copy(phone = phone)
            }

            is EditProfileUiState.ParentLoadSuccess -> {
                _uiState.value = state.copy(phone = phone)
            }

            else -> {}
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

            // Step 1: Upload avatar to Cloudinary if pending
            val avatarUrl = if (pendingAvatarBytes != null) {
                when (val uploadResult = uploadAvatarUseCase(pendingAvatarBytes!!)) {
                    is ApiResult.Success -> {
                        pendingAvatarBytes = null
                        uploadResult.data
                    }

                    is ApiResult.Error -> {
                        _saveStatus.value = SaveStatus.Error(uploadResult.asUiText())
                        return@launch
                    }
                }
            } else {
                null // No new avatar → use existing
            }

            // Step 2: Update profile with new avatar URL (or existing)
            when (state) {
                is EditProfileUiState.StudentLoadSuccess -> saveStudentProfile(state, avatarUrl)
                is EditProfileUiState.TeacherLoadSuccess -> saveTeacherProfile(state, avatarUrl)
                is EditProfileUiState.ParentLoadSuccess -> saveParentProfile(state, avatarUrl)
                else -> {
                    _saveStatus.value = SaveStatus.Error(UiText.DynamicString("Cannot save: invalid state."))
                }
            }
        }
    }

    private suspend fun saveStudentProfile(
        state: EditProfileUiState.StudentLoadSuccess,
        newAvatarUrl: String?
    ) {
        val dobStr = state.dateOfBirth?.toString() ?: ""
        val imgUrl = newAvatarUrl ?: state.student.img ?: ""

        val result = updateStudentProfileUseCase(
            studentId = state.student.studentId,
            fullName = state.fullName,
            dateOfBirth = dobStr,
            gender = state.gender,
            address = state.address,
            zaloLink = state.zaloLink,
            img = imgUrl
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
                    zaloLink = result.data.zaloLink ?: "",
                    avatarPreview = null
                )
            }

            is ApiResult.Error -> {
                _saveStatus.value = SaveStatus.Error(result.asUiText())
            }
        }
    }

    private suspend fun saveTeacherProfile(
        state: EditProfileUiState.TeacherLoadSuccess,
        newAvatarUrl: String?
    ) {
        // Filter out blank/empty certificates before sending
        val cleanCertificates = state.certificates
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val imgUrl = newAvatarUrl ?: state.teacher.img ?: ""

        val result = updateTeacherProfileUseCase(
            teacherId = state.teacher.teacherId,
            fullName = state.fullName,
            email = state.email,
            phoneNumber = state.phone,
            img = imgUrl,
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
                    experience = result.data.experience ?: "",
                    avatarPreview = null
                )
            }

            is ApiResult.Error -> {
                _saveStatus.value = SaveStatus.Error(result.asUiText())
            }
        }
    }

    private suspend fun saveParentProfile(
        state: EditProfileUiState.ParentLoadSuccess,
        newAvatarUrl: String?
    ) {
        val imgUrl = newAvatarUrl ?: state.parent.img ?: ""

        val result = updateParentProfileUseCase(
            parentId = state.parent.parentId,
            fullName = state.fullName,
            email = state.email,
            phoneNumber = state.phone,
            address = state.address,
            img = imgUrl
        )

        when (result) {
            is ApiResult.Success -> {
                _saveStatus.value = SaveStatus.Saved
                _uiState.value = state.copy(
                    parent = result.data,
                    fullName = result.data.fullName,
                    email = result.data.email ?: "",
                    phone = result.data.phoneNumber ?: "",
                    address = result.data.address ?: "",
                    avatarPreview = null
                )
            }

            is ApiResult.Error -> {
                _saveStatus.value = SaveStatus.Error(result.asUiText())
            }
        }
    }

    fun resetSaveStatus() {
        _saveStatus.value = SaveStatus.Idle
    }
}
