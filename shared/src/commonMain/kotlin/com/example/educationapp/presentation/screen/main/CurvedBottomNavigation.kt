package com.example.educationapp.presentation.screen.main

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.ui.text.AppText

/**
 * Data class representing a single bottom navigation tab item.
 */
data class BottomNavItem(
    val title: String,
    val icon: Painter,
    val index: Int
)

/**
 * Custom curved bottom navigation bar with:
 * - Theme-adaptive bar background (surfaceContainer)
 * - Selected tab icon floating above the bar inside a circle matching the screen background
 * - Concave notch/cutout around the selected tab
 * - Theme-adaptive icon and label colors
 * - Title labels below each icon
 */
@Composable
fun CurvedBottomNavigation(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    selectedIconTint: Color = MaterialTheme.colorScheme.primary,
    unselectedIconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    selectedLabelColor: Color = MaterialTheme.colorScheme.primary,
    unselectedLabelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    iconCircleColor: Color = MaterialTheme.colorScheme.background,
    barHeight: Dp = 70.dp,
    floatingCircleSize: Dp = 56.dp,
    floatingOffset: Dp = 28.dp,
    notchRadius: Dp = 36.dp
) {
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight + floatingOffset)
    ) {
        val totalWidth = maxWidth
        val itemWidth = totalWidth / items.size

        // Animated notch position
        val targetNotchCenterX = itemWidth * selectedIndex + itemWidth / 2
        val animatedNotchCenterX by animateDpAsState(
            targetValue = targetNotchCenterX,
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
        )

        // Draw the bar background with notch cutout
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .align(Alignment.BottomCenter)
        ) {
            val notchCenterXPx = with(density) { animatedNotchCenterX.toPx() }
            val notchRadiusPx = with(density) { notchRadius.toPx() }
            val barHeightPx = size.height
            val barWidthPx = size.width

            val path = createNotchedBarPath(
                barWidth = barWidthPx,
                barHeight = barHeightPx,
                notchCenterX = notchCenterXPx,
                notchRadius = notchRadiusPx
            )

            drawPath(path, barColor)
        }

        // Items row positioned at the bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex

                // Each item container
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(barHeight)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onItemSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    if (!isSelected) {
                        // Unselected item: icon + label inside bar
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = item.icon,
                                contentDescription = item.title,
                                tint = unselectedIconTint,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            AppText(
                                text = item.title,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = unselectedLabelColor
                            )
                        }
                    } else {
                        // Selected: only show label at the bottom of the bar area
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.fillMaxSize().padding(bottom = 8.dp)
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            AppText(
                                text = item.title,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = selectedLabelColor
                            )
                        }
                    }
                }
            }
        }

        // Floating selected icon circle (Single circle matching background/app theme)
        val animatedFloatingX by animateDpAsState(
            targetValue = targetNotchCenterX - floatingCircleSize / 2,
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
        )

        Box(
            modifier = Modifier
                .offset(x = animatedFloatingX, y = floatingOffset - floatingCircleSize / 2 + 6.dp)
                .size(floatingCircleSize)
                .clip(CircleShape)
                .background(iconCircleColor),
            contentAlignment = Alignment.Center
        ) {
            val selectedItem = items.getOrNull(selectedIndex)
            if (selectedItem != null) {
                Icon(
                    painter = selectedItem.icon,
                    contentDescription = selectedItem.title,
                    tint = selectedIconTint,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

private fun createNotchedBarPath(
    barWidth: Float,
    barHeight: Float,
    notchCenterX: Float,
    notchRadius: Float
): Path {
    val notchDepth = notchRadius * 0.8f
    val curveWidth = notchRadius * 1.5f

    val notchLeft = notchCenterX - curveWidth
    val notchRight = notchCenterX + curveWidth

    return Path().apply {
        // Start from top-left corner
        moveTo(0f, 0f)

        // Top edge until notch start
        lineTo(notchLeft, 0f)

        // Notch curve - left side going down (positive Y = into the bar)
        cubicTo(
            x1 = notchLeft + notchRadius * 0.6f,
            y1 = 0f,
            x2 = notchCenterX - notchRadius * 0.8f,
            y2 = notchDepth,
            x3 = notchCenterX,
            y3 = notchDepth
        )

        // Notch curve - right side going back up
        cubicTo(
            x1 = notchCenterX + notchRadius * 0.8f,
            y1 = notchDepth,
            x2 = notchRight - notchRadius * 0.6f,
            y2 = 0f,
            x3 = notchRight,
            y3 = 0f
        )

        // Top edge after notch until top-right corner
        lineTo(barWidth, 0f)

        // Right edge
        lineTo(barWidth, barHeight)

        // Bottom edge
        lineTo(0f, barHeight)

        // Close path back to top-left
        close()
    }
}
