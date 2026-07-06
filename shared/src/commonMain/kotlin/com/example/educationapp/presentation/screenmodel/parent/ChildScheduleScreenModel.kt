package com.example.educationapp.presentation.screenmodel.parent

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.asUiText
import com.example.educationapp.core.util.CalendarHelper
import com.example.educationapp.domain.entity.SchoolClass
import com.example.educationapp.domain.usecase.FilterSchedulesNoPaginationUseCase
import com.example.educationapp.domain.usecase.GetStudentClassesNoPaginationUseCase
import com.example.educationapp.presentation.model.ScheduleSessionUiModel
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleState
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
import com.example.educationapp.core.util.asUiText


class ChildScheduleScreenModel(
    private val studentId: Long,
    private val getStudentClassesNoPaginationUseCase: GetStudentClassesNoPaginationUseCase,
    private val filterSchedulesNoPaginationUseCase: FilterSchedulesNoPaginationUseCase
) : ScreenModel {

    val selectedDate = MutableStateFlow(CalendarHelper.getCurrentDate())
    val isMonthExpanded = MutableStateFlow(false)

    private val _classesState = MutableStateFlow<ChildClassesState>(ChildClassesState.Loading)
    val classesState: StateFlow<ChildClassesState> = _classesState.asStateFlow()

    private val _selectedClass = MutableStateFlow<SchoolClass?>(null)
    val selectedClass: StateFlow<SchoolClass?> = _selectedClass.asStateFlow()

    private val _schedulesState = MutableStateFlow<ScheduleState>(ScheduleState.Idle)
    val schedulesState: StateFlow<ScheduleState> = _schedulesState.asStateFlow()

    private var allLoadedSchedules = listOf<ScheduleSessionUiModel>()
    private var lastFetchedFrom: LocalDate? = null
    private var lastFetchedTo: LocalDate? = null
    private var lastFetchedClassId: Long? = null

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
        loadClasses()
        
        // Listen for changes to reload schedule
        screenModelScope.launch {
            combine(selectedClass, selectedDate, isMonthExpanded) { clazz, date, expanded ->
                Triple(clazz, date, expanded)
            }.collect { (clazz, date, expanded) ->
                if (clazz != null) {
                    val (from, to) = calculateVisibleRange(date, expanded)
                    checkAndLoadSchedules(clazz.id, from, to)
                } else {
                    _schedulesState.value = ScheduleState.Idle
                    allLoadedSchedules = emptyList()
                    lastFetchedClassId = null
                    lastFetchedFrom = null
                    lastFetchedTo = null
                }
            }
        }
    }

    fun loadClasses() {
        screenModelScope.launch {
            _classesState.value = ChildClassesState.Loading
            when (val result = getStudentClassesNoPaginationUseCase(studentId)) {
                is ApiResult.Error -> {
                    _classesState.value = ChildClassesState.Error(result.asUiText())
                }
                is ApiResult.Success -> {
                    val list = result.data
                    _classesState.value = ChildClassesState.Success(list)
                    if (list.isNotEmpty()) {
                        _selectedClass.value = list.first()
                    } else {
                        _selectedClass.value = null
                    }
                }
            }
        }
    }

    fun selectClass(schoolClass: SchoolClass) {
        _selectedClass.value = schoolClass
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

    private fun checkAndLoadSchedules(classId: Long, from: LocalDate, to: LocalDate) {
        if (lastFetchedClassId != classId || lastFetchedFrom != from || lastFetchedTo != to) {
            loadSchedules(classId, from, to)
        }
    }

    fun retryLoadSchedules() {
        val clazz = selectedClass.value
        if (clazz != null) {
            val (from, to) = calculateVisibleRange(selectedDate.value, isMonthExpanded.value)
            loadSchedules(clazz.id, from, to)
        }
    }

    private fun loadSchedules(classId: Long, from: LocalDate, to: LocalDate) {
        screenModelScope.launch {
            _schedulesState.value = ScheduleState.Loading
            val fromTime = "${from}T00:00:00"
            val toTime = "${to}T23:59:59"

            when (val result = filterSchedulesNoPaginationUseCase(classId = classId, fromTime = fromTime, toTime = toTime)) {
                is ApiResult.Success -> {
                    allLoadedSchedules = result.data.map { item ->
                        val date = try {
                            LocalDateTime.parse(item.startTime).date
                        } catch (e: Exception) {
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
                    lastFetchedClassId = classId
                    lastFetchedFrom = from
                    lastFetchedTo = to
                    _schedulesState.value = ScheduleState.Success(allLoadedSchedules)
                }

                is ApiResult.Error -> {
                    lastFetchedClassId = null
                    lastFetchedFrom = null
                    lastFetchedTo = null
                    _schedulesState.value = ScheduleState.Error(result.asUiText())
                }
            }
        }
    }
}
