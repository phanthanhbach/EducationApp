package com.example.educationapp.domain.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.entity.TeacherRatingSummary
import com.example.educationapp.domain.enums.AppRole

interface ProfileRepository {
    suspend fun getMyProfile(role: AppRole): ApiResult<UserProfile>
    suspend fun getTeacherRatingSummary(teacherId: Long): ApiResult<TeacherRatingSummary>
    suspend fun updateStudentProfile(
        studentId: Int,
        fullName: String,
        dateOfBirth: String,
        gender: String,
        address: String,
        zaloLink: String
    ): ApiResult<UserProfile.Student>
    suspend fun updateTeacherProfile(
        teacherId: Int,
        fullName: String,
        email: String,
        phoneNumber: String,
        img: String,
        teacherCode: String,
        certificates: List<String>,
        experience: String
    ): ApiResult<UserProfile.Teacher>

    suspend fun updateParentProfile(
        parentId: Int,
        fullName: String,
        email: String,
        phoneNumber: String,
        address: String,
        img: String
    ): ApiResult<UserProfile.Parent>
}

