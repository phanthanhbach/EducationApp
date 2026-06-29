package com.example.educationapp.di

import com.example.educationapp.presentation.screen.setting.SettingScreenModel
import com.example.educationapp.presentation.screen.setting.ChangePasswordScreenModel
import com.example.educationapp.presentation.screen.profile.EditProfileScreenModel
import com.example.educationapp.presentation.screenmodel.assignment.AssignmentTabScreenModel
import com.example.educationapp.presentation.screenmodel.assignment.ClassAssignmentsScreenModel
import com.example.educationapp.presentation.screenmodel.assignment.StudentClassAssignmentsScreenModel
import com.example.educationapp.presentation.screenmodel.dashboard.StudentDashboardScreenModel
import com.example.educationapp.presentation.screenmodel.dashboard.TeacherDashboardScreenModel
import com.example.educationapp.presentation.screenmodel.course.MyCoursesScreenModel
import com.example.educationapp.presentation.screenmodel.feedback.ClassFeedbackScreenModel
import com.example.educationapp.presentation.screenmodel.feedback.StudentFeedbackScreenModel
import com.example.educationapp.presentation.screenmodel.invoice.ClassInvoicesScreenModel
import com.example.educationapp.presentation.screenmodel.login.LoginScreenModel
import com.example.educationapp.presentation.screenmodel.login.ForgotPasswordScreenModel
import com.example.educationapp.presentation.screenmodel.login.ResetPasswordScreenModel
import com.example.educationapp.presentation.screenmodel.parent.ChildAttendanceRateScreenModel
import com.example.educationapp.presentation.screenmodel.parent.ChildScheduleScreenModel
import com.example.educationapp.presentation.screenmodel.parent.FeedbackScreenModel
import com.example.educationapp.presentation.screenmodel.parent.MyChildrenScreenModel
import com.example.educationapp.presentation.screenmodel.parent.ParentMainScreenModel
import com.example.educationapp.presentation.screenmodel.parent.PaymentsScreenModel
import com.example.educationapp.presentation.screenmodel.profile.ProfileScreenModel
import com.example.educationapp.presentation.screenmodel.schedule.AttendanceScreenModel
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleScreenModel
import com.example.educationapp.presentation.screenmodel.schedule.SessionDetailScreenModel
import org.koin.dsl.module

val screenModelModule = module {
    factory { LoginScreenModel(get(), get()) }
    factory { ForgotPasswordScreenModel(get()) }
    factory { ResetPasswordScreenModel(get()) }
    factory { SettingScreenModel(get(), get(), get(), get()) }
    factory { ChangePasswordScreenModel(get()) }
    factory { ProfileScreenModel(get(), get()) }
    factory { EditProfileScreenModel(get(), get(), get(), get(), get(), get()) }
    factory { ScheduleScreenModel(get()) }
    factory { SessionDetailScreenModel(get(), get(), get(), get()) }
    factory { AttendanceScreenModel(get(), get()) }
    factory { TeacherDashboardScreenModel(get(), get(), get(), get()) }
    factory { StudentDashboardScreenModel(get(), get(), get(), get(), get(), get()) }
    factory { MyCoursesScreenModel(get()) }
    factory { AssignmentTabScreenModel(get(), get(), get()) }
    factory { ClassAssignmentsScreenModel(get()) }
    factory { ClassFeedbackScreenModel(get(), get()) }
    factory { ParentMainScreenModel(get(), get()) }
    factory { MyChildrenScreenModel(get(), get(), get(), get(), get()) }
    factory { FeedbackScreenModel(get()) }
    factory { PaymentsScreenModel(get(), get()) }
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
    factory { (classId: Int, studentId: Int) ->
        ClassInvoicesScreenModel(
            classId = classId,
            studentId = studentId,
            getMyInvoicesUseCase = get(),
            getPaymentQrUseCase = get(),
            getInvoiceByIdUseCase = get()
        )
    }
    factory { StudentClassAssignmentsScreenModel(get(), get()) }
    factory { StudentFeedbackScreenModel(get(), get()) }
}
