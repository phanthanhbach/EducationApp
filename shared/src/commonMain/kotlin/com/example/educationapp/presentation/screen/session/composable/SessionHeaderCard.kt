package com.example.educationapp.presentation.screen.session.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.badge.AppBadge
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.enums.SessionStatus
import com.example.educationapp.presentation.model.ScheduleSessionUiModel
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.session_class_prefix
import educationapp.shared.generated.resources.session_number_prefix
import educationapp.shared.generated.resources.session_status_completed
import educationapp.shared.generated.resources.session_status_ongoing
import educationapp.shared.generated.resources.session_status_upcoming
import kotlin.time.Clock
import org.jetbrains.compose.resources.stringResource

@Composable
fun SessionHeaderCard(
    session: ScheduleSessionUiModel,
    modifier: Modifier = Modifier
) {
    val now = remember { Clock.System.now() }
    val status = session.getStatus(now)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppDimen.p16),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF475AD7), Color(0xFF8E97FD))
                    )
                )
                .padding(AppDimen.p20)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AppDimen.p12)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText(
                        text = stringResource(
                            Res.string.session_number_prefix,
                            session.sessionNumber
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    SessionStatusBadge(status = status)
                }

                AppText(
                    text = session.subjectName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                AppText(
                    text = stringResource(Res.string.session_class_prefix, session.className),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun SessionStatusBadge(status: SessionStatus) {
    val (text, textColor, badgeBg) = when (status) {
        SessionStatus.COMPLETED -> Triple(
            stringResource(Res.string.session_status_completed),
            Color(0xFF2E7D32),
            Color(0xFFE8F5E9)
        )

        SessionStatus.ONGOING -> Triple(
            stringResource(Res.string.session_status_ongoing),
            Color(0xFFE65100),
            Color(0xFFFFF3E0)
        )

        SessionStatus.UPCOMING -> Triple(
            stringResource(Res.string.session_status_upcoming),
            Color(0xFF37474F),
            Color(0xFFECEFF1)
        )
    }

    AppBadge(
        text = text,
        color = textColor,
        backgroundColor = badgeBg,
        borderColor = null
    )
}
