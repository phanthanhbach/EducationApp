package com.example.educationapp.core.ui.row

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.layout.ThreeSectionRow
import com.example.educationapp.core.ui.text.AppText
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_arrow_forward_ios_24dp
import org.jetbrains.compose.resources.DrawableResource

/**
 * A highly customizable option row component containing a leading icon (optional background),
 * a text area with title and optional description, and a trailing arrow icon or custom content.
 */
@Composable
fun OptionRow(
    title: String,
    iconRes: DrawableResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    iconBgColor: Color? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    trailing: (@Composable () -> Unit)? = null
) {
    val rowModifier = modifier
            .fillMaxWidth()
            .then(
                if (trailing == null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
            .padding(horizontal = AppDimen.p16, vertical = AppDimen.p16)
    ThreeSectionRow(
        modifier = rowModifier,
        spacing = AppDimen.p16,
        verticalAlignment = Alignment.CenterVertically,
        first = {
            val iconBoxModifier = if (iconBgColor != null) {
                Modifier
                    .size(48.dp)
                    .background(iconBgColor, CircleShape)
            } else {
                Modifier
            }
            AppIcon(
                drawableRes = iconRes,
                tint = iconTint,
                iconModifier = Modifier.size(24.dp),
                boxModifier = iconBoxModifier
            )
        },
        second = {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                AppText(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (description != null) {
                    AppText(
                        text = description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        third = {
            if (trailing != null) {
                trailing()
            } else {
                AppIcon(
                    drawableRes = Res.drawable.ic_arrow_forward_ios_24dp,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    iconModifier = Modifier.size(16.dp)
                )
            }
        }
    )
}
