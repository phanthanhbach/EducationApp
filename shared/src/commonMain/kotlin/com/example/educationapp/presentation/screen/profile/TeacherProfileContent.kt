package com.example.educationapp.presentation.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.row.ProfileIconInfoRow
import com.example.educationapp.core.ui.row.ProfileInfoRow
import com.example.educationapp.domain.entity.UserProfile
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_call_24dp
import educationapp.shared.generated.resources.ic_mail_24dp
import educationapp.shared.generated.resources.profile_certificates
import educationapp.shared.generated.resources.profile_email
import educationapp.shared.generated.resources.profile_experience
import educationapp.shared.generated.resources.profile_not_available
import educationapp.shared.generated.resources.profile_phone
import org.jetbrains.compose.resources.stringResource

/**
 * Contact card for Teacher profile: Email + Phone with leading icons.
 */
@Composable
fun TeacherContactCard(
    teacher: UserProfile.Teacher,
    modifier: Modifier = Modifier
) {
    val na = stringResource(Res.string.profile_not_available)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppDimen.p16),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimen.p16),
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
}

/**
 * About Me card for Teacher profile: Experience + Certificates.
 */
@Composable
fun TeacherAboutMeCard(
    teacher: UserProfile.Teacher,
    modifier: Modifier = Modifier
) {
    val na = stringResource(Res.string.profile_not_available)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppDimen.p16),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimen.p16),
            verticalArrangement = Arrangement.spacedBy(AppDimen.p4)
        ) {
            ProfileInfoRow(
                label = stringResource(Res.string.profile_experience),
                value = teacher.experience ?: na
            )
            ProfileInfoRow(
                label = stringResource(Res.string.profile_certificates),
                value = if (teacher.certificates.isNotEmpty()) teacher.certificates.joinToString(", ") else na
            )
        }
    }
}
