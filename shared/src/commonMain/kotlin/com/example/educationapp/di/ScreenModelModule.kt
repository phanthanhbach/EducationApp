package com.example.educationapp.di

import com.example.educationapp.presentation.screenmodel.login.LoginScreenModel
import com.example.educationapp.presentation.screenmodel.profile.ProfileScreenModel
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleScreenModel
import com.example.educationapp.presentation.screenmodel.schedule.SessionDetailScreenModel
import com.example.educationapp.presentation.screenmodel.schedule.AttendanceScreenModel
import com.example.educationapp.presentation.screenmodel.dashboard.TeacherDashboardScreenModel
import com.example.educationapp.presentation.screenmodel.dashboard.StudentDashboardScreenModel
import com.example.educationapp.presentation.screenmodel.assignment.AssignmentTabScreenModel
import com.example.educationapp.presentation.screenmodel.parent.ParentMainScreenModel
import com.example.educationapp.presentation.screenmodel.parent.MyChildrenScreenModel
import com.example.educationapp.presentation.screenmodel.parent.FeedbackScreenModel
import com.example.educationapp.presentation.screenmodel.parent.PaymentsScreenModel
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
    factory { ParentMainScreenModel(get(), get()) }
    factory { MyChildrenScreenModel(get(), get(), get(), get(), get()) }
    factory { FeedbackScreenModel() }
    factory { PaymentsScreenModel() }
}
