package com.example.educationapp.core.ui.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.util.GreetingHelper
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopBarGreeting(
    userName: String?,
    modifier: Modifier = Modifier
) {
    val greetingRes = remember { GreetingHelper.getGreetingStringRes(hasName = false) }
    val greetingText = stringResource(greetingRes)

    if (userName != null) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(AppDimen.p2),
            horizontalAlignment = Alignment.Start
        ) {
            AppText(
                text = "$greetingText,",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            AppText(
                text = userName,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        AppText(
            text = greetingText,
            modifier = modifier,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
