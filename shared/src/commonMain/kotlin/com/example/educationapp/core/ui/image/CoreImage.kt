package com.example.educationapp.core.ui.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.educationapp.core.util.optional
import org.jetbrains.compose.resources.painterResource

@Composable
fun CoreImage(
    source: CoreMediaSource,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: Painter? = null,
    error: Painter? = null,
    onClick: (() -> Unit)? = null,
    onError: (() -> Unit)? = null
) {
    val context = LocalPlatformContext.current
    val combinedModifier = modifier.optional(onClick) { clickable { it.invoke() } }

    when (source) {
        is CoreMediaSource.ComposeResource -> {
            Image(
                painter = painterResource(source.resource),
                contentDescription = contentDescription,
                modifier = combinedModifier,
                contentScale = contentScale
            )
        }
        is CoreMediaSource.Bitmap -> {
            Image(
                bitmap = source.bitmap,
                contentDescription = contentDescription,
                modifier = combinedModifier,
                contentScale = contentScale
            )
        }
        is CoreMediaSource.Url, is CoreMediaSource.LocalPath -> {
            val model = remember(source) {
                when (source) {
                    is CoreMediaSource.Url -> source.url
                    is CoreMediaSource.LocalPath -> source.path
                }
            }

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(model)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                modifier = combinedModifier,
                contentScale = contentScale,
                placeholder = placeholder,
                error = error,
                onError = { onError?.invoke() }
            )
        }
        is CoreMediaSource.None -> {
            if (placeholder != null) {
                Image(
                    painter = placeholder,
                    contentDescription = contentDescription,
                    modifier = combinedModifier,
                    contentScale = contentScale
                )
            }
        }
    }
}
