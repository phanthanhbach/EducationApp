package com.example.educationapp.core.ui.dialog

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.button.AppButton
import com.example.educationapp.core.ui.button.AppTextButton
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.util.liquidGlass
import com.example.educationapp.presentation.screen.main.LocalSharedHazeState

/**
 * A standard, high-quality Alert Dialog component for warnings, confirmations, and actions.
 */
@Composable
fun AppAlertDialog(
    title: String,
    description: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    isConfirmDestructive: Boolean = false
) {
    val sharedHazeState = LocalSharedHazeState.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            AppText(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            AppText(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            AppButton(
                text = confirmText,
                onClick = onConfirm,
                modifier = Modifier.height(AppDimen.p40),
                shape = RoundedCornerShape(AppDimen.p45),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isConfirmDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    contentColor = if (isConfirmDestructive) {
                        MaterialTheme.colorScheme.onError
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    }
                ),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        },
        dismissButton = {
            AppTextButton(
                modifier = Modifier.height(AppDimen.p40),
                text = dismissText,
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier.liquidGlass(
            shape = RoundedCornerShape(AppDimen.p28),
            hazeState = sharedHazeState,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        )
    )
}
