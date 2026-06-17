package com.example.educationapp.presentation.screen.dashboard.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.modifier.GlassBox
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.TeacherRatingSummary
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.dashboard_feedback_count_label
import educationapp.shared.generated.resources.dashboard_rating_count_label
import educationapp.shared.generated.resources.dashboard_rating_overview
import educationapp.shared.generated.resources.dashboard_rating_overview_desc
import educationapp.shared.generated.resources.ic_assignment_filled_24dp
import educationapp.shared.generated.resources.ic_person_filled_24dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun TeacherRatingSummaryCard(
    ratingSummary: TeacherRatingSummary,
    modifier: Modifier = Modifier
) {
    val averageRating = (ratingSummary.averageRating * 10).roundToInt() / 10.0
    val highlightedStars = averageRating.roundToInt().coerceIn(0, 5)
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(AppDimen.p16),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                SectionHeader(
                    title = stringResource(Res.string.dashboard_rating_overview)
                )
                AppText(
                    text = stringResource(Res.string.dashboard_rating_overview_desc),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(AppDimen.p16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassBox(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(18.dp),
                containerColor = Color.Transparent,
                blurRadius = 0.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.70f),
                                    primaryColor
                                )
                            )
                        )
                        .padding(vertical = AppDimen.p16, horizontal = AppDimen.p12)
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AppText(
                            text = averageRating.toString(),
                            fontSize = 42.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        AppText(
                            text = "/5",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.72f),
                            modifier = Modifier.padding(bottom = 7.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        repeat(5) { index ->
                            val starFraction = when {
                                averageRating >= index + 1 -> 1.0f
                                averageRating <= index -> 0.0f
                                else -> (averageRating - index).toFloat()
                            }
                            RatingStar(fraction = starFraction)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
            ) {
                RatingSummaryMetric(
                    icon = Res.drawable.ic_person_filled_24dp,
                    value = ratingSummary.totalRatings.toString(),
                    label = stringResource(Res.string.dashboard_rating_count_label)
                )
                RatingSummaryMetric(
                    icon = Res.drawable.ic_assignment_filled_24dp,
                    value = ratingSummary.totalFeedback.toString(),
                    label = stringResource(Res.string.dashboard_feedback_count_label)
                )
            }
        }
    }
}

@Composable
private fun RatingStar(
    fraction: Float,
    modifier: Modifier = Modifier
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        AppText(
            text = "★",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.28f)
        )
        if (fraction > 0f) {
            val clipShape = remember(fraction) {
                object : Shape {
                    override fun createOutline(
                        size: Size,
                        layoutDirection: LayoutDirection,
                        density: Density
                    ): Outline {
                        return Outline.Rectangle(
                            Rect(
                                left = 0f,
                                top = 0f,
                                right = size.width * fraction,
                                bottom = size.height
                            )
                        )
                    }
                }
            }
            AppText(
                text = "★",
                fontSize = 16.sp,
                color = AppColor.Tertiary,
                modifier = Modifier.clip(clipShape)
            )
        }
    }
}

@Composable
private fun RatingSummaryMetric(
    icon: DrawableResource,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    GlassBox(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        containerColor = Color.Transparent,
        blurRadius = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.70f),
                            primaryColor
                        )
                    )
                )
                .padding(AppDimen.p12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(
                    drawableRes = icon,
                    tint = Color.White,
                    iconModifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                AppText(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                AppText(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.72f)
                )
            }
        }
    }
}
