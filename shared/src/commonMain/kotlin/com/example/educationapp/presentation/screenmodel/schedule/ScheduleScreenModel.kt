package com.example.educationapp.presentation.screenmodel.schedule

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.CalendarHelper
import com.example.educationapp.domain.usecase.GetMySchedulesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number

sealed interface ScheduleState {
    object Idle : ScheduleState
    object Loading : ScheduleState
    data class Success(val schedules: List<ScheduleSessionUiModel>) : ScheduleState
    data class Error(val message: String) : ScheduleState
}

class ScheduleScreenModel(
    private val getMySchedulesUseCase: GetMySchedulesUseCase
) : ScreenModel {

    val selectedDate = MutableStateFlow(CalendarHelper.getCurrentDate())
    val isMonthExpanded = MutableStateFlow(false)

    private val _schedulesState = MutableStateFlow<ScheduleState>(ScheduleState.Idle)
    val schedulesState: StateFlow<ScheduleState> = _schedulesState.asStateFlow()

    // Cache to hold loaded schedules as UI Models for the current visible range
    private var allLoadedSchedules = listOf<ScheduleSessionUiModel>()
    private var lastFetchedFrom: LocalDate? = null
    private var lastFetchedTo: LocalDate? = null

    // Filter schedules locally for the selected date
    val filteredSchedules: StateFlow<List<ScheduleSessionUiModel>> = combine(
        selectedDate,
        schedulesState
    ) { date, state ->
        if (state is ScheduleState.Success) {
            state.schedules.filter { it.date == date }
        } else {
            emptyList()
        }
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Compute all dates with at least one class session to draw dot indicators
    val highlightDates: StateFlow<Set<LocalDate>> = schedulesState
        .map { state ->
            if (state is ScheduleState.Success) {
                state.schedules.map { it.date }.toSet()
            } else {
                emptySet()
            }
        }.stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    init {
        // Automatically check and load schedules when selectedDate or isMonthExpanded changes
        screenModelScope.launch {
            combine(selectedDate, isMonthExpanded) { date, expanded ->
                calculateVisibleRange(date, expanded)
            }.collect { (from, to) ->
                checkAndLoadSchedules(from, to)
            }
        }
    }

    private fun calculateVisibleRange(
        date: LocalDate,
        expanded: Boolean
    ): Pair<LocalDate, LocalDate> {
        return if (expanded) {
            val dates = CalendarHelper.getMonthGridDates(date)
            val rows = getMonthRowsCount(dates, date)
            Pair(dates.first(), dates[rows * 7 - 1])
        } else {
            val dates = CalendarHelper.getWeekDates(date)
            Pair(dates.first(), dates.last())
        }
    }

    private fun getMonthRowsCount(dates: List<LocalDate>, selectedDate: LocalDate): Int {
        return when {
            dates[28].month.number != selectedDate.month.number -> 4
            dates[35].month.number != selectedDate.month.number -> 5
            else -> 6
        }
    }

    fun retryLoad() {
        val (from, to) = calculateVisibleRange(selectedDate.value, isMonthExpanded.value)
        loadSchedules(from, to)
    }

    private fun checkAndLoadSchedules(from: LocalDate, to: LocalDate) {
        if (lastFetchedFrom != from || lastFetchedTo != to) {
            loadSchedules(from, to)
        }
    }

    private fun loadSchedules(from: LocalDate, to: LocalDate) {
        screenModelScope.launch {
            _schedulesState.value = ScheduleState.Loading
            val fromTime = "${from}T00:00:00"
            val toTime = "${to}T23:59:59"

            when (val result = getMySchedulesUseCase(fromTime, toTime)) {
                is ApiResult.Success -> {
                    allLoadedSchedules = result.data.map { item ->
                        val date = try {
                            LocalDateTime.parse(item.startTime).date
                        } catch (_: Exception) {
                            LocalDate(2026, 6, 1)
                        }

                        val roomText =
                            if (item.roomName.isNotBlank() && item.roomName != "string") {
                                item.roomName
                            } else {
                                "Phòng ${item.roomId}"
                            }

                        ScheduleSessionUiModel(
                            id = "${item.classId}_${item.sessionNumber}_${item.startTime}",
                            classId = item.classId,
                            sessionNumber = item.sessionNumber,
                            subjectName = if (!item.notes.isNullOrBlank() && item.notes != "string") item.notes else item.className,
                            className = item.className,
                            room = roomText,
                            startTimeRaw = item.startTime,
                            endTimeRaw = item.endTime,
                            attendanceText = "",
                            date = date
                        )
                    }
                    lastFetchedFrom = from
                    lastFetchedTo = to
                    _schedulesState.value = ScheduleState.Success(allLoadedSchedules)
                }

                is ApiResult.Error -> {
                    // Reset fetched range on error so retry triggers it again
                    lastFetchedFrom = null
                    lastFetchedTo = null
                    _schedulesState.value = ScheduleState.Error(
                        result.message ?: "Không thể tải lịch trình."
                    )
                }
            }
        }
    }
}
