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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.presentation.screenmodel.dashboard.TeacherContactUiModel
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_call_24dp
import educationapp.shared.generated.resources.ic_mail_24dp
import org.jetbrains.compose.resources.stringResource
import educationapp.shared.generated.resources.teacher_contact_no_email_app
import educationapp.shared.generated.resources.teacher_contact_email_copied
import educationapp.shared.generated.resources.teacher_contact_no_phone_app
import educationapp.shared.generated.resources.teacher_contact_phone_copied

@Composable
fun TeacherContactSection(
    contacts: List<TeacherContactUiModel>,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(AppDimen.p16),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
    val clipboardManager = LocalClipboardManager.current

    val noEmailAppMsg = stringResource(Res.string.teacher_contact_no_email_app)
    val emailCopiedMsg = stringResource(Res.string.teacher_contact_email_copied)
    val noPhoneAppMsg = stringResource(Res.string.teacher_contact_no_phone_app)
    val phoneCopiedMsg = stringResource(Res.string.teacher_contact_phone_copied)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
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

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                contact.teacherEmail?.let { email ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
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

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                contact.teacherEmail?.let { email ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
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
                                    clipboardManager.setText(AnnotatedString(email))
                                    onShowToast(emailCopiedMsg)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(
                            drawableRes = Res.drawable.ic_mail_24dp,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            iconModifier = Modifier.size(18.dp)
                        )
                    }
                }

                contact.teacherPhone?.let { phone ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
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
                                    clipboardManager.setText(AnnotatedString(phone))
                                    onShowToast(phoneCopiedMsg)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(
                            drawableRes = Res.drawable.ic_call_24dp,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            iconModifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
