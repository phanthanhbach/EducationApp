package com.example.educationapp.di

import com.example.educationapp.core.data.TokenManager
import com.example.educationapp.data.repository.AppPreferencesRepositoryImpl
import com.example.educationapp.data.repository.AssignmentRepositoryImpl
import com.example.educationapp.data.repository.AuthRepositoryImpl
import com.example.educationapp.data.repository.ClassFeedbackRepositoryImpl
import com.example.educationapp.data.repository.ParentRepositoryImpl
import com.example.educationapp.data.repository.ProfileRepositoryImpl
import com.example.educationapp.data.repository.ScheduleRepositoryImpl
import com.example.educationapp.data.repository.StudentDashboardRepositoryImpl
import com.example.educationapp.data.repository.InvoiceRepositoryImpl
import com.example.educationapp.data.repository.CloudinaryRepositoryImpl
import com.example.educationapp.domain.repository.AppPreferencesRepository
import com.example.educationapp.domain.repository.AssignmentRepository
import com.example.educationapp.domain.repository.AuthRepository
import com.example.educationapp.domain.repository.ClassFeedbackRepository
import com.example.educationapp.domain.repository.ParentRepository
import com.example.educationapp.domain.repository.ProfileRepository
import com.example.educationapp.domain.repository.ScheduleRepository
import com.example.educationapp.domain.repository.StudentDashboardRepository
import com.example.educationapp.domain.repository.InvoiceRepository
import com.example.educationapp.domain.repository.CloudinaryRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { TokenManager(get()) }
    single<AppPreferencesRepository> { AppPreferencesRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<AssignmentRepository> { AssignmentRepositoryImpl(get()) }
    single<ClassFeedbackRepository> { ClassFeedbackRepositoryImpl(get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
    single<ScheduleRepository> { ScheduleRepositoryImpl(get()) }
    single<StudentDashboardRepository> { StudentDashboardRepositoryImpl(get()) }
    single<ParentRepository> { ParentRepositoryImpl(get()) }
    single<InvoiceRepository> { InvoiceRepositoryImpl(get()) }
    single<CloudinaryRepository> { CloudinaryRepositoryImpl() }
}
