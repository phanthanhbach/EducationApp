package com.example.educationapp.presentation.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.avatar.AppAvatar
import com.example.educationapp.core.ui.error.ErrorStateView
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.image.CoreMediaSource
import com.example.educationapp.core.ui.layout.CollapsingHeaderScaffold
import com.example.educationapp.core.ui.layout.lerpDp
import com.example.educationapp.core.ui.shimmer.skeleton.InfoRowSkeleton
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.StudentStatus
import com.example.educationapp.domain.enums.TeacherStatus
import com.example.educationapp.presentation.screenmodel.profile.ProfileState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_settings_24dp
import educationapp.shared.generated.resources.profile_cover
import educationapp.shared.generated.resources.profile_parent
import educationapp.shared.generated.resources.title_about_me
import educationapp.shared.generated.resources.title_contact
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val CoverHeight = 220.dp
private val SheetTopExpanded = 164.dp
private val ExpandedContentSpacer = 156.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(
    profileState: ProfileState,
    onSettingsClick: () -> Unit,
    onRetry: () -> Unit,
    isRefreshing: Boolean = false,
    onRefresh: (() -> Unit)? = null
) {
    val profile = (profileState as? ProfileState.Success)?.profile

    val scaffoldContent = @Composable {
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
                    profile = profile,
                    imgUrl = profile?.img
                )
            }
        ) {
            when (profileState) {
                is ProfileState.Loading, is ProfileState.Idle -> {
                    item {
                        SectionTitle(stringResource(Res.string.title_about_me))
                    }
                    item {
                        InfoRowSkeleton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            rowCount = 4,
                            showIcons = true
                        )
                    }
                }

                is ProfileState.Error -> {
                    item {
                        SectionTitle(stringResource(Res.string.title_about_me))
                    }
                    item {
                        ErrorStateView(
                            error = profileState.error,
                            onRetry = onRetry,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }

                is ProfileState.Success -> {
                    when (val successProfile = profileState.profile) {
                        is UserProfile.Teacher -> {
                            item { SectionTitle(stringResource(Res.string.title_about_me)) }
                            item { TeacherAboutSection(teacher = successProfile) }
                            item { SectionTitle(stringResource(Res.string.title_contact)) }
                            item { TeacherContactSection(teacher = successProfile) }
                        }

                        is UserProfile.Student -> {
                            item { SectionTitle(stringResource(Res.string.title_about_me)) }
                            item { StudentAboutCard(student = successProfile) }
                            item { SectionTitle(stringResource(Res.string.title_contact)) }
                            item { StudentContactSection(student = successProfile) }
                        }

                        is UserProfile.Parent -> {
                            item { SectionTitle(stringResource(Res.string.title_about_me)) }
                            item { ParentAboutSection(parent = successProfile) }
                            item { SectionTitle(stringResource(Res.string.title_contact)) }
                            item { ParentContactSection(parent = successProfile) }
                        }
                    }
                }
            }
        }
    }

    if (onRefresh != null) {
        val pullToRefreshState = rememberPullToRefreshState()
        val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = pullToRefreshState,
            modifier = Modifier.fillMaxSize(),
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullToRefreshState,
                    isRefreshing = isRefreshing,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = statusBarHeight)
                )
            }
        ) {
            scaffoldContent()
        }
    } else {
        scaffoldContent()
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
    profile: UserProfile?,
    imgUrl: String?
) {
    val expandedAvatarSize = 88.dp
    val avatarSize = lerpDp(expandedAvatarSize, 40.dp, collapseProgress)
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val expandedAvatarTop =
        SheetTopExpanded - (expandedAvatarSize / 2) + AppDimen.p20 - statusBarHeight
    val expandedTextTop = SheetTopExpanded + AppDimen.p16 - statusBarHeight
    val avatarTop = lerpDp(expandedAvatarTop, AppDimen.p8, collapseProgress)
    val avatarStart = lerpDp(AppDimen.p20, AppDimen.p16, collapseProgress)
    val textTop = lerpDp(expandedTextTop, 17.dp, collapseProgress)
    val textStart = lerpDp(120.dp, AppDimen.p64, collapseProgress)
    val nameSize = 20 - (2 * collapseProgress)
    val subtitleAlpha = 1f - collapseProgress

    val mediaSource = if (!imgUrl.isNullOrBlank()) {
        CoreMediaSource.Url(imgUrl)
    } else {
        CoreMediaSource.None
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        AppAvatar(
            name = fullName,
            source = mediaSource,
            modifier = Modifier
                .offset(x = avatarStart, y = avatarTop)
                .size(avatarSize)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.surface,
                    shape = CircleShape
                ),
        )

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

            val profileCode = getProfileCode(profile)
            val profileStatus = getProfileStatus(profile)

            if (!profileCode.isNullOrBlank() || !profileStatus.isNullOrBlank()) {
                Row(
                    modifier = Modifier.graphicsLayer {
                        alpha = subtitleAlpha
                    },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!profileCode.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color(0xFFE8EAF6))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            AppText(
                                text = profileCode,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3F51B5)
                            )
                        }
                    }

                    if (!profileStatus.isNullOrBlank()) {
                        StatusBadge(status = profileStatus, profile = profile)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String, profile: UserProfile?) {
    val (backgroundColor, textColor, label) = when (profile) {
        is UserProfile.Student -> {
            val studentStatus = StudentStatus.fromString(status)
            val bgColor = when (studentStatus) {
                StudentStatus.ACTIVE -> Color(0xFFE6F4EA)
                StudentStatus.INACTIVE -> Color(0xFFFCE8E6)
                StudentStatus.SUSPENDED -> Color(0xFFFEF7E0)
                StudentStatus.GRADUATED -> Color(0xFFE8F0FE)
            }
            val txtColor = when (studentStatus) {
                StudentStatus.ACTIVE -> Color(0xFF137333)
                StudentStatus.INACTIVE -> Color(0xFFC5221F)
                StudentStatus.SUSPENDED -> Color(0xFFB06000)
                StudentStatus.GRADUATED -> Color(0xFF1A73E8)
            }
            val text = stringResource(studentStatus.labelRes)
            Triple(bgColor, txtColor, text)
        }

        is UserProfile.Teacher -> {
            val teacherStatus = TeacherStatus.fromString(status)
            val bgColor = when (teacherStatus) {
                TeacherStatus.ACTIVE -> Color(0xFFE6F4EA)
                TeacherStatus.INACTIVE -> Color(0xFFFCE8E6)
                TeacherStatus.ON_LEAVE -> Color(0xFFFEF7E0)
            }
            val txtColor = when (teacherStatus) {
                TeacherStatus.ACTIVE -> Color(0xFF137333)
                TeacherStatus.INACTIVE -> Color(0xFFC5221F)
                TeacherStatus.ON_LEAVE -> Color(0xFFB06000)
            }
            val text = when (teacherStatus) {
                TeacherStatus.ACTIVE -> "Hoạt động"
                TeacherStatus.INACTIVE -> "Ngưng hoạt động"
                TeacherStatus.ON_LEAVE -> "Nghỉ phép"
            }
            Triple(bgColor, txtColor, text)
        }

        else -> {
            val uppercaseStatus = status.uppercase()
            val bgColor = when (uppercaseStatus) {
                "ACTIVE" -> Color(0xFFE6F4EA)
                "INACTIVE" -> Color(0xFFFCE8E6)
                else -> MaterialTheme.colorScheme.primaryContainer
            }
            val txtColor = when (uppercaseStatus) {
                "ACTIVE" -> Color(0xFF137333)
                "INACTIVE" -> Color(0xFFC5221F)
                else -> MaterialTheme.colorScheme.onPrimaryContainer
            }
            val text = when (uppercaseStatus) {
                "ACTIVE" -> "Hoạt động"
                "INACTIVE" -> "Ngưng hoạt động"
                else -> status
            }
            Triple(bgColor, txtColor, text)
        }
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        AppText(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

// ── Subtitle helper ─────────────────────────────────────────────────────

@Composable
private fun getProfileCode(profile: UserProfile?): String? {
    return when (profile) {
        is UserProfile.Teacher -> profile.teacherCode
        is UserProfile.Student -> profile.studentCode
        is UserProfile.Parent -> stringResource(Res.string.profile_parent)
        null -> null
    }
}

private fun getProfileStatus(profile: UserProfile?): String? {
    return profile?.status
}
