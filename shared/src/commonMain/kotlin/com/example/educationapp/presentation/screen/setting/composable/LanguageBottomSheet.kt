package com.example.educationapp.presentation.screen.setting.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.sheet.AppBottomSheet
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.enums.AppLanguage
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_check_24dp
import educationapp.shared.generated.resources.sheet_title_language
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageBottomSheet(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit,
) {
    AppBottomSheet(
        onDismissRequest = onDismiss,
        title = stringResource(Res.string.sheet_title_language)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AppDimen.p16),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppLanguage.entries.forEach { language ->
                LanguageSheetItem(
                    flag = language.flagEmoji,
                    label = language.displayName,
                    selected = language == selectedLanguage,
                    onClick = { onLanguageSelected(language) }
                )
            }
        }
    }
}

@Composable
private fun LanguageSheetItem(
    flag: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(AppDimen.p16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppDimen.p16)
        ) {
            AppText(
                text = flag,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = AppDimen.s16,
                color = MaterialTheme.colorScheme.onSurface
            )
            AppText(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (selected) {
            AppIcon(
                drawableRes = Res.drawable.ic_check_24dp,
                tint = MaterialTheme.colorScheme.primary,
                iconModifier = Modifier.size(22.dp)
            )
        }
    }
}
