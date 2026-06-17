package com.example.educationapp.core.ui.textfield

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_keyboard_arrow_down_24dp

@Composable
fun DropdownTextField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    optionLabel: (String) -> String = { it },
    enabled: Boolean = true,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val rotationAngle by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                if (enabled) {
                    expanded = true
                }
            }
        }
    }

    val density = LocalDensity.current

    Box(modifier = modifier) {
        AppTextField(
            value = optionLabel(selectedValue),
            onValueChange = {},
            label = label,
            labelStyle = AppTextFieldLabelStyle.External,
            readOnly = true,
            enabled = enabled,
            interactionSource = interactionSource,
            errorMessage = errorMessage,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
            trailingIcon = {
                AppIcon(
                    drawableRes = Res.drawable.ic_keyboard_arrow_down_24dp,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    iconModifier = Modifier
                        .size(24.dp)
                        .rotate(rotationAngle),
                    onClick = {
                        if (enabled) {
                            expanded = !expanded
                        }
                    }
                )
            }
        )

        if (expanded) {
            val verticalOffset = textFieldSize.height.toInt() + with(density) { 4.dp.roundToPx() }
            Popup(
                onDismissRequest = { expanded = false },
                offset = IntOffset(0, verticalOffset),
                properties = PopupProperties(focusable = true)
            ) {
                Card(
                    modifier = Modifier.width(with(density) { textFieldSize.width.toDp() }),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    AppText(
                                        text = optionLabel(option),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                onClick = {
                                    onValueSelected(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
