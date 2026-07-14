package com.example.educationapp.presentation.screen.setting.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.getPlatform
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.app_description_line1
import educationapp.shared.generated.resources.app_description_line2
import educationapp.shared.generated.resources.app_name
import educationapp.shared.generated.resources.app_version_format
import educationapp.shared.generated.resources.ic_launcher
import org.jetbrains.compose.resources.stringResource

/**
 * AboutAppSection displays information about the application as a card in the settings screen.
 * It dynamically fetches the app version from the platform.
 */
@Composable
fun AboutAppSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AppDimen.p16, horizontal = AppDimen.p16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo
        AppIcon(
            drawableRes = Res.drawable.ic_launcher,
            tint = MaterialTheme.colorScheme.primary,
            iconModifier = Modifier.size(AppDimen.p56),
            boxModifier = Modifier.size(AppDimen.p64)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
        )

        Spacer(modifier = Modifier.height(AppDimen.p12))

        // App Name
        AppText(
            text = stringResource(Res.string.app_name),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        // App Version
        val appVersion = getPlatform().appVersion
        AppText(
            text = stringResource(Res.string.app_version_format, appVersion),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(AppDimen.p16))

        // App Description Line 1
        AppText(
            text = stringResource(Res.string.app_description_line1),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(AppDimen.p4))

        // App Description Line 2
        AppText(
            text = stringResource(Res.string.app_description_line2),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}
