package com.example.educationapp.domain.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.entity.TeacherRatingSummary
import com.example.educationapp.domain.enums.AppRole

interface ProfileRepository {
    suspend fun getMyProfile(role: AppRole): ApiResult<UserProfile>
    suspend fun getTeacherRatingSummary(teacherId: Long): ApiResult<TeacherRatingSummary>
}
