package com.example.educationapp.core.util

import com.example.educationapp.core.network.ApiResult
import educationapp.shared.generated.resources.*

/**
 * Extension function to map presentation-independent ApiResult.Error into localized UiText.
 * This separates the UI localization framework details from core networking layer logic,
 * complying with Clean Architecture dependencies rules.
 */
fun ApiResult.Error.asUiText(): UiText {
    return when (this) {
        is ApiResult.Error.HttpError -> {
            when (code) {
                401 -> UiText.ResourceString(Res.string.error_unauthorized)
                403 -> UiText.ResourceString(Res.string.error_forbidden)
                404 -> UiText.ResourceString(Res.string.error_not_found)
                500 -> UiText.ResourceString(Res.string.error_server)
                else -> {
                    if (!message.isNullOrBlank()) UiText.DynamicString(message)
                    else UiText.ResourceString(Res.string.error_unknown)
                }
            }
        }
        is ApiResult.Error.NetworkError -> {
            if (isTimeout) UiText.ResourceString(Res.string.error_timeout)
            else UiText.ResourceString(Res.string.error_no_internet)
        }
        is ApiResult.Error.SerializationError -> {
            UiText.ResourceString(Res.string.error_serialization)
        }
        is ApiResult.Error.UnknownError -> {
            UiText.ResourceString(Res.string.error_unknown)
        }
    }
}
