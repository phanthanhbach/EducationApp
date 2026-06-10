package com.example.educationapp.di

import com.example.educationapp.presentation.screenmodel.login.LoginScreenModel
import com.example.educationapp.presentation.screenmodel.profile.ProfileScreenModel
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleScreenModel
import com.example.educationapp.presentation.screenmodel.schedule.SessionDetailScreenModel
import com.example.educationapp.presentation.screenmodel.schedule.AttendanceScreenModel
import com.example.educationapp.presentation.screenmodel.dashboard.TeacherDashboardScreenModel
import com.example.educationapp.presentation.screenmodel.dashboard.StudentDashboardScreenModel
import com.example.educationapp.presentation.screenmodel.assignment.AssignmentTabScreenModel
import com.example.educationapp.presentation.screenmodel.assignment.ClassAssignmentsScreenModel

import com.example.educationapp.presentation.screenmodel.feedback.ClassFeedbackScreenModel
import com.example.educationapp.presentation.screenmodel.parent.ParentMainScreenModel
import com.example.educationapp.presentation.screenmodel.parent.MyChildrenScreenModel
import com.example.educationapp.presentation.screenmodel.parent.FeedbackScreenModel
import com.example.educationapp.presentation.screenmodel.parent.PaymentsScreenModel
import com.example.educationapp.presentation.screenmodel.parent.ChildScheduleScreenModel
import com.example.educationapp.presentation.screenmodel.parent.ChildAttendanceRateScreenModel
import com.example.educationapp.domain.usecase.GetStudentClassesNoPaginationUseCase
import com.example.educationapp.domain.usecase.GetAttendanceRateUseCase
import com.example.educationapp.domain.usecase.FilterSchedulesNoPaginationUseCase
import com.example.educationapp.presentation.screen.setting.SettingScreenModel
import org.koin.dsl.module

val screenModelModule = module {
    factory { LoginScreenModel(get(), get()) }
    factory { SettingScreenModel(get()) }
    factory { ProfileScreenModel(get(), get()) }
    factory { ScheduleScreenModel(get()) }
    factory { SessionDetailScreenModel(get(), get(), get(), get()) }
    factory { AttendanceScreenModel(get(), get()) }
    factory { TeacherDashboardScreenModel(get(), get(), get()) }
    factory { StudentDashboardScreenModel(get(), get(), get(), get(), get(), get()) }
    factory { AssignmentTabScreenModel(get(), get(), get()) }
    factory { ClassAssignmentsScreenModel(get()) }
    factory { ClassFeedbackScreenModel(get()) }
    factory { ParentMainScreenModel(get(), get()) }
    factory { MyChildrenScreenModel(get(), get(), get(), get(), get()) }
    factory { FeedbackScreenModel() }
    factory { PaymentsScreenModel() }
    factory { (studentId: Long) ->
        ChildScheduleScreenModel(
            studentId = studentId,
            getStudentClassesNoPaginationUseCase = get(),
            filterSchedulesNoPaginationUseCase = get()
        )
    }
    factory { (studentId: Long) ->
        ChildAttendanceRateScreenModel(
            studentId = studentId,
            getStudentClassesNoPaginationUseCase = get(),
            getAttendanceRateUseCase = get()
        )
    }
}
