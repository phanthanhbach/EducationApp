package com.example.educationapp.domain.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.TeacherRatingSummary
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.AppRole

class MockProfileRepository : ProfileRepository {
    var shouldReturnError = false
    var stubbedProfile: UserProfile? = null
    var lastRequestedRole: AppRole? = null

    override suspend fun getMyProfile(role: AppRole): ApiResult<UserProfile> {
        lastRequestedRole = role
        if (shouldReturnError) {
            return ApiResult.Error.UnknownError("Fake repository error", Exception())
        }
        return stubbedProfile?.let { ApiResult.Success(it) }
            ?: ApiResult.Error.UnknownError("No stubbed profile", Exception())
    }

    override suspend fun getTeacherRatingSummary(teacherId: Long): ApiResult<TeacherRatingSummary> {
        TODO("Not needed for testing GetMyProfileUseCase")
    }

    override suspend fun updateStudentProfile(
        studentId: Int,
        fullName: String,
        dateOfBirth: String,
        gender: String,
        address: String,
        zaloLink: String,
        img: String
    ): ApiResult<UserProfile.Student> {
        TODO("Not needed for testing GetMyProfileUseCase")
    }

    override suspend fun updateTeacherProfile(
        teacherId: Int,
        fullName: String,
        email: String,
        phoneNumber: String,
        img: String,
        teacherCode: String,
        certificates: List<String>,
        experience: String
    ): ApiResult<UserProfile.Teacher> {
        TODO("Not needed for testing GetMyProfileUseCase")
    }

    override suspend fun updateParentProfile(
        parentId: Int,
        fullName: String,
        email: String,
        phoneNumber: String,
        address: String,
        img: String
    ): ApiResult<UserProfile.Parent> {
        TODO("Not needed for testing GetMyProfileUseCase")
    }
}
