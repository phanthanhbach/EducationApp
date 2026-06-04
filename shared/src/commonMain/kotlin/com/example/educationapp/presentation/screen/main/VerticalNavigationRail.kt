package com.example.educationapp.presentation.screen.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.ui.text.AppText
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_arrow_back_24dp
import educationapp.shared.generated.resources.ic_menu_24dp
import org.jetbrains.compose.resources.painterResource

/**
 * Expandable vertical navigation rail for tablet and landscape layouts.
 *
 * Collapsed: menu button + tab icons.
 * Expanded: keeps the same icon rail and reveals tab titles beside each icon.
 */
@Composable
fun VerticalNavigationRail(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {},
    barColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    selectedIconTint: Color = MaterialTheme.colorScheme.primary,
    unselectedIconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    selectedLabelColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    unselectedLabelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    indicatorColor: Color = MaterialTheme.colorScheme.primary,
    collapsedWidth: Dp = 76.dp,
    expandedWidth: Dp = 212.dp
) {
    // System insets so the background fills edge-to-edge while content stays safe.
    val layoutDirection = LocalLayoutDirection.current
    val systemInsets = WindowInsets.systemBars.asPaddingValues()
    val topInset = systemInsets.calculateTopPadding()
    val startInset = systemInsets.calculateStartPadding(layoutDirection)

    val surfaceWidth by animateDpAsState(
        targetValue = if (isExpanded) expandedWidth + startInset else collapsedWidth + startInset,
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
    )
    val expandedProgress by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxHeight()
            .width(expandedWidth + startInset)
            .clipToBounds()
    ) {
        val isCompactHeight = maxHeight < 450.dp
        val sidePadding = 10.dp
        val paddingTop = if (isCompactHeight) 14.dp else 24.dp
        val menuSize = 42.dp
        val afterMenuSpacing = if (isCompactHeight) 16.dp else 28.dp
        val itemHeight = if (isCompactHeight) 48.dp else 56.dp
        val itemSpacing = if (isCompactHeight) 8.dp else 12.dp
        val contentTop = topInset + paddingTop
        val navStartY = contentTop + menuSize + afterMenuSpacing

        val indicatorHeight = if (isCompactHeight) 28.dp else 34.dp
        val selectedPillY = navStartY + (itemHeight + itemSpacing) * selectedIndex
        val targetIndicatorY =
            selectedPillY + (itemHeight - indicatorHeight) / 2
        val animatedSelectedPillY by animateDpAsState(
            targetValue = selectedPillY,
            animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
        )
        val animatedIndicatorY by animateDpAsState(
            targetValue = targetIndicatorY,
            animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
        )

        // Background – extends edge-to-edge (behind status bar & left safe area)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(surfaceWidth)
                .background(barColor)
        )

        // Left-edge indicator strip – offset by startInset
        Box(
            modifier = Modifier
                .offset(x = startInset, y = animatedIndicatorY)
                .width(4.dp)
                .height(indicatorHeight)
                .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                .background(indicatorColor)
        )

        // Selected-item pill – offset by startInset
        Box(
            modifier = Modifier
                .offset(x = startInset + sidePadding, y = animatedSelectedPillY)
                .width(surfaceWidth - startInset - sidePadding * 2)
                .height(itemHeight)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.86f))
        )

        // Content column – padded by topInset and startInset so items stay safe
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .requiredWidth(expandedWidth + startInset)
                .padding(
                    top = contentTop,
                    start = startInset + sidePadding,
                    end = sidePadding
                ),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier.width(collapsedWidth - sidePadding * 2),
                contentAlignment = Alignment.Center
            ) {
                MenuToggleButton(
                    isExpanded = isExpanded,
                    buttonSize = menuSize,
                    tint = MaterialTheme.colorScheme.onSurface,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    onExpandedChange(!isExpanded)
                }
            }

            Spacer(modifier = Modifier.height(afterMenuSpacing))

            items.forEachIndexed { index, item ->
                VerticalNavigationItem(
                    item = item,
                    isSelected = index == selectedIndex,
                    expandedProgress = expandedProgress,
                    height = itemHeight,
                    width = surfaceWidth - startInset - sidePadding * 2,
                    collapsedWidth = collapsedWidth - sidePadding * 2,
                    selectedIconTint = selectedIconTint,
                    unselectedIconTint = unselectedIconTint,
                    selectedLabelColor = selectedLabelColor,
                    unselectedLabelColor = unselectedLabelColor,
                    onClick = { onItemSelected(index) }
                )

                if (index < items.lastIndex) {
                    Spacer(modifier = Modifier.height(itemSpacing))
                }
            }
        }
    }
}

@Composable
private fun MenuToggleButton(
    isExpanded: Boolean,
    buttonSize: Dp,
    tint: Color,
    containerColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(buttonSize)
            .clip(CircleShape)
            .background(containerColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(
                if (isExpanded) {
                    Res.drawable.ic_arrow_back_24dp
                } else {
                    Res.drawable.ic_menu_24dp
                }
            ),
            contentDescription = if (isExpanded) "Collapse navigation" else "Expand navigation",
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun VerticalNavigationItem(
    item: BottomNavItem,
    isSelected: Boolean,
    expandedProgress: Float,
    height: Dp,
    width: Dp,
    collapsedWidth: Dp,
    selectedIconTint: Color,
    unselectedIconTint: Color,
    selectedLabelColor: Color,
    unselectedLabelColor: Color,
    onClick: () -> Unit
) {
    val iconTint by animateColorAsState(
        targetValue = if (isSelected) selectedIconTint else unselectedIconTint,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
    )
    val labelColor by animateColorAsState(
        targetValue = if (isSelected) selectedLabelColor else unselectedLabelColor,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
    )

    Row(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(start = 0.dp, end = 12.dp * expandedProgress),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(collapsedWidth)
                .height(38.dp)
                .clip(RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = item.icon,
                contentDescription = item.title,
                tint = iconTint,
                modifier = Modifier.size(23.dp)
            )
        }

        Row(
            modifier = Modifier.graphicsLayer {
                alpha = expandedProgress
                translationX = (1f - expandedProgress) * -10f
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            AppText(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = labelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
