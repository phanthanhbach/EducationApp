package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.repository.ProfileRepository

class UpdateTeacherProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        teacherId: Int,
        fullName: String,
        email: String,
        phoneNumber: String,
        img: String,
        teacherCode: String,
        certificates: List<String>,
        experience: String
    ): ApiResult<UserProfile.Teacher> {
        return repository.updateTeacherProfile(
            teacherId = teacherId,
            fullName = fullName,
            email = email,
            phoneNumber = phoneNumber,
            img = img,
            teacherCode = teacherCode,
            certificates = certificates,
            experience = experience
        )
    }
}
