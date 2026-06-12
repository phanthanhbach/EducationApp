package com.example.educationapp.data.dto.response

import com.example.educationapp.domain.entity.AssignmentSubmission
import kotlinx.serialization.Serializable

@Serializable
data class AssignmentSubmissionDTO(
    val classId: Int,
    val studentId: Int,
    val assignmentId: Int,
    val assignmentTitle: String,
    val studentName: String,
    val fileAttachment: String? = null,
    val submittedAt: String? = null,
    val status: String? = null,
    val score: Double? = null,
    val teacherComment: String? = null,
    val createdAt: String? = null
)

fun AssignmentSubmissionDTO.toDomainEntity() = AssignmentSubmission(
    classId = classId,
    studentId = studentId,
    assignmentId = assignmentId,
    assignmentTitle = assignmentTitle,
    studentName = studentName,
    fileAttachment = fileAttachment,
    submittedAt = submittedAt,
    status = status,
    score = score,
    teacherComment = teacherComment,
    createdAt = createdAt
)

