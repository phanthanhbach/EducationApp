package com.example.educationapp.di

import com.example.educationapp.domain.usecase.ObserveAppPreferencesUseCase
import com.example.educationapp.domain.usecase.SetAppLanguageUseCase
import com.example.educationapp.domain.usecase.SetAppThemeModeUseCase
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
import com.example.educationapp.domain.usecase.GetTeacherCheckInsUseCase
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
import com.example.educationapp.domain.usecase.GetMyInvoicesUseCase
import com.example.educationapp.domain.usecase.GetPaymentQrUseCase
import com.example.educationapp.domain.usecase.GetInvoiceByIdUseCase
import com.example.educationapp.domain.usecase.GetMyAssignmentsFilteredUseCase
import com.example.educationapp.domain.usecase.SubmitAssignmentUseCase
import com.example.educationapp.domain.usecase.UpdateStudentProfileUseCase
import com.example.educationapp.domain.usecase.UpdateTeacherProfileUseCase
import com.example.educationapp.domain.usecase.UpdateParentProfileUseCase
import com.example.educationapp.domain.usecase.ChangePasswordUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { ObserveAppPreferencesUseCase(get()) }
    single { SetAppThemeModeUseCase(get()) }
    single { SetAppLanguageUseCase(get()) }
    single { LoginUseCase(get()) }
    single { GetMyInvoicesUseCase(get()) }
    single { GetPaymentQrUseCase(get()) }
    single { GetInvoiceByIdUseCase(get()) }
    single { GetMyAssignmentsFilteredUseCase(get()) }
    single { SubmitAssignmentUseCase(get()) }
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
    single { GetTeacherCheckInsUseCase(get()) }
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
    single { UpdateStudentProfileUseCase(get()) }
    single { UpdateTeacherProfileUseCase(get()) }
    single { UpdateParentProfileUseCase(get()) }
    single { ChangePasswordUseCase(get()) }
}
