package com.example.educationapp.core.ui.button

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText

/**
 * Base Filled Button for the application.
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
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
    ),
    style: TextStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        enabled = enabled && !isLoading,
        shape = shape,
        colors = colors,
        contentPadding = PaddingValues(horizontal = AppDimen.p16)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = LocalContentColor.current,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.5.dp
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
        modifier = modifier.height(50.dp),
        enabled = enabled && !isLoading,
        shape = shape,
        border = border,
        colors = colors,
        contentPadding = PaddingValues(horizontal = AppDimen.p16)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = LocalContentColor.current,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.5.dp
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
        contentPadding = PaddingValues(horizontal = AppDimen.p8)
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
