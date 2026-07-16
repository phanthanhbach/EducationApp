package com.example.educationapp.data.dto.response

import com.example.educationapp.domain.entity.SubmissionDetail
import kotlinx.serialization.Serializable

@Serializable
data class SubmissionFilterDTO(
    val assignmentId: Int,
    val classId: Int,
    val studentId: Int,
    val studentName: String,
    val title: String,
    val description: String? = null,
    val dueDate: String,
    val assignmentFileAttachment: String? = null,
    val finalExam: Boolean = false,
    val submitted: Boolean,
    val submissionStatus: String? = null,
    val fileAttachment: String? = null,
    val submittedAt: String? = null,
    val score: Double? = null
)

fun SubmissionFilterDTO.toDomainEntity() = SubmissionDetail(
    assignmentId = assignmentId,
    classId = classId,
    studentId = studentId,
    studentName = studentName,
    title = title,
    description = description,
    dueDate = dueDate,
    assignmentFileAttachment = assignmentFileAttachment,
    finalExam = finalExam,
    submitted = submitted,
    submissionStatus = submissionStatus,
    fileAttachment = fileAttachment,
    submittedAt = submittedAt,
    score = score
)
