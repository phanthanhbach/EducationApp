package com.example.educationapp.core.ui.textfield

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.example.educationapp.core.ui.button.AppTextButton
import com.example.educationapp.core.ui.icon.AppIcon
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_calendar_month_filled_24dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerTextField(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    errorMessage: String? = null
) {
    var showDialog by remember { mutableStateOf(false) }

    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val containerSize = windowInfo.containerSize
    val windowWidthDp = with(density) { containerSize.width.toDp() }
    val windowHeightDp = with(density) { containerSize.height.toDp() }

    // Phone in landscape has small vertical space (height < 500.dp) and width > height
    val isPhoneLandscape = windowHeightDp < 500.dp && windowWidthDp > windowHeightDp

    val formattedDate = remember(selectedDate) {
        selectedDate?.let { date ->
            val day = date.day.toString().padStart(2, '0')
            val month = date.month.number.toString().padStart(2, '0')
            val year = date.year
            "$day/$month/$year"
        } ?: ""
    }

    // Trigger dialog on click by observing interaction source
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                if (enabled) {
                    showDialog = true
                }
            }
        }
    }

    Box(modifier = modifier) {
        AppTextField(
            value = formattedDate,
            onValueChange = {},
            label = label,
            labelStyle = AppTextFieldLabelStyle.External,
            readOnly = true,
            enabled = enabled,
            interactionSource = interactionSource,
            errorMessage = errorMessage,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                AppIcon(
                    drawableRes = Res.drawable.ic_calendar_month_filled_24dp,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    onClick = {
                        if (enabled) {
                            showDialog = true
                        }
                    }
                )
            }
        )

        if (showDialog) {
            val initialEpochMillis = remember(selectedDate) {
                selectedDate?.toEpochDays()?.times(86400000L)
            }

            val datePickerState = key(isPhoneLandscape) {
                rememberDatePickerState(
                    initialSelectedDateMillis = initialEpochMillis,
                    initialDisplayMode = if (isPhoneLandscape) DisplayMode.Input else DisplayMode.Picker
                )
            }

            DatePickerDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    AppTextButton(
                        text = "OK",
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val selectedEpochDays = millis / 86400000L
                                val localDate = LocalDate.fromEpochDays(selectedEpochDays.toInt())
                                onDateSelected(localDate)
                            }
                            showDialog = false
                        }
                    )
                },
                dismissButton = {
                    AppTextButton(
                        text = "Cancel",
                        onClick = { showDialog = false }
                    )
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false
                )
            }
        }
    }
}
