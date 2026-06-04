package com.example.educationapp.data.dto.response

import com.example.educationapp.domain.entity.SchoolClass
import kotlinx.serialization.Serializable

@Serializable
data class SchoolClassDTO(
    val id: Long,
    val name: String,
    val courseId: Long,
    val courseName: String,
    val teacherId: Long,
    val teacherName: String,
    val branchId: Long,
    val branchName: String,
    val maxStudents: Int,
    val currentStudents: Int,
    val status: String,
    val startDate: String,
    val endDate: String,
    val zaloGroupLink: String? = null,
    val zaloGroupName: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

fun SchoolClassDTO.toDomainEntity() = SchoolClass(
    id = id,
    name = name,
    courseId = courseId,
    courseName = courseName,
    teacherId = teacherId,
    teacherName = teacherName,
    branchId = branchId,
    branchName = branchName,
    maxStudents = maxStudents,
    currentStudents = currentStudents,
    status = status,
    startDate = startDate,
    endDate = endDate,
    zaloGroupLink = zaloGroupLink,
    zaloGroupName = zaloGroupName
)
