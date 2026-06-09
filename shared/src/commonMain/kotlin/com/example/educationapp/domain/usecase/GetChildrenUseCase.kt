package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.repository.ParentRepository

class GetChildrenUseCase(
    private val repository: ParentRepository
) {
    suspend operator fun invoke(): ApiResult<List<UserProfile.Student>> {
        return repository.getMyChildren()
    }
}
