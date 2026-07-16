package com.example.educationapp.core.ui.badge

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText

@Composable
fun AppBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    backgroundColor: Color = color.copy(alpha = 0.08f),
    borderColor: Color? = color.copy(alpha = 0.24f),
    shape: Shape = RoundedCornerShape(AppDimen.p100),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = AppDimen.p10,
        vertical = AppDimen.p4
    ),
    fontSize: TextUnit = 11.sp,
    fontWeight: FontWeight = FontWeight.SemiBold,
    maxLines: Int = 1,
    leadingContent: (@Composable () -> Unit)? = null
) {
    val boxModifier = modifier
        .clip(shape)
        .background(backgroundColor)
        .let {
            if (borderColor != null) {
                it.border(BorderStroke(AppDimen.p1, borderColor), shape)
            } else {
                it
            }
        }
        .padding(contentPadding)

    Box(
        modifier = boxModifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimen.p4)
        ) {
            if (leadingContent != null) {
                leadingContent()
            }
            AppText(
                text = text,
                fontSize = fontSize,
                fontWeight = fontWeight,
                color = color,
                maxLines = maxLines
            )
        }
    }
}
