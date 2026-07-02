package com.example.educationapp.presentation.screen.schedule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.button.AppButton
import com.example.educationapp.core.ui.layout.AppScaffold
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.shimmer.skeleton.StudentListSkeleton
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.ui.toast.LocalToastController
import com.example.educationapp.domain.enums.AttendanceStatus
import com.example.educationapp.presentation.screenmodel.schedule.AttendanceScreenModel
import com.example.educationapp.presentation.screenmodel.schedule.AttendanceState
import com.example.educationapp.presentation.screenmodel.schedule.AttendanceUiModel
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.attendance_filter_absent
import educationapp.shared.generated.resources.attendance_filter_all
import educationapp.shared.generated.resources.attendance_filter_excused
import educationapp.shared.generated.resources.attendance_filter_late
import educationapp.shared.generated.resources.attendance_filter_present
import educationapp.shared.generated.resources.attendance_label_saved
import educationapp.shared.generated.resources.attendance_no_reason
import educationapp.shared.generated.resources.attendance_no_students_found
import educationapp.shared.generated.resources.attendance_readonly_warning
import educationapp.shared.generated.resources.attendance_reason_placeholder
import educationapp.shared.generated.resources.attendance_returning_message
import educationapp.shared.generated.resources.attendance_save_success
import educationapp.shared.generated.resources.attendance_search_placeholder
import educationapp.shared.generated.resources.attendance_session_number_format
import educationapp.shared.generated.resources.attendance_status_absent
import educationapp.shared.generated.resources.attendance_status_excused
import educationapp.shared.generated.resources.attendance_status_late
import educationapp.shared.generated.resources.attendance_status_present
import educationapp.shared.generated.resources.attendance_title
import educationapp.shared.generated.resources.btn_retry
import educationapp.shared.generated.resources.btn_save_attendance
import org.jetbrains.compose.resources.stringResource
import kotlin.math.absoluteValue

