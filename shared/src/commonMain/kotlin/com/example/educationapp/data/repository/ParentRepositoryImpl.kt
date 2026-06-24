package com.example.educationapp.data.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.BaseResponse
import com.example.educationapp.core.network.safeApiCall
import com.example.educationapp.data.dto.response.StudentDTO
import com.example.educationapp.data.endpoint.ParentEndpoint
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.repository.ParentRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ParentRepositoryImpl(
    private val httpClient: HttpClient
) : ParentRepository {

    override suspend fun getMyChildren(): ApiResult<List<UserProfile.Student>> {
        return safeApiCall {
            val response = httpClient.get(ParentEndpoint.MY_STUDENTS)
                .body<BaseResponse<List<StudentDTO>>>()
            response.data.map { dto ->
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
                    updatedAt = dto.updatedAt,
                    email = dto.email,
                    phoneNumber = dto.phoneNumber
                )
            }
        }
    }
}
