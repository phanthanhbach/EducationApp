package com.example.educationapp.domain.enums

import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.lb_status_upcoming
import educationapp.shared.generated.resources.lb_status_ongoing
import educationapp.shared.generated.resources.lb_status_completed
import educationapp.shared.generated.resources.lb_status_cancelled
import org.jetbrains.compose.resources.StringResource

/**
 * Trạng thái của lớp học, đi kèm tài nguyên chuỗi bản dịch (StringResource).
 */
enum class ClassStatus(val labelRes: StringResource) {
    UPCOMING(Res.string.lb_status_upcoming),
    ONGOING(Res.string.lb_status_ongoing),
    COMPLETED(Res.string.lb_status_completed),
    CANCELLED(Res.string.lb_status_cancelled);

    companion object {
        fun fromString(value: String?): ClassStatus? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}
