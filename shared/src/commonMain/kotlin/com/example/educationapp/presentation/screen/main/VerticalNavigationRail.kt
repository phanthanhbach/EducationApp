package com.example.educationapp.presentation.screen.main

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.ui.text.AppText

/**
 * Premium vertical navigation rail for tablets and landscape screens.
 * Features a modern, clean sidebar layout:
 * - Selected tab has a soft rounded background container behind the icon (no overlapping text).
 * - Animated accent indicator line on the left edge.
 * - Clean text labels positioned strictly below the icons.
 * - Responsive sizing for low-height phone screens.
 */
@Composable
fun VerticalNavigationRail(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    selectedIconTint: Color = MaterialTheme.colorScheme.primary,
    unselectedIconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    selectedLabelColor: Color = MaterialTheme.colorScheme.primary,
    unselectedLabelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    indicatorColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
    width: Dp = 76.dp
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxHeight()
            .width(width)
            .background(barColor)
    ) {
        val isCompactHeight = maxHeight < 450.dp

        val paddingTop = if (isCompactHeight) 16.dp else 32.dp
        val itemHeight = if (isCompactHeight) 56.dp else 72.dp
        val itemSpacing = if (isCompactHeight) 8.dp else 16.dp
        val yStart = paddingTop

        // Smooth vertical sliding indicator on the very left edge of the rail
        val indicatorHeight = if (isCompactHeight) 20.dp else 24.dp
        val targetIndicatorY =
            yStart + (itemHeight + itemSpacing) * selectedIndex + (itemHeight - indicatorHeight) / 2
        val animatedIndicatorY by animateDpAsState(
            targetValue = targetIndicatorY,
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
        )

        // Accent indicator bar on the left edge
        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = animatedIndicatorY)
                .width(4.dp)
                .height(indicatorHeight)
                .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                .background(selectedIconTint)
        )

        // Navigation Items Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingTop),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex

                Column(
                    modifier = Modifier
                        .width(width)
                        .height(itemHeight)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onItemSelected(index) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Icon Box with a soft rounded background when selected (does not overlap title)
                    Box(
                        modifier = Modifier
                            .size(if (isCompactHeight) 36.dp else 44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) indicatorColor else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) selectedIconTint else unselectedIconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Text Label (Always positioned below the icon box)
                    AppText(
                        text = item.title,
                        fontSize = if (isCompactHeight) 9.sp else 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) selectedLabelColor else unselectedLabelColor
                    )
                }

                if (index < items.size - 1) {
                    Spacer(modifier = Modifier.height(itemSpacing))
                }
            }
        }
    }
}
