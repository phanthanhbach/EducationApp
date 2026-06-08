package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.StudentClassInfo
import com.example.educationapp.domain.repository.StudentDashboardRepository

class GetStudentClassesInfoUseCase(
    private val repository: StudentDashboardRepository
) {
    suspend operator fun invoke(
        studentId: Long,
        page: Int = 0,
        size: Int = 100
    ): ApiResult<List<StudentClassInfo>> {
        return repository.getStudentClassesInfo(studentId, page, size)
    }
}
