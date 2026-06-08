package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.domain.entity.AssignmentReminder
import com.example.educationapp.domain.repository.StudentDashboardRepository

class GetAssignmentRemindersUseCase(
    private val repository: StudentDashboardRepository
) {
    suspend operator fun invoke(
        studentId: Int? = null,
        classId: Int? = null,
        dueInHours: Int = 48,
        page: Int = 0,
        size: Int = 20
    ): ApiResult<PaginationResponse<AssignmentReminder>> {
        return repository.getAssignmentReminders(studentId, classId, dueInHours, page, size)
    }
}
