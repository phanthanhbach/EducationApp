package com.example.educationapp.data.dto.response

import com.example.educationapp.domain.entity.AssignmentReminder
import kotlinx.serialization.Serializable

@Serializable
data class AssignmentReminderDTO(
    val studentId: Int,
    val studentName: String,
    val assignmentId: Int,
    val title: String,
    val classId: Int,
    val className: String,
    val courseId: Int,
    val courseName: String,
    val dueDate: String,
    val hoursRemaining: Double
)

fun AssignmentReminderDTO.toDomainEntity() = AssignmentReminder(
    studentId = studentId,
    studentName = studentName,
    assignmentId = assignmentId,
    title = title,
    classId = classId,
    className = className,
    courseId = courseId,
    courseName = courseName,
    dueDate = dueDate,
    hoursRemaining = hoursRemaining
)
