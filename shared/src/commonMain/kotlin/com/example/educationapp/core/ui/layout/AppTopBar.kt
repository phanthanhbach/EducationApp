package com.example.educationapp.core.ui.layout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_arrow_back_24dp
import com.example.educationapp.presentation.screen.main.LocalIsTablet

/**
 * A highly reusable, premium Top App Bar layout built on top of ThreeSectionRow.
 * Supports customizable leading, center, and trailing layouts with automatic centering logic.
 * Supports scrolling behaviors like dynamic Y translation with smooth dual-snap gravity
 * and premium hardware-accelerated frosted-glass backdrop blur when scrolled.
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
    containerColor: Color = MaterialTheme.colorScheme.surface,
    scrollState: ScrollState? = null,
    hazeState: HazeState? = null
) {
    var topBarHeightPx by remember { mutableStateOf(0) }
    var previousScrollOffset by remember { mutableStateOf(0) }
    val translationYAnimatable = remember { Animatable(0f) }

    if (scrollState != null) {
        // Track scroll delta for show/hide
        LaunchedEffect(scrollState.value, topBarHeightPx) {
            val currentOffset = scrollState.value
            val delta = currentOffset - previousScrollOffset
            previousScrollOffset = currentOffset

            if (currentOffset > 0) {
                val newTranslationY = (translationYAnimatable.value - delta)
                    .coerceIn(-topBarHeightPx.toFloat(), 0f)
                translationYAnimatable.snapTo(newTranslationY)
            } else {
                translationYAnimatable.snapTo(0f)
            }
        }

        // Snap/gravity: when scrolling stops, settle smoothly.
        // - Near the top (scrollState < topBarHeight): Snap the scroll state to snap content and bar together.
        // - Far down (scrollState >= topBarHeight): Snap only the TopBar translation for ultra-smoothness.
        LaunchedEffect(scrollState, topBarHeightPx) {
            snapshotFlow { scrollState.isScrollInProgress }
                .collect { isScrolling ->
                    if (!isScrolling && topBarHeightPx > 0) {
                        // Wait 1 frame (16ms) to let any final scroll value updates propagate to translationY
                        kotlinx.coroutines.delay(16)

                        val currentY = translationYAnimatable.value
                        val threshold = -topBarHeightPx.toFloat() / 2f
                        val targetY = if (currentY > threshold) 0f else -topBarHeightPx.toFloat()

                        val isCloseToTarget = kotlin.math.abs(currentY - targetY) < 2f
                        if (!isCloseToTarget) {
                            if (scrollState.value < topBarHeightPx) {
                                // 1. Near the top: snap the list scroll state to settle the top content spacer cleanly
                                val targetScrollValue = if (targetY == 0f) 0 else topBarHeightPx
                                if (scrollState.value != targetScrollValue) {
                                    scrollState.animateScrollTo(
                                        value = targetScrollValue,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioNoBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        )
                                    )
                                }
                            } else {
                                // 2. Scrolled down: animate only the TopBar translation directly.
                                // This keeps the list static (no jumps/stutters) and slides the bar smoothly.
                                translationYAnimatable.animateTo(
                                    targetValue = targetY,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                            }
                        } else {
                            translationYAnimatable.snapTo(targetY)
                        }
                    }
                }
        }
    }

    val resolvedHazeState = hazeState ?: LocalTopBarHazeState.current
    val isScrolled = scrollState?.let { it.value > 0 } ?: false

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.translationY = translationYAnimatable.value
            }
            .onGloballyPositioned { coordinates ->
                topBarHeightPx = coordinates.size.height
            }
            .clipToBounds()
    ) {
        // Layer 1: Backdrop blur or Frosted-glass mock background
        if (isScrolled) {
            if (resolvedHazeState != null) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .hazeEffect(state = resolvedHazeState) {
                            blurEffect {
                                blurRadius = 20.dp
                                colorEffects = listOf(
                                    HazeColorEffect.tint(containerColor.copy(alpha = 0.5f))
                                )
                            }
                        }
                )
            } else {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    containerColor.copy(alpha = 0.96f),
                                    containerColor.copy(alpha = 0.88f)
                                )
                            )
                        )
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(containerColor)
            )
        }

        // Layer 2: Top bar content
        Column(
            modifier = Modifier.statusBarsPadding()
        ) {
            val isTablet = LocalIsTablet.current
            val horizontalPadding = if (isTablet) AppDimen.p24 else AppDimen.p16

            Box(modifier = Modifier.fillMaxWidth()) {

                ThreeSectionRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .padding(horizontal = horizontalPadding, vertical = AppDimen.p8),
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

            if (isScrolled) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    thickness = 0.5.dp
                )
            }
        }
    }
}
