package com.example.educationapp.core.ui.icon

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.educationapp.core.util.optional
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun CoreIcon(
    imageVector: ImageVector? = null,
    drawableRes: DrawableResource? = null,
    iconModifier: Modifier = Modifier,
    boxModifier: Modifier = Modifier,
    tint: Color? = null,
    onClick: (() -> Unit)? = null
) {
    val finalModifier = iconModifier.optional(onClick) { clickable { it.invoke() } }

    Box(boxModifier, contentAlignment = Alignment.Center) {
        when {
            drawableRes != null -> {
                Icon(
                    painter = painterResource(drawableRes),
                    contentDescription = null,
                    tint = tint ?: Color.Unspecified,
                    modifier = finalModifier
                )
            }

            imageVector != null -> {
                Icon(
                    imageVector = imageVector,
                    contentDescription = null,
                    tint = tint ?: Color.Unspecified,
                    modifier = finalModifier
                )
            }

            else -> {}
        }
    }
}
