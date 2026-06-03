package com.example.educationapp.data.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.BaseResponse
import com.example.educationapp.core.network.safeApiCall
import com.example.educationapp.data.dto.response.ParentDTO
import com.example.educationapp.data.dto.response.StudentDTO
import com.example.educationapp.data.dto.response.TeacherDTO
import com.example.educationapp.data.endpoint.ProfileEndpoint
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.domain.repository.ProfileRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ProfileRepositoryImpl(
    private val httpClient: HttpClient
) : ProfileRepository {

    override suspend fun getMyProfile(role: AppRole): ApiResult<UserProfile> {
        return when (role) {
            AppRole.TEACHER -> fetchTeacherProfile()
            AppRole.STUDENT -> fetchStudentProfile()
            AppRole.PARENT -> fetchParentProfile()
            AppRole.UNKNOWN -> ApiResult.Error.UnknownError(
                message = "Unknown role cannot fetch profile",
                exception = IllegalStateException("Unknown role")
            )
        }
    }

    private suspend fun fetchTeacherProfile(): ApiResult<UserProfile> {
        return safeApiCall {
            val response = httpClient.get(ProfileEndpoint.TEACHER_ME)
                .body<BaseResponse<TeacherDTO>>()
            val dto = response.data
            UserProfile.Teacher(
                teacherId = dto.teacherId,
                teacherCode = dto.teacherCode,
                fullName = dto.fullName,
                email = dto.email,
                phone = dto.phone,
                img = dto.img,
                certificates = dto.certificates ?: emptyList(),
                hourlyRate = dto.hourlyRate,
                experience = dto.experience,
                status = dto.status,
                createdAt = dto.createdAt,
                updatedAt = dto.updatedAt
            )
        }
    }

    private suspend fun fetchStudentProfile(): ApiResult<UserProfile> {
        return safeApiCall {
            val response = httpClient.get(ProfileEndpoint.STUDENT_ME)
                .body<BaseResponse<StudentDTO>>()
            val dto = response.data
            UserProfile.Student(
                studentId = dto.studentId,
                studentCode = dto.studentCode,
                fullName = dto.fullName,
                dateOfBirth = dto.dateOfBirth,
                gender = dto.gender,
                address = dto.address,
                img = dto.img,
                zaloLink = dto.zaloLink,
                currentLevel = dto.currentLevel,
                status = dto.status,
                parentId = dto.parentId,
                parentName = dto.parentName,
                createdAt = dto.createdAt,
                updatedAt = dto.updatedAt
            )
        }
    }

    private suspend fun fetchParentProfile(): ApiResult<UserProfile> {
        return safeApiCall {
            val response = httpClient.get(ProfileEndpoint.PARENT_ME)
                .body<BaseResponse<ParentDTO>>()
            val dto = response.data
            UserProfile.Parent(
                parentId = dto.parentId,
                fullName = dto.fullName,
                phoneNumber = dto.phoneNumber,
                email = dto.email,
                address = dto.address,
                active = dto.active,
                img = dto.img,
                createdAt = dto.createdAt
            )
        }
    }
}
