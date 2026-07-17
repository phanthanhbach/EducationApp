package com.example.educationapp.domain.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.core.file.UploadFile
import com.example.educationapp.domain.entity.Assignment
import com.example.educationapp.domain.entity.AssignmentSubmission
import com.example.educationapp.domain.entity.StudentAssignment
import com.example.educationapp.domain.entity.SubmissionDetail

interface AssignmentRepository {
    suspend fun filterAssignments(
        classId: Int,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<Assignment>>

    suspend fun getMyAssignmentsFiltered(
        classId: Int,
        submitted: Boolean,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<StudentAssignment>>

    suspend fun submitAssignment(
        assignmentId: Int,
        classId: Int,
        studentId: Int,
        file: UploadFile
    ): ApiResult<AssignmentSubmission>

    suspend fun filterSubmissions(
        assignmentId: Int,
        classId: Int,
        submitted: Boolean? = null,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<SubmissionDetail>>

    suspend fun gradeSubmission(
        classId: Int,
        studentId: Int,
        assignmentId: Int,
        score: Double,
        comment: String
    ): ApiResult<AssignmentSubmission>
}
