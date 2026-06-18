package com.example.educationapp.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(baseUrl: String, appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            module { single { baseUrl } },
            platformModule,
            networkModule,
            repositoryModule,
            useCaseModule,
            screenModelModule
        )
    }

// Helper cho iOS vì iOS không dùng appDeclaration trực tiếp dễ dàng như Android
fun initKoinIos(baseUrl: String) = initKoin(baseUrl) {}
