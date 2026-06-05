package com.example.educationapp.di

import com.example.educationapp.domain.usecase.GetMyProfileUseCase
import com.example.educationapp.domain.usecase.GetMySchedulesUseCase
import com.example.educationapp.domain.usecase.LoginUseCase
import com.example.educationapp.domain.usecase.LogoutUseCase
import com.example.educationapp.domain.usecase.GetCheckInStatusUseCase
import com.example.educationapp.domain.usecase.TeacherCheckInUseCase
import com.example.educationapp.domain.usecase.GetAttendancesUseCase
import com.example.educationapp.domain.usecase.SubmitAttendancesUseCase
import com.example.educationapp.domain.usecase.TeacherCheckOutUseCase
import com.example.educationapp.domain.usecase.GetTeacherRatingSummaryUseCase
import com.example.educationapp.domain.usecase.FilterClassesUseCase
import com.example.educationapp.domain.usecase.GetStudentClassesUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { LoginUseCase(get()) }
    single { LogoutUseCase(get()) }
    single { GetMyProfileUseCase(get()) }
    single { GetMySchedulesUseCase(get()) }
    single { TeacherCheckInUseCase(get()) }
    single { GetCheckInStatusUseCase(get()) }
    single { GetAttendancesUseCase(get()) }
    single { SubmitAttendancesUseCase(get()) }
    single { TeacherCheckOutUseCase(get()) }
    single { GetTeacherRatingSummaryUseCase(get()) }
    single { FilterClassesUseCase(get()) }
    single { GetStudentClassesUseCase(get()) }
}