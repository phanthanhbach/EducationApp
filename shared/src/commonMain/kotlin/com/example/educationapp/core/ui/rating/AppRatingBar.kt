package com.example.educationapp.core.ui.rating

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.ui.icon.AppIcon
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_star_24dp
import educationapp.shared.generated.resources.ic_star_filled_24dp

/**
 * A reusable rating bar component supporting both display and interactive rating input.
 */
@Composable
fun AppRatingBar(
    rating: Int,
    modifier: Modifier = Modifier,
    onRatingChanged: ((Int) -> Unit)? = null,
    maxStars: Int = 5,
    starSize: Dp = 24.dp,
    spacing: Dp = 8.dp,
    filledColor: Color = AppColor.Tertiary,
    unfilledColor: Color = MaterialTheme.colorScheme.outline
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isFilled = i <= rating
            val starIcon = if (isFilled) Res.drawable.ic_star_filled_24dp else Res.drawable.ic_star_24dp
            val tint = if (isFilled) filledColor else unfilledColor

            AppIcon(
                drawableRes = starIcon,
                iconModifier = Modifier.size(starSize),
                tint = tint,
                onClick = onRatingChanged?.let { { it(i) } }
            )
        }
    }
}
