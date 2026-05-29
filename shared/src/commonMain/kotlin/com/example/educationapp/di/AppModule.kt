package com.example.educationapp.di

import org.koin.dsl.module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import com.example.educationapp.core.data.TokenManager
import com.example.educationapp.core.network.createHttpClient
import com.example.educationapp.data.repository.AuthRepositoryImpl
import com.example.educationapp.domain.repository.AuthRepository
import com.example.educationapp.domain.usecase.LoginUseCase
import com.example.educationapp.domain.usecase.LogoutUseCase
import com.example.educationapp.presentation.screen.login.LoginScreenModel
import com.example.educationapp.presentation.screen.home.HomeScreenModel

val networkModule = module {
    single { createHttpClient(get()) }
}

val repositoryModule = module {
    single { TokenManager(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
}

val useCaseModule = module {
    single { LoginUseCase(get(), get()) }
    single { LogoutUseCase(get(), get()) }
}

val screenModelModule = module {
    factory { LoginScreenModel(get()) }
    factory { HomeScreenModel(get()) }
}

val appModule = listOf(
    platformModule,
    networkModule,
    repositoryModule,
    useCaseModule,
    screenModelModule
)
