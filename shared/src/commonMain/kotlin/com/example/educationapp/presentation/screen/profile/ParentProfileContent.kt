package com.example.educationapp.presentation.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.row.ProfileIconInfoRow
import com.example.educationapp.domain.entity.UserProfile
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_call_24dp
import educationapp.shared.generated.resources.ic_event_24dp
import educationapp.shared.generated.resources.ic_location_on_24dp
import educationapp.shared.generated.resources.ic_mail_24dp
import educationapp.shared.generated.resources.profile_address
import educationapp.shared.generated.resources.profile_email
import educationapp.shared.generated.resources.profile_join_date
import educationapp.shared.generated.resources.profile_not_available
import educationapp.shared.generated.resources.profile_phone
import org.jetbrains.compose.resources.stringResource

/**
 * About section for Parent profile: Address + Join Date.
 */
@Composable
fun ParentAboutSection(
    parent: UserProfile.Parent,
    modifier: Modifier = Modifier
) {
    val na = stringResource(Res.string.profile_not_available)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppDimen.p4)
    ) {
        ProfileIconInfoRow(
            iconRes = Res.drawable.ic_location_on_24dp,
            label = stringResource(Res.string.profile_address),
            value = parent.address ?: na
        )
        ProfileIconInfoRow(
            iconRes = Res.drawable.ic_event_24dp,
            label = stringResource(Res.string.profile_join_date),
            value = formatJoinDate(parent.createdAt) ?: na
        )
    }
}

/**
 * Contact section for Parent profile: Email + Phone.
 */
@Composable
fun ParentContactSection(
    parent: UserProfile.Parent,
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
            value = parent.email ?: na
        )
        ProfileIconInfoRow(
            iconRes = Res.drawable.ic_call_24dp,
            label = stringResource(Res.string.profile_phone),
            value = parent.phoneNumber ?: na
        )
    }
}

private fun formatJoinDate(dateStr: String?): String? {
    if (dateStr.isNullOrBlank()) return null
    return try {
        val datePart = dateStr.substringBefore("T")
        val parts = datePart.split("-")
        if (parts.size == 3) {
            "${parts[2]}/${parts[1]}/${parts[0]}"
        } else {
            dateStr
        }
    } catch (e: Exception) {
        dateStr
    }
}
