package com.example.educationapp.presentation.screen.main.tab.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.button.AppButton
import com.example.educationapp.core.ui.button.AppTextButton
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.calendar_dialog_btn_cancel
import educationapp.shared.generated.resources.calendar_dialog_btn_confirm
import educationapp.shared.generated.resources.calendar_dialog_title
import educationapp.shared.generated.resources.calendar_month_10_short
import educationapp.shared.generated.resources.calendar_month_11_short
import educationapp.shared.generated.resources.calendar_month_12_short
import educationapp.shared.generated.resources.calendar_month_1_short
import educationapp.shared.generated.resources.calendar_month_2_short
import educationapp.shared.generated.resources.calendar_month_3_short
import educationapp.shared.generated.resources.calendar_month_4_short
import educationapp.shared.generated.resources.calendar_month_5_short
import educationapp.shared.generated.resources.calendar_month_6_short
import educationapp.shared.generated.resources.calendar_month_7_short
import educationapp.shared.generated.resources.calendar_month_8_short
import educationapp.shared.generated.resources.calendar_month_9_short
import educationapp.shared.generated.resources.calendar_year_suffix
import educationapp.shared.generated.resources.ic_arrow_back_ios_new_24dp
import educationapp.shared.generated.resources.ic_arrow_forward_ios_24dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.jetbrains.compose.resources.stringResource

@Composable
fun YearMonthPickerDialog(
    currentDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (year: Int, month: Int) -> Unit
) {
    var chosenYear by remember { mutableStateOf(currentDate.year) }
    var chosenMonth by remember { mutableStateOf(currentDate.month.number) }

    val monthNamesShort = listOf(
        stringResource(Res.string.calendar_month_1_short),
        stringResource(Res.string.calendar_month_2_short),
        stringResource(Res.string.calendar_month_3_short),
        stringResource(Res.string.calendar_month_4_short),
        stringResource(Res.string.calendar_month_5_short),
        stringResource(Res.string.calendar_month_6_short),
        stringResource(Res.string.calendar_month_7_short),
        stringResource(Res.string.calendar_month_8_short),
        stringResource(Res.string.calendar_month_9_short),
        stringResource(Res.string.calendar_month_10_short),
        stringResource(Res.string.calendar_month_11_short),
        stringResource(Res.string.calendar_month_12_short)
    )

    BoxWithConstraints {
        val isLandscape = maxHeight < 420.dp

        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = !isLandscape)
        ) {
            Surface(
                modifier = Modifier
                    .width(if (isLandscape) 460.dp else 320.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(AppDimen.p20)
                ) {
                    AppText(
                        text = stringResource(Res.string.calendar_dialog_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(AppDimen.p16))

                    if (isLandscape) {
                        // Side-by-side selectors row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppDimen.p16),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left column: Year selector & action buttons
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(AppDimen.p16),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AppIcon(
                                        drawableRes = Res.drawable.ic_arrow_back_ios_new_24dp,
                                        tint = MaterialTheme.colorScheme.primary,
                                        iconModifier = Modifier.size(16.dp),
                                        onClick = { chosenYear-- }
                                    )

                                    Spacer(modifier = Modifier.width(AppDimen.p16))

                                    AppText(
                                        text = stringResource(
                                            Res.string.calendar_year_suffix,
                                            chosenYear
                                        ),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.width(AppDimen.p16))

                                    AppIcon(
                                        drawableRes = Res.drawable.ic_arrow_forward_ios_24dp,
                                        tint = MaterialTheme.colorScheme.primary,
                                        iconModifier = Modifier.size(16.dp),
                                        onClick = { chosenYear++ }
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(
                                        AppDimen.p12,
                                        Alignment.CenterHorizontally
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AppTextButton(
                                        text = stringResource(Res.string.calendar_dialog_btn_cancel),
                                        onClick = onDismiss
                                    )

                                    AppButton(
                                        text = stringResource(Res.string.calendar_dialog_btn_confirm),
                                        onClick = { onConfirm(chosenYear, chosenMonth) },
                                        shape = RoundedCornerShape(AppDimen.p100),
                                        modifier = Modifier.height(38.dp),
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                            }

                            // Right column: Month selector only
                            Column(
                                modifier = Modifier.weight(1.3f)
                            ) {
                                for (row in 0 until 3) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(AppDimen.p8)
                                    ) {
                                        for (col in 0 until 4) {
                                            val monthIndex = row * 4 + col
                                            val monthNum = monthIndex + 1
                                            val isSelected = monthNum == chosenMonth
                                            val monthName = monthNamesShort[monthIndex]

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(AppDimen.p100))
                                                    .background(
                                                        if (isSelected) {
                                                            MaterialTheme.colorScheme.primary.copy(
                                                                alpha = 0.15f
                                                            )
                                                        } else {
                                                            MaterialTheme.colorScheme.surfaceVariant.copy(
                                                                alpha = 0.4f
                                                            )
                                                        }
                                                    )
                                                    .clickable { chosenMonth = monthNum }
                                                    .padding(vertical = AppDimen.p12),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AppText(
                                                    text = monthName,
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                                    ),
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                    if (row < 2) {
                                        Spacer(modifier = Modifier.height(AppDimen.p8))
                                    }
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = AppDimen.p4),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppIcon(
                                drawableRes = Res.drawable.ic_arrow_back_ios_new_24dp,
                                tint = MaterialTheme.colorScheme.primary,
                                iconModifier = Modifier.size(18.dp),
                                onClick = { chosenYear-- }
                            )

                            AppText(
                                text = stringResource(Res.string.calendar_year_suffix, chosenYear),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            AppIcon(
                                drawableRes = Res.drawable.ic_arrow_forward_ios_24dp,
                                tint = MaterialTheme.colorScheme.primary,
                                iconModifier = Modifier.size(18.dp),
                                onClick = { chosenYear++ }
                            )
                        }

                        Spacer(modifier = Modifier.height(AppDimen.p20))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(AppDimen.p8)
                        ) {
                            for (row in 0 until 4) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(AppDimen.p12)
                                ) {
                                    for (col in 0 until 3) {
                                        val monthIndex = row * 3 + col
                                        val monthNum = monthIndex + 1
                                        val isSelected = monthNum == chosenMonth
                                        val monthName = monthNamesShort[monthIndex]

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(AppDimen.p100))
                                                .background(
                                                    if (isSelected) {
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                                    } else {
                                                        MaterialTheme.colorScheme.surfaceVariant.copy(
                                                            alpha = 0.4f
                                                        )
                                                    }
                                                )
                                                .clickable { chosenMonth = monthNum }
                                                .padding(vertical = AppDimen.p12),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AppText(
                                                text = monthName,
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                                ),
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(AppDimen.p24))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppTextButton(
                                text = stringResource(Res.string.calendar_dialog_btn_cancel),
                                onClick = onDismiss
                            )

                            Spacer(modifier = Modifier.width(AppDimen.p8))

                            AppButton(
                                text = stringResource(Res.string.calendar_dialog_btn_confirm),
                                onClick = { onConfirm(chosenYear, chosenMonth) },
                                shape = RoundedCornerShape(AppDimen.p100),
                                modifier = Modifier.height(38.dp),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }
            }
        }
    }
}
