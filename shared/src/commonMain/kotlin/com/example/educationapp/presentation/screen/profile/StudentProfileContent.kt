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
import com.example.educationapp.core.ui.row.ProfileInfoRow
import com.example.educationapp.domain.entity.UserProfile
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.profile_address
import educationapp.shared.generated.resources.profile_dob
import educationapp.shared.generated.resources.profile_gender
import educationapp.shared.generated.resources.profile_level
import educationapp.shared.generated.resources.profile_not_available
import educationapp.shared.generated.resources.profile_parent
import educationapp.shared.generated.resources.profile_zalo
import org.jetbrains.compose.resources.stringResource

/**
 * About card for Student profile, wrapped in a styled Card.
 */
@Composable
fun StudentAboutCard(
    student: UserProfile.Student,
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
            ProfileInfoRow(label = stringResource(Res.string.profile_dob), value = student.dateOfBirth ?: na)
            ProfileInfoRow(label = stringResource(Res.string.profile_gender), value = student.gender ?: na)
            ProfileInfoRow(label = stringResource(Res.string.profile_address), value = student.address ?: na)
            ProfileInfoRow(label = stringResource(Res.string.profile_level), value = student.currentLevel ?: na)
            ProfileInfoRow(label = stringResource(Res.string.profile_zalo), value = student.zaloLink ?: na)
            ProfileInfoRow(label = stringResource(Res.string.profile_parent), value = student.parentName ?: na)
        }
    }
}