class AttendanceScreen(
    private val classId: Long,
    private val sessionNumber: Int,
    private val className: String,
    private val subjectName: String,
    private val readOnly: Boolean = false
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<AttendanceScreenModel>()
        val state by screenModel.state.collectAsState()
        val isRefreshing by screenModel.isRefreshing.collectAsState()

        var searchQuery by remember { mutableStateOf("") }
        var selectedFilterStatus by remember { mutableStateOf<AttendanceStatus?>(null) }
        val toastController = LocalToastController.current

        // Fetch data
        LaunchedEffect(classId, sessionNumber) {
            screenModel.loadAttendances(classId, sessionNumber)
        }

        // Handle saved state
        LaunchedEffect(state) {
            if (state is AttendanceState.Saved) {
                navigator.pop()
            }
        }

        AppScaffold(
            topBar = {
                AppTopBar(
                    title = stringResource(Res.string.attendance_title),
                    onBackClick = { navigator.pop() }
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
            isRefreshing = isRefreshing,
            onRefresh = { screenModel.loadAttendances(classId, sessionNumber, isRefresh = true) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                when (val currentState = state) {
                    is AttendanceState.Loading -> {
                        StudentListSkeleton(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            itemCount = 6
                        )
                    }

                    is AttendanceState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.padding(24.dp)
                            ) {
                                AppText(
                                    text = currentState.message,
                                    color = AppColor.Error,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                                Button(
                                    onClick = {
                                        screenModel.loadAttendances(
                                            classId,
                                            sessionNumber
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColor.Primary)
                                ) {
                                    AppText(
                                        text = stringResource(Res.string.btn_retry),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    is AttendanceState.Saved -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(AppColor.Success.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AppText(
                                        text = "✓",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppColor.Success
                                    )
                                }
                                AppText(
                                    text = stringResource(Res.string.attendance_save_success),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                AppText(
                                    text = stringResource(Res.string.attendance_returning_message),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    is AttendanceState.Loaded -> {
                        // Filter students based on search text and status filters
                        val filteredStudents = currentState.students.filter { student ->
                            val matchesSearch =
                                student.studentName.contains(searchQuery, ignoreCase = true)
                            val matchesStatus =
                                selectedFilterStatus == null || student.status == selectedFilterStatus
                            matchesSearch && matchesStatus
                        }

                        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                            val isTablet = maxWidth >= 600.dp

                            Column(modifier = Modifier.fillMaxSize()) {
                                // Header Card
                                HeaderBlock(
                                    className = className,
                                    subjectName = subjectName,
                                    sessionNumber = sessionNumber
                                )

                                if (readOnly) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(AppColor.Warning.copy(alpha = 0.15f))
                                            .padding(
                                                horizontal = AppDimen.p16,
                                                vertical = AppDimen.p8
                                            )
                                    ) {
                                        AppText(
                                            text = stringResource(Res.string.attendance_readonly_warning),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AppColor.Warning
                                        )
                                    }
                                }

                                // Statistics block
                                StatisticsRow(
                                    students = currentState.students,
                                    selectedStatus = selectedFilterStatus,
                                    onStatusSelect = { selectedFilterStatus = it }
                                )

                                // Search bar
                                SearchBar(
                                    query = searchQuery,
                                    onQueryChange = { searchQuery = it }
                                )

                                // Student list
                                if (filteredStudents.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AppText(
                                            text = stringResource(Res.string.attendance_no_students_found),
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                } else {
                                    Box(modifier = Modifier.weight(1f)) {
                                        if (isTablet) {
                                            LazyVerticalGrid(
                                                columns = GridCells.Adaptive(minSize = 340.dp),
                                                modifier = Modifier.fillMaxSize(),
                                                contentPadding = PaddingValues(
                                                    start = AppDimen.p16,
                                                    end = AppDimen.p16,
                                                    top = AppDimen.p8,
                                                    bottom = 90.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                                                ),
                                                horizontalArrangement = Arrangement.spacedBy(
                                                    AppDimen.p12
                                                ),
                                                verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
                                            ) {
                                                items(
                                                    filteredStudents,
                                                    key = { it.studentId }) { student ->
                                                    StudentCard(
                                                        student = student,
                                                        onStatusSelect = { status ->
                                                            screenModel.updateStudentStatus(
                                                                student.studentId,
                                                                status
                                                            )
                                                        },
                                                        onReasonChange = { reason ->
                                                            screenModel.updateStudentReason(
                                                                student.studentId,
                                                                reason
                                                            )
                                                        }
                                                    )
                                                }
                                            }
                                        } else {
                                            LazyColumn(
                                                modifier = Modifier.fillMaxSize(),
                                                contentPadding = PaddingValues(
                                                    start = AppDimen.p16,
                                                    end = AppDimen.p16,
                                                    top = AppDimen.p8,
                                                    bottom = 90.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                                                ),
                                                verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
                                            ) {
                                                items(
                                                    filteredStudents,
                                                    key = { it.studentId }) { student ->
                                                    StudentCard(
                                                        student = student,
                                                        onStatusSelect = { status ->
                                                            screenModel.updateStudentStatus(
                                                                student.studentId,
                                                                status
                                                            )
                                                        },
                                                        onReasonChange = { reason ->
                                                            screenModel.updateStudentReason(
                                                                student.studentId,
                                                                reason
                                                            )
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Submit bottom bar
                            if (!readOnly) {
                                SubmitBottomBar(
                                    modifier = Modifier.align(Alignment.BottomCenter),
                                    hasChanges = currentState.hasChanges,
                                    isSaving = currentState.isSaving,
                                    onSave = {
                                        screenModel.saveAttendances(classId, sessionNumber) { msg ->
                                            toastController.show(msg)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun HeaderBlock(
        className: String,
        subjectName: String,
        sessionNumber: Int
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF475AD7), Color(0xFF8E97FD))
                    )
                )
                .padding(horizontal = AppDimen.p20, vertical = AppDimen.p16)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppText(
                        text = stringResource(
                            Res.string.attendance_session_number_format,
                            sessionNumber
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        AppText(
                            text = className,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                AppText(
                    text = subjectName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    @Composable
    private fun StatisticsRow(
        students: List<AttendanceUiModel>,
        selectedStatus: AttendanceStatus?,
        onStatusSelect: (AttendanceStatus?) -> Unit
    ) {
        val total = students.size
        val present = students.count { it.status == AttendanceStatus.PRESENT }
        val late = students.count { it.status == AttendanceStatus.LATE }
        val excused = students.count { it.status == AttendanceStatus.EXCUSED }
        val absent = students.count { it.status == AttendanceStatus.ABSENT }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimen.p16, vertical = AppDimen.p12),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatFilterChip(
                label = stringResource(Res.string.attendance_filter_all, total),
                selected = selectedStatus == null,
                activeColor = AppColor.Primary,
                onClick = { onStatusSelect(null) }
            )
            StatFilterChip(
                label = stringResource(Res.string.attendance_filter_present, present),
                selected = selectedStatus == AttendanceStatus.PRESENT,
                activeColor = AppColor.Success,
                onClick = { onStatusSelect(AttendanceStatus.PRESENT) }
            )
            StatFilterChip(
                label = stringResource(Res.string.attendance_filter_late, late),
                selected = selectedStatus == AttendanceStatus.LATE,
                activeColor = AppColor.Warning,
                onClick = { onStatusSelect(AttendanceStatus.LATE) }
            )
            StatFilterChip(
                label = stringResource(Res.string.attendance_filter_excused, excused),
                selected = selectedStatus == AttendanceStatus.EXCUSED,
                activeColor = Color(0xFF2196F3),
                onClick = { onStatusSelect(AttendanceStatus.EXCUSED) }
            )
            StatFilterChip(
                label = stringResource(Res.string.attendance_filter_absent, absent),
                selected = selectedStatus == AttendanceStatus.ABSENT,
                activeColor = AppColor.Error,
                onClick = { onStatusSelect(AttendanceStatus.ABSENT) }
            )
        }
    }

    @Composable
    private fun StatFilterChip(
        label: String,
        selected: Boolean,
        activeColor: Color,
        onClick: () -> Unit
    ) {
        val bg =
            if (selected) activeColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        val border =
            if (selected) activeColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        val text = if (selected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(bg)
                .border(BorderStroke(1.dp, border), shape = RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            AppText(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = text
            )
        }
    }

    @Composable
    private fun SearchBar(
        query: String,
        onQueryChange: (String) -> Unit
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = AppDimen.p16, end = AppDimen.p16, bottom = AppDimen.p8),
            placeholder = {
                AppText(
                    text = stringResource(Res.string.attendance_search_placeholder),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = AppColor.Primary.copy(alpha = 0.7f),
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
            )
        )
    }

    @Composable
    private fun StudentCard(
        student: AttendanceUiModel,
        onStatusSelect: (AttendanceStatus) -> Unit,
        onReasonChange: (String?) -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header with Name & Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StudentAvatar(
                        name = student.studentName,
                        modifier = Modifier.size(36.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        AppText(
                            text = student.studentName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (student.originalStatus != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            AppText(
                                text = stringResource(Res.string.attendance_label_saved),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Status Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatusSelectorButton(
                        label = stringResource(Res.string.attendance_status_present),
                        selected = student.status == AttendanceStatus.PRESENT,
                        color = AppColor.Success,
                        modifier = Modifier.weight(1f),
                        enabled = !readOnly,
                        onClick = { onStatusSelect(AttendanceStatus.PRESENT) }
                    )
                    StatusSelectorButton(
                        label = stringResource(Res.string.attendance_status_late),
                        selected = student.status == AttendanceStatus.LATE,
                        color = AppColor.Warning,
                        modifier = Modifier.weight(1f),
                        enabled = !readOnly,
                        onClick = { onStatusSelect(AttendanceStatus.LATE) }
                    )
                    StatusSelectorButton(
                        label = stringResource(Res.string.attendance_status_excused),
                        selected = student.status == AttendanceStatus.EXCUSED,
                        color = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f),
                        enabled = !readOnly,
                        onClick = { onStatusSelect(AttendanceStatus.EXCUSED) }
                    )
                    StatusSelectorButton(
                        label = stringResource(Res.string.attendance_status_absent),
                        selected = student.status == AttendanceStatus.ABSENT,
                        color = AppColor.Error,
                        modifier = Modifier.weight(1f),
                        enabled = !readOnly,
                        onClick = { onStatusSelect(AttendanceStatus.ABSENT) }
                    )
                }

                // Show reason field conditionally (if status is not PRESENT)
                if (student.status != AttendanceStatus.PRESENT) {
                    OutlinedTextField(
                        value = student.reason ?: "",
                        onValueChange = { onReasonChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = readOnly,
                        enabled = !readOnly,
                        placeholder = {
                            AppText(
                                text = if (readOnly) stringResource(Res.string.attendance_no_reason) else stringResource(
                                    Res.string.attendance_reason_placeholder
                                ),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = AppColor.Primary.copy(alpha = 0.5f),
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant.copy(
                                alpha = 0.4f
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun StatusSelectorButton(
        label: String,
        selected: Boolean,
        color: Color,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        onClick: () -> Unit
    ) {
        val bg = if (selected) color.copy(alpha = 0.15f) else Color.Transparent
        val border =
            if (selected) color else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        val text = if (selected) color else MaterialTheme.colorScheme.onSurfaceVariant

        val clickableModifier = if (enabled) {
            Modifier.clickable(onClick = onClick)
        } else {
            Modifier
        }

        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(bg)
                .border(BorderStroke(1.dp, border), shape = RoundedCornerShape(8.dp))
                .then(clickableModifier)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            AppText(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = text
            )
        }
    }

    @Composable
    private fun StudentAvatar(name: String, modifier: Modifier = Modifier) {
        val initials = name.split(" ")
            .filter { it.isNotBlank() }
            .takeLast(2)
            .map { it.firstOrNull()?.uppercase() ?: "" }
            .joinToString("")

        val colorHash = name.hashCode().absoluteValue
        val bgColors = listOf(
            Color(0xFFEF5350), Color(0xFFEC407A), Color(0xFFAB47BC), Color(0xFF7E57C2),
            Color(0xFF5C6BC0), Color(0xFF42A5F5), Color(0xFF26A69A), Color(0xFF26C6DA),
            Color(0xFF66BB6A), Color(0xFF9CCC65), Color(0xFFFFCA28), Color(0xFFFFA726),
            Color(0xFFFF7043), Color(0xFF8D6E63)
        )
        val bgColor = bgColors[colorHash % bgColors.size]

        Box(
            modifier = modifier
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            AppText(
                text = initials,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

    @Composable
    private fun SubmitBottomBar(
        modifier: Modifier = Modifier,
        hasChanges: Boolean,
        isSaving: Boolean,
        onSave: () -> Unit
    ) {
        val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        Card(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.Transparent),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = AppDimen.p20,
                        end = AppDimen.p20,
                        top = AppDimen.p16,
                        bottom = AppDimen.p16 + bottomPadding
                    )
            ) {
                AppButton(
                    text = stringResource(Res.string.btn_save_attendance),
                    onClick = onSave,
                    enabled = hasChanges,
                    isLoading = isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
            }
        }
    }
}
