package com.example.educationapp.di

import com.example.educationapp.domain.usecase.GetChildrenUseCase
import com.example.educationapp.domain.usecase.FilterAssignmentsUseCase
import com.example.educationapp.domain.usecase.GetClassFeedbacksUseCase
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
import com.example.educationapp.domain.usecase.GetAttendanceRateUseCase
import com.example.educationapp.domain.usecase.GetMyCoursesUseCase
import com.example.educationapp.domain.usecase.GetAssignmentRemindersUseCase
import com.example.educationapp.domain.usecase.GetStudentClassesInfoUseCase
import com.example.educationapp.domain.usecase.FilterSchedulesNoPaginationUseCase
import com.example.educationapp.domain.usecase.GetStudentClassesNoPaginationUseCase
import com.example.educationapp.domain.usecase.SubmitTeacherFeedbackUseCase
import com.example.educationapp.domain.usecase.GetFeedbackNoPaginationUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { LoginUseCase(get()) }
    single { LogoutUseCase(get()) }
    single { FilterAssignmentsUseCase(get()) }
    single { GetClassFeedbacksUseCase(get()) }
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
    single { GetAttendanceRateUseCase(get()) }
    single { GetMyCoursesUseCase(get()) }
    single { GetAssignmentRemindersUseCase(get()) }
    single { GetStudentClassesInfoUseCase(get()) }
    single { GetChildrenUseCase(get()) }
    single { FilterSchedulesNoPaginationUseCase(get()) }
    single { GetStudentClassesNoPaginationUseCase(get()) }
    single { SubmitTeacherFeedbackUseCase(get()) }
    single { GetFeedbackNoPaginationUseCase(get()) }
}
