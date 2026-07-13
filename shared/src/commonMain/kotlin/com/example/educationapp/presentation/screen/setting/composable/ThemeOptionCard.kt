package com.example.educationapp.presentation.screen.setting.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.enums.AppThemeMode
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_check_24dp
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun ThemeOptionCard(
    themeMode: AppThemeMode,
    label: String,
    icon: DrawableResource,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .then(
                    if (selected) {
                        Modifier.border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                )
                .clip(RoundedCornerShape(12.dp))
        ) {
            ThemeMockupPreview(themeMode = themeMode)

            if (selected) {
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .size(16.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(
                        drawableRes = Res.drawable.ic_check_24dp,
                        tint = Color.White,
                        iconModifier = Modifier.size(10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AppIcon(
                drawableRes = icon,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                iconModifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            AppText(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ThemeMockupPreview(
    themeMode: AppThemeMode,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        when (themeMode) {
            AppThemeMode.LIGHT -> {
                ModePreviewContent(isDark = false)
            }

            AppThemeMode.DARK -> {
                ModePreviewContent(isDark = true)
            }

            AppThemeMode.SYSTEM -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().clipToBounds()) {
                        ModePreviewContent(isDark = false)
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().clipToBounds()) {
                        ModePreviewContent(isDark = true)
                    }
                }
            }
        }
    }
}

@Composable
private fun ModePreviewContent(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isDark) Color(0xFF161925) else Color(0xFFE2E4E9)
    val headerBgColor = if (isDark) Color(0xFF0F111A) else Color(0xFFD5D7DD)
    val headerDotColor = if (isDark) Color(0xFF25293C) else Color(0xFFB8BAC2)
    val cardBgColor = if (isDark) Color(0xFF1F2232) else Color(0xFFFFFFFF)
    val cardLineColor = if (isDark) Color(0xFF333852) else Color(0xFFE2E4E9)
    val primaryAccent = if (isDark) Color(0xFF8E97FD) else Color(0xFF475AD7)
    val secondaryAccent = Color(0xFF2ED33C)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .background(headerBgColor)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(headerDotColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .height(3.dp)
                    .width(20.dp)
                    .background(headerDotColor, RoundedCornerShape(1.5.dp))
            )
        }

        // Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PreviewCardItem(
                cardBgColor = cardBgColor,
                accentColor = primaryAccent,
                lineColor = cardLineColor
            )
            PreviewCardItem(
                cardBgColor = cardBgColor,
                accentColor = secondaryAccent,
                lineColor = cardLineColor
            )
        }
    }
}

@Composable
private fun PreviewCardItem(
    cardBgColor: Color,
    accentColor: Color,
    lineColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(18.dp)
            .background(cardBgColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .height(4.dp)
                .width(10.dp)
                .background(accentColor, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .height(3.dp)
                .weight(1f)
                .background(lineColor, RoundedCornerShape(1.5.dp))
        )
    }
}
