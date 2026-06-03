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
import educationapp.shared.generated.resources.profile_not_available
import educationapp.shared.generated.resources.profile_phone
import org.jetbrains.compose.resources.stringResource

/**
 * About card for Parent profile, wrapped in a styled Card.
 */
@Composable
fun ParentAboutCard(
    parent: UserProfile.Parent,
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
            ProfileInfoRow(label = stringResource(Res.string.profile_phone), value = parent.phoneNumber ?: na)
            ProfileInfoRow(label = stringResource(Res.string.profile_address), value = parent.address ?: na)
        }
    }
}
