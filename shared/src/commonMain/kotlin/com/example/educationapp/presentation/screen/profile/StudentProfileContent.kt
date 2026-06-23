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
import educationapp.shared.generated.resources.gender_female
import educationapp.shared.generated.resources.gender_male
import educationapp.shared.generated.resources.ic_alternate_email_24dp
import educationapp.shared.generated.resources.ic_cake_24dp
import educationapp.shared.generated.resources.ic_female_24dp
import educationapp.shared.generated.resources.ic_location_on_24dp
import educationapp.shared.generated.resources.ic_male_24dp
import educationapp.shared.generated.resources.ic_supervisor_account_24dp
import educationapp.shared.generated.resources.profile_address
import educationapp.shared.generated.resources.profile_dob
import educationapp.shared.generated.resources.profile_gender
import educationapp.shared.generated.resources.profile_not_available
import educationapp.shared.generated.resources.profile_parent
import educationapp.shared.generated.resources.profile_zalo
import org.jetbrains.compose.resources.stringResource

/**
 * About info section for Student profile, rendered directly without a Card container.
 */
@Composable
fun StudentAboutCard(
    student: UserProfile.Student,
    modifier: Modifier = Modifier
) {
    val na = stringResource(Res.string.profile_not_available)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppDimen.p4)
    ) {
        val genderIcon = if (student.gender?.uppercase() == "FEMALE") {
            Res.drawable.ic_female_24dp
        } else {
            Res.drawable.ic_male_24dp
        }

        val displayGender = when (student.gender?.uppercase()) {
            "MALE" -> stringResource(Res.string.gender_male)
            "FEMALE" -> stringResource(Res.string.gender_female)
            else -> student.gender ?: na
        }

        ProfileIconInfoRow(
            iconRes = Res.drawable.ic_cake_24dp,
            label = stringResource(Res.string.profile_dob),
            value = student.dateOfBirth ?: na
        )
        ProfileIconInfoRow(
            iconRes = genderIcon,
            label = stringResource(Res.string.profile_gender),
            value = displayGender
        )
        ProfileIconInfoRow(
            iconRes = Res.drawable.ic_location_on_24dp,
            label = stringResource(Res.string.profile_address),
            value = student.address ?: na
        )
        ProfileIconInfoRow(
            iconRes = Res.drawable.ic_supervisor_account_24dp,
            label = stringResource(Res.string.profile_parent),
            value = student.parentName ?: na
        )
    }
}

/**
 * Contact info section for Student profile, rendered directly without a Card container.
 */
@Composable
fun StudentContactSection(
    student: UserProfile.Student,
    modifier: Modifier = Modifier
) {
    val na = stringResource(Res.string.profile_not_available)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppDimen.p4)
    ) {
        ProfileIconInfoRow(
            iconRes = Res.drawable.ic_alternate_email_24dp,
            label = stringResource(Res.string.profile_zalo),
            value = student.zaloLink ?: na
        )
    }
}


