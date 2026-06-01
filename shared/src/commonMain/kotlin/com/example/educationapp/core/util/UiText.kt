package com.example.educationapp.core.util

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    
    class ResourceString(
        val resource: StringResource,
        vararg val args: Any
    ) : UiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is ResourceString -> stringResource(resource, *args)
        }
    }
}
