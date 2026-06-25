package com.example.educationapp.presentation.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.row.ProfileIconInfoRow
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.UserProfile
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_award_star_24dp
import educationapp.shared.generated.resources.ic_book_24dp
import educationapp.shared.generated.resources.ic_call_24dp
import educationapp.shared.generated.resources.ic_mail_24dp
import educationapp.shared.generated.resources.ic_subdirectory_arrow_right_24dp
import educationapp.shared.generated.resources.profile_certificates
import educationapp.shared.generated.resources.profile_email
import educationapp.shared.generated.resources.profile_experience
import educationapp.shared.generated.resources.profile_not_available
import educationapp.shared.generated.resources.profile_phone
import org.jetbrains.compose.resources.stringResource

/**
 * Contact section for Teacher profile: Email + Phone with leading icons.
 */
@Composable
fun TeacherContactSection(
    teacher: UserProfile.Teacher,
    modifier: Modifier = Modifier
) {
    val na = stringResource(Res.string.profile_not_available)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppDimen.p4)
    ) {
        ProfileIconInfoRow(
            iconRes = Res.drawable.ic_mail_24dp,
            label = stringResource(Res.string.profile_email),
            value = teacher.email ?: na
        )
        ProfileIconInfoRow(
            iconRes = Res.drawable.ic_call_24dp,
            label = stringResource(Res.string.profile_phone),
            value = teacher.phone ?: na
        )
    }
}

/**
 * About section for Teacher profile: Experience + Certificates.
 */
@Composable
fun TeacherAboutSection(
    teacher: UserProfile.Teacher,
    modifier: Modifier = Modifier
) {
    val na = stringResource(Res.string.profile_not_available)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppDimen.p4)
    ) {
        ProfileIconInfoRow(
            iconRes = Res.drawable.ic_book_24dp,
            label = stringResource(Res.string.profile_experience),
            value = teacher.experience ?: na
        )
        if (teacher.certificates.isNotEmpty()) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = AppDimen.p8),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimen.p16)
            ) {
                AppIcon(
                    drawableRes = Res.drawable.ic_award_star_24dp,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
                AppText(
                    text = stringResource(Res.string.profile_certificates),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            teacher.certificates.forEach { certificate ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIcon(
                        drawableRes = Res.drawable.ic_subdirectory_arrow_right_24dp,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        iconModifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AppText(
                        text = certificate,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else
            ProfileIconInfoRow(
                iconRes = Res.drawable.ic_award_star_24dp,
                label = stringResource(Res.string.profile_certificates),
                value = na
            )
    }
}
