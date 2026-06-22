package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.repository.ProfileRepository

class UpdateStudentProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        studentId: Int,
        fullName: String,
        dateOfBirth: String,
        gender: String,
        address: String,
        zaloLink: String,
        img: String
    ): ApiResult<UserProfile.Student> {
        return repository.updateStudentProfile(
            studentId = studentId,
            fullName = fullName,
            dateOfBirth = dateOfBirth,
            gender = gender,
            address = address,
            zaloLink = zaloLink,
            img = img
        )
    }
}
