package com.example.educationapp.di

import com.example.educationapp.core.data.TokenManager
import com.example.educationapp.data.repository.AuthRepositoryImpl
import com.example.educationapp.domain.repository.AuthRepository
import org.koin.dsl.module


val repositoryModule = module {
    single { TokenManager(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
}