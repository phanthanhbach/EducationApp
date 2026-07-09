package com.example.educationapp.presentation.screen.schedule.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.enums.AppRole
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_calendar_month_filled_24dp
import educationapp.shared.generated.resources.schedule_empty_desc
import educationapp.shared.generated.resources.schedule_empty_desc_student
import educationapp.shared.generated.resources.schedule_empty_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmptyScheduleView(
    role: AppRole,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isTablet = maxWidth >= AppDimen.tabletWidth

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(if (isTablet) AppDimen.p12 else AppDimen.p32),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppIcon(
                    drawableRes = Res.drawable.ic_calendar_month_filled_24dp,
                    boxModifier = Modifier
                        .size(if (isTablet) AppDimen.p100 else AppDimen.p50)
                        .clip(RoundedCornerShape(if (isTablet) AppDimen.p20 else AppDimen.p12))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    iconModifier = Modifier.size(if (isTablet) AppDimen.p48 else AppDimen.p28),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(if (isTablet) AppDimen.p24 else AppDimen.p8))

                AppText(
                    text = stringResource(Res.string.schedule_empty_title),
                    fontSize = if (isTablet) AppDimen.s20 else AppDimen.s16,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(AppDimen.p4))

                val emptyDescRes = if (role == AppRole.TEACHER) {
                    Res.string.schedule_empty_desc
                } else {
                    Res.string.schedule_empty_desc_student
                }

                AppText(
                    text = stringResource(emptyDescRes),
                    fontSize = if (isTablet) AppDimen.s14 else AppDimen.s12,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
