package com.example.educationapp.core.ui.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.ui.image.AppImage
import com.example.educationapp.core.ui.image.CoreMediaSource
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.util.optional
import kotlin.math.absoluteValue

@Composable
fun AppAvatar(
    name: String,
    source: CoreMediaSource,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(
        fontSize = TextUnit.Unspecified,
        fontWeight = FontWeight.Medium,
        color = Color.White
    ),
    onClick: (() -> Unit)? = null
) {
    var hasError by remember(source) { mutableStateOf(source is CoreMediaSource.None) }

    val combinedModifier = modifier
        .clip(CircleShape)
        .optional(onClick) { clickable { it.invoke() } }

    if (hasError) {
        val initials = remember(name) {
            name.split(" ")
                .filter { it.isNotBlank() }
                .takeLast(2)
                .joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
        }

        val colorHash = name.hashCode().absoluteValue
        val bgColors = remember {
            listOf(
                Color(0xFFEF5350), Color(0xFFEC407A), Color(0xFFAB47BC), Color(0xFF7E57C2),
                Color(0xFF5C6BC0), Color(0xFF42A5F5), Color(0xFF26A69A), Color(0xFF26C6DA),
                Color(0xFF66BB6A), Color(0xFF9CCC65), Color(0xFFFFCA28), Color(0xFFFFA726),
                Color(0xFFFF7043), Color(0xFF8D6E63)
            )
        }
        val bgColor = bgColors[colorHash % bgColors.size]

        BoxWithConstraints(
            modifier = combinedModifier.background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            val resolvedFontSize = if (textStyle.fontSize.isSpecified) {
                textStyle.fontSize
            } else {
                (maxWidth.value * 0.35f).sp
            }

            AppText(
                text = initials,
                style = textStyle.copy(fontSize = resolvedFontSize)
            )
        }
    } else {
        AppImage(
            source = source,
            modifier = combinedModifier,
            contentScale = ContentScale.Crop,
            onError = { hasError = true }
        )
    }
}

@Composable
fun AppAvatar(
    name: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(
        fontSize = TextUnit.Unspecified,
        fontWeight = FontWeight.Bold,
        color = Color.White
    ),
    onClick: (() -> Unit)? = null
) {
    val source = remember(imageUrl) {
        if (imageUrl.isNullOrBlank()) CoreMediaSource.None
        else CoreMediaSource.Url(imageUrl)
    }
    AppAvatar(
        name = name,
        source = source,
        modifier = modifier,
        textStyle = textStyle,
        onClick = onClick
    )
}
