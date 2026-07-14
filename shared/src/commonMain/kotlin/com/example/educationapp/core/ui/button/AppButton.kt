package com.example.educationapp.core.ui.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText

/**
 * Base Filled Button for the application. Supporting custom shape, colors, loading indicator,
 * icons, and customized chromatic shadows.
 *
 * @param text The text to display inside the button.
 * @param onClick Callback when the button is clicked.
 * @param modifier Modifier to be applied to the button layout.
 * @param enabled Controls the enabled state of the button.
 * @param isLoading Renders a loading spinner instead of text when true.
 * @param allCaps Displays the button text in all capital letters when true.
 * @param leadingIcon Optional leading icon composable to display before the text.
 * @param trailingIcon Optional trailing icon composable to display after the text.
 * @param shape Shape outline of the button.
 * @param containerColor Custom background color. If provided, disabled states are generated automatically.
 * @param contentColor Custom text/icon color. If provided, disabled states are generated automatically.
 * @param colors Full custom ButtonColors configuration. Takes precedence over containerColor/contentColor.
 * @param shadowColor Custom color of the shadow. Defaults to active container color if null.
 * @param elevation Depth of the custom chromatic shadow.
 * @param style Typography style for the button text.
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    allCaps: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(AppDimen.p12),
    containerColor: Color? = null,
    contentColor: Color? = null,
    colors: ButtonColors? = null,
    shadowColor: Color? = null,
    elevation: Dp = AppDimen.zero,
    style: TextStyle = MaterialTheme.typography.titleMedium
) {
    val resolvedColors = colors ?: ButtonDefaults.buttonColors(
        containerColor = containerColor ?: MaterialTheme.colorScheme.primary,
        contentColor = contentColor ?: MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = (containerColor
            ?: MaterialTheme.colorScheme.primary).copy(alpha = 0.5f),
        disabledContentColor = (contentColor
            ?: MaterialTheme.colorScheme.onPrimary).copy(alpha = 0.5f)
    )

    val hasCustomShadow = elevation > AppDimen.zero && enabled && !isLoading

    val currentBgColor = if (colors != null) {
        if (enabled && !isLoading) colors.containerColor else colors.disabledContainerColor
    } else {
        val base = containerColor ?: MaterialTheme.colorScheme.primary
        if (enabled && !isLoading) base else base.copy(alpha = 0.5f)
    }

    val resolvedShadowColor = shadowColor ?: currentBgColor

    val (buttonModifier, finalColors) = if (hasCustomShadow) {
        val finalModifier = modifier
            .height(AppDimen.p50)
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = resolvedShadowColor,
                spotColor = resolvedShadowColor
            )
            .background(color = currentBgColor, shape = shape)
        val transparentColors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = resolvedColors.contentColor,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = resolvedColors.disabledContentColor
        )
        finalModifier to transparentColors
    } else {
        modifier.height(AppDimen.p50) to resolvedColors
    }

    Button(
        onClick = onClick,
        modifier = buttonModifier,
        enabled = enabled && !isLoading,
        shape = shape,
        colors = finalColors,
        elevation = null,
        contentPadding = PaddingValues(horizontal = AppDimen.p16)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = LocalContentColor.current,
                modifier = Modifier.size(AppDimen.p24),
                strokeWidth = AppDimen.p2
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(AppDimen.p8))
                }
                AppText(
                    text = text,
                    style = style,
                    color = LocalContentColor.current,
                    allCaps = allCaps
                )
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(AppDimen.p8))
                    trailingIcon()
                }
            }
        }
    }
}

/**
 * Outlined Button wrapper.
 */
@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    allCaps: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(AppDimen.p12),
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled),
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    ),
    style: TextStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(AppDimen.p50),
        enabled = enabled && !isLoading,
        shape = shape,
        border = border,
        colors = colors,
        contentPadding = PaddingValues(horizontal = AppDimen.p16)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = LocalContentColor.current,
                modifier = Modifier.size(AppDimen.p24),
                strokeWidth = AppDimen.p2
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(AppDimen.p8))
                }
                AppText(
                    text = text,
                    style = style,
                    color = LocalContentColor.current,
                    allCaps = allCaps
                )
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(AppDimen.p8))
                    trailingIcon()
                }
            }
        }
    }
}

/**
 * Text Button wrapper.
 */
@Composable
fun AppTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    allCaps: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    colors: ButtonColors = ButtonDefaults.textButtonColors(
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
    )
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        contentPadding = PaddingValues(horizontal = AppDimen.p16)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(AppDimen.p4))
            }
            AppText(
                text = text,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = LocalContentColor.current,
                allCaps = allCaps
            )
            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(AppDimen.p4))
                trailingIcon()
            }
        }
    }
}
