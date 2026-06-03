package com.example.educationapp.core.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_arrow_back_24dp

import androidx.compose.foundation.layout.statusBarsPadding

/**
 * A highly reusable, premium Top App Bar layout built on top of ThreeSectionRow.
 * Supports customizable leading, center, and trailing layouts with automatic centering logic.
 */
@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    isTitleCentered: Boolean = true,
    titleContent: @Composable (RowScope.() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    leadingContent: @Composable (RowScope.() -> Unit)? = null,
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .statusBarsPadding()
    ) {
        ThreeSectionRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = AppDimen.p12),
            spacing = AppDimen.p8,
            verticalAlignment = Alignment.CenterVertically,
            first = {
                when {
                    leadingContent != null -> leadingContent()
                    onBackClick != null -> {
                        AppIcon(
                            drawableRes = Res.drawable.ic_arrow_back_24dp,
                            tint = MaterialTheme.colorScheme.onSurface,
                            iconModifier = Modifier.size(24.dp),
                            onClick = onBackClick
                        )
                    }
                    isTitleCentered -> Box(modifier = Modifier.size(24.dp))
                }
            },
            second = {
                val rowScope = this
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = if (isTitleCentered) Alignment.Center else Alignment.CenterStart
                ) {
                    when {
                        titleContent != null -> rowScope.titleContent()
                        title != null -> {
                            AppText(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = if (isTitleCentered) TextAlign.Center else TextAlign.Start
                            )
                        }
                    }
                }
            },
            third = {
                when {
                    trailingContent != null -> trailingContent()
                    isTitleCentered -> Box(modifier = Modifier.size(24.dp))
                }
            }
        )
    }
}
