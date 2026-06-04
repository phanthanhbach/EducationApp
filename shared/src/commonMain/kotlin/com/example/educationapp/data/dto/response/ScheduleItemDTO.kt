package com.example.educationapp.data.dto.response

import com.example.educationapp.domain.entity.ScheduleItem
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleItemDTO(
    val classId: Long,
    val sessionNumber: Int,
    val schoolClassId: Long,
    val className: String,
    val roomId: Long,
    val roomName: String,
    val startTime: String,
    val endTime: String,
    val notes: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

fun ScheduleItemDTO.toScheduleItemEntity(): ScheduleItem {
    return ScheduleItem(
        classId = classId,
        sessionNumber = sessionNumber,
        schoolClassId = schoolClassId,
        className = className,
        roomId = roomId,
        roomName = roomName,
        startTime = startTime,
        endTime = endTime,
        notes = notes
    )
}
