package com.example.educationapp.di

import com.example.educationapp.core.data.TokenManager
import com.example.educationapp.data.repository.AuthRepositoryImpl
import com.example.educationapp.data.repository.ProfileRepositoryImpl
import com.example.educationapp.data.repository.ScheduleRepositoryImpl
import com.example.educationapp.domain.repository.AuthRepository
import com.example.educationapp.domain.repository.ProfileRepository
import com.example.educationapp.domain.repository.ScheduleRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { TokenManager(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
    single<ScheduleRepository> { ScheduleRepositoryImpl(get()) }
}