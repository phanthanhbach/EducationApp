package com.example.educationapp.presentation.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.card.ProfileErrorCard
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.layout.CollapsingHeaderScaffold
import com.example.educationapp.core.ui.layout.lerpDp
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.presentation.screenmodel.profile.ProfileState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_person_filled_24dp
import educationapp.shared.generated.resources.ic_settings_24dp
import educationapp.shared.generated.resources.profile_cover
import educationapp.shared.generated.resources.title_about
import educationapp.shared.generated.resources.title_about_me
import educationapp.shared.generated.resources.title_contact
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val CoverHeight = 220.dp
private val SheetTopExpanded = 164.dp
private val ExpandedContentSpacer = 156.dp

@Composable
fun ProfileScreenContent(
    profileState: ProfileState,
    onSettingsClick: () -> Unit,
    onRetry: () -> Unit
) {
    val profile = (profileState as? ProfileState.Success)?.profile

    CollapsingHeaderScaffold(
        sheetTopExpanded = SheetTopExpanded,
        expandedContentSpacer = ExpandedContentSpacer,
        cover = {
            ProfileCover()
        },
        headerActions = { collapseProgress ->
            ProfileHeaderActions(
                collapseProgress = collapseProgress,
                onSettingsClick = onSettingsClick
            )
        },
        collapsingContent = { collapseProgress ->
            CollapsingProfileIdentity(
                collapseProgress = collapseProgress,
                fullName = profile?.fullName ?: "",
                subtitle = getProfileSubtitle(profile)
            )
        }
    ) {
        when (profileState) {
            is ProfileState.Loading, is ProfileState.Idle -> {
                item {
                    SectionTitle(stringResource(Res.string.title_about))
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp
                        )
                    }
                }
            }

            is ProfileState.Error -> {
                item {
                    SectionTitle(stringResource(Res.string.title_about))
                }
                item {
                    ProfileErrorCard(
                        message = profileState.message,
                        onRetry = onRetry
                    )
                }
            }

            is ProfileState.Success -> {
                when (val successProfile = profileState.profile) {
                    is UserProfile.Teacher -> {
                        item { SectionTitle(stringResource(Res.string.title_contact)) }
                        item { TeacherContactCard(teacher = successProfile) }
                        item { SectionTitle(stringResource(Res.string.title_about_me)) }
                        item { TeacherAboutMeCard(teacher = successProfile) }
                    }
                    is UserProfile.Student -> {
                        item { SectionTitle(stringResource(Res.string.title_about)) }
                        item { StudentAboutCard(student = successProfile) }
                    }
                    is UserProfile.Parent -> {
                        item { SectionTitle(stringResource(Res.string.title_about)) }
                        item { ParentAboutCard(parent = successProfile) }
                    }
                }
            }
        }
    }
}

// ── Section title helper ────────────────────────────────────────────────

@Composable
private fun SectionTitle(text: String) {
    AppText(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = AppDimen.p4)
    )
}

// ── Cover & Header composables ──────────────────────────────────────────

@Composable
private fun ProfileCover() {
    Image(
        painter = painterResource(Res.drawable.profile_cover),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(CoverHeight),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun ProfileHeaderActions(
    collapseProgress: Float,
    onSettingsClick: () -> Unit
) {
    val settingsTint = lerp(
        start = MaterialTheme.colorScheme.onPrimary,
        stop = MaterialTheme.colorScheme.onSurface,
        fraction = collapseProgress
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(AppDimen.p64)
    ) {
        AppIcon(
            drawableRes = Res.drawable.ic_settings_24dp,
            tint = settingsTint,
            iconModifier = Modifier.size(24.dp),
            boxModifier = Modifier
                .align(Alignment.TopEnd)
                .size(AppDimen.p56),
            onClick = onSettingsClick
        )
    }
}

// ── Collapsing identity ─────────────────────────────────────────────────

@Composable
private fun CollapsingProfileIdentity(
    collapseProgress: Float,
    fullName: String,
    subtitle: String
) {
    val expandedAvatarSize = 88.dp
    val avatarSize = lerpDp(expandedAvatarSize, 40.dp, collapseProgress)
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val expandedAvatarTop = SheetTopExpanded - (expandedAvatarSize / 2) + AppDimen.p20 - statusBarHeight
    val expandedTextTop = SheetTopExpanded + AppDimen.p16 - statusBarHeight
    val avatarTop = lerpDp(expandedAvatarTop, AppDimen.p8, collapseProgress)
    val avatarStart = lerpDp(AppDimen.p20, AppDimen.p16, collapseProgress)
    val textTop = lerpDp(expandedTextTop, AppDimen.p8, collapseProgress)
    val textStart = lerpDp(120.dp, AppDimen.p64, collapseProgress)
    val nameSize = 20 - (4 * collapseProgress)
    val subtitleAlpha = 1f - collapseProgress

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .offset(x = avatarStart, y = avatarTop)
                .size(avatarSize)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(avatarSize / 2)
                ),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(
                drawableRes = Res.drawable.ic_person_filled_24dp,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                iconModifier = Modifier.size(avatarSize * 0.5f)
            )
        }

        Column(
            modifier = Modifier.offset(x = textStart, y = textTop)
        ) {
            AppText(
                text = fullName,
                fontWeight = FontWeight.Bold,
                fontSize = nameSize.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(AppDimen.p4))
            AppText(
                text = subtitle,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.graphicsLayer {
                    alpha = subtitleAlpha
                }
            )
        }
    }
}

// ── Subtitle helper ─────────────────────────────────────────────────────

private fun getProfileSubtitle(profile: UserProfile?): String {
    return when (profile) {
        is UserProfile.Teacher -> {
            val parts = listOfNotNull(profile.teacherCode, profile.status).filter { it.isNotBlank() }
            parts.joinToString(" • ")
        }
        is UserProfile.Student -> {
            val parts = listOfNotNull(profile.studentCode, profile.status).filter { it.isNotBlank() }
            parts.joinToString(" • ")
        }
        is UserProfile.Parent -> {
            val parts = listOfNotNull(profile.email, profile.status).filter { it.isNotBlank() }
            parts.joinToString(" • ")
        }
        null -> ""
    }
}
