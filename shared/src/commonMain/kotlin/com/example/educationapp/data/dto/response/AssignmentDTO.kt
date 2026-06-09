package com.example.educationapp.data.dto.response

import com.example.educationapp.domain.entity.Assignment
import kotlinx.serialization.Serializable

@Serializable
data class AssignmentDTO(
    val id: Int,
    val title: String,
    val description: String? = null,
    val classId: Int,
    val className: String,
    val courseId: Int,
    val courseName: String,
    val fileAttachment: String? = null,
    val dueDate: String,
    val active: Boolean,
    val finalExam: Boolean,
    val submittedCount: Int,
    val notSubmittedCount: Int
)

fun AssignmentDTO.toDomainEntity() = Assignment(
    id = id,
    title = title,
    description = description,
    classId = classId,
    className = className,
    courseId = courseId,
    courseName = courseName,
    fileAttachment = fileAttachment,
    dueDate = dueDate,
    active = active,
    finalExam = finalExam,
    submittedCount = submittedCount,
    notSubmittedCount = notSubmittedCount
)
