package com.example.educationapp.presentation.screen.dashboard.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.util.clipEntryOf
import com.example.educationapp.presentation.screenmodel.dashboard.TeacherContactUiModel
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_call_24dp
import educationapp.shared.generated.resources.ic_mail_24dp
import educationapp.shared.generated.resources.teacher_contact_email_copied
import educationapp.shared.generated.resources.teacher_contact_no_email_app
import educationapp.shared.generated.resources.teacher_contact_no_phone_app
import educationapp.shared.generated.resources.teacher_contact_phone_copied
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun TeacherContactSection(
    contacts: List<TeacherContactUiModel>,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppDimen.p12),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            AppDimen.p1,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(AppDimen.p16),
            verticalArrangement = Arrangement.spacedBy(AppDimen.p16)
        ) {
            contacts.forEach { contact ->
                TeacherContactItem(
                    contact = contact,
                    onShowToast = onShowToast
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TeacherContactItem(
    contact: TeacherContactUiModel,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    val noEmailAppMsg = stringResource(Res.string.teacher_contact_no_email_app)
    val emailCopiedMsg = stringResource(Res.string.teacher_contact_email_copied)
    val noPhoneAppMsg = stringResource(Res.string.teacher_contact_no_phone_app)
    val phoneCopiedMsg = stringResource(Res.string.teacher_contact_phone_copied)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppDimen.p2)
    ) {
        AppText(
            text = contact.className,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        AppText(
            text = contact.courseName,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(AppDimen.p4))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppDimen.p4)
            ) {
                contact.teacherEmail?.let { email ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimen.p6)
                    ) {
                        AppIcon(
                            drawableRes = Res.drawable.ic_mail_24dp,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            iconModifier = Modifier.size(14.dp)
                        )
                        AppText(
                            text = email,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                contact.teacherPhone?.let { phone ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimen.p6)
                    ) {
                        AppIcon(
                            drawableRes = Res.drawable.ic_call_24dp,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            iconModifier = Modifier.size(14.dp)
                        )
                        AppText(
                            text = phone,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(AppDimen.p8))

            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimen.p4),
                verticalAlignment = Alignment.CenterVertically
            ) {
                contact.teacherEmail?.let { email ->
                    Box(
                        modifier = Modifier
                            .size(AppDimen.p36)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .combinedClickable(
                                onClick = {
                                    try {
                                        uriHandler.openUri("mailto:$email")
                                    } catch (_: Exception) {
                                        onShowToast(noEmailAppMsg)
                                    }
                                },
                                onLongClick = {
                                    scope.launch {
                                        clipboard.setClipEntry(clipEntryOf(email))
                                    }
                                    onShowToast(emailCopiedMsg)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(
                            drawableRes = Res.drawable.ic_mail_24dp,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            iconModifier = Modifier.size(AppDimen.p18)
                        )
                    }
                }

                contact.teacherPhone?.let { phone ->
                    Box(
                        modifier = Modifier
                            .size(AppDimen.p36)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .combinedClickable(
                                onClick = {
                                    try {
                                        uriHandler.openUri("tel:$phone")
                                    } catch (_: Exception) {
                                        onShowToast(noPhoneAppMsg)
                                    }
                                },
                                onLongClick = {
                                    scope.launch {
                                        clipboard.setClipEntry(clipEntryOf(phone))
                                    }
                                    onShowToast(phoneCopiedMsg)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(
                            drawableRes = Res.drawable.ic_call_24dp,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            iconModifier = Modifier.size(AppDimen.p18)
                        )
                    }
                }
            }
        }
    }
}
