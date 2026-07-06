package com.example.educationapp.presentation.screen.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.layout.AppScaffold
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.ui.toast.LocalToastController
import com.example.educationapp.presentation.screen.attendance.AttendanceScreen
import com.example.educationapp.presentation.screen.session.composable.CheckInInteractionSection
import com.example.educationapp.presentation.screen.session.composable.SessionHeaderCard
import com.example.educationapp.presentation.screen.session.composable.SessionInfoSection
import com.example.educationapp.presentation.model.ScheduleSessionUiModel
import com.example.educationapp.presentation.screenmodel.session_detail.SessionDetailScreenModel
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.session_checkin_status_title
import educationapp.shared.generated.resources.session_detail_title
import org.jetbrains.compose.resources.stringResource

class SessionDetailScreen(
    private val session: ScheduleSessionUiModel
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<SessionDetailScreenModel>()
        val state by screenModel.state.collectAsState()
        val isRefreshing by screenModel.isRefreshing.collectAsState()
        val scrollState = rememberScrollState()

        val toastController = LocalToastController.current

        // Fetch check-in status when screen launches
        LaunchedEffect(session) {
            screenModel.loadCheckInStatus(session)
        }

        AppScaffold(
            topBar = {
                AppTopBar(
                    title = stringResource(Res.string.session_detail_title),
                    onBackClick = { navigator.pop() }
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
            isRefreshing = isRefreshing,
            onRefresh = { screenModel.loadCheckInStatus(session, isRefresh = true) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val isTablet = maxWidth >= AppDimen.tabletWidth

                    if (isTablet) {
                        // Tablet Side-by-Side Bipartite Layout
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(AppDimen.p24),
                            horizontalArrangement = Arrangement.spacedBy(AppDimen.p24)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1.1f)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(AppDimen.p20)
                            ) {
                                SessionHeaderCard(session = session)
                                SessionInfoSection(session = session)
                                Spacer(modifier = Modifier.height(AppDimen.p12))
                            }

                            Column(
                                modifier = Modifier
                                    .weight(0.9f)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(AppDimen.p20)
                            ) {
                                AppText(
                                    text = stringResource(Res.string.session_checkin_status_title),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = AppDimen.p4)
                                )

                                CheckInInteractionSection(
                                    state = state,
                                    session = session,
                                    onCheckInClick = {
                                        screenModel.performCheckIn(session) { msg ->
                                            toastController.show(msg)
                                        }
                                    },
                                    onAttendanceClick = { isCheckedOut ->
                                        navigator.push(
                                            AttendanceScreen(
                                                classId = session.classId,
                                                sessionNumber = session.sessionNumber,
                                                className = session.className,
                                                subjectName = session.subjectName,
                                                readOnly = isCheckedOut
                                            )
                                        )
                                    },
                                    onCheckOutClick = { checkInId ->
                                        screenModel.performCheckOut(checkInId, session) { msg ->
                                            toastController.show(msg)
                                        }
                                    },
                                    onRetry = {
                                        screenModel.loadCheckInStatus(session)
                                    }
                                )
                                Spacer(modifier = Modifier.height(AppDimen.p12))
                            }
                        }
                    } else {
                        // Mobile Stacked Layout
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(AppDimen.p16),
                            verticalArrangement = Arrangement.spacedBy(AppDimen.p16)
                        ) {
                            SessionHeaderCard(session = session)
                            SessionInfoSection(session = session)
                            CheckInInteractionSection(
                                state = state,
                                session = session,
                                onCheckInClick = {
                                    screenModel.performCheckIn(session) { msg ->
                                        toastController.show(msg)
                                    }
                                },
                                onAttendanceClick = { isCheckedOut ->
                                    navigator.push(
                                        AttendanceScreen(
                                            classId = session.classId,
                                            sessionNumber = session.sessionNumber,
                                            className = session.className,
                                            subjectName = session.subjectName,
                                            readOnly = isCheckedOut
                                        )
                                    )
                                },
                                onCheckOutClick = { checkInId ->
                                    screenModel.performCheckOut(checkInId, session) { msg ->
                                        toastController.show(msg)
                                    }
                                },
                                onRetry = {
                                    screenModel.loadCheckInStatus(session)
                                }
                            )
                            Spacer(modifier = Modifier.height(AppDimen.p16))
                        }
                    }
                }
            }
        }
    }


}