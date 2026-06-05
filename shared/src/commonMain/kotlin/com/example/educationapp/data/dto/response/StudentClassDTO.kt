package com.example.educationapp.data.dto.response

import com.example.educationapp.domain.entity.SchoolClass
import kotlinx.serialization.Serializable

@Serializable
data class StudentClassDTO(
    val classId: Long,
    val studentId: Long,
    val studentName: String? = null,
    val className: String,
    val courseName: String,
    val teacherEmail: String? = null,
    val teacherPhone: String? = null,
    val enrolledDate: String? = null,
    val status: String,
    val feedbackRating: String? = null,
    val feedbackComment: String? = null,
    val feedbackAt: String? = null,
    val teacherFeedback: String? = null,
    val teacherFeedbackDate: String? = null,
    val finalResult: String? = null,
    val resultNote: String? = null,
    val resultDate: String? = null,
    val notifiedAt: String? = null,
    val renewedToClassId: Long? = null,
    val renewedAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

fun StudentClassDTO.toSchoolClass() = SchoolClass(
    id = classId,
    name = className,
    courseId = 0,
    courseName = courseName,
    teacherId = 0,
    teacherName = teacherEmail ?: "",
    branchId = 0,
    branchName = "",
    maxStudents = 0,
    currentStudents = 0,
    status = status,
    startDate = enrolledDate ?: "",
    endDate = "",
    zaloGroupLink = null,
    zaloGroupName = null,
    finalResult = finalResult
)
