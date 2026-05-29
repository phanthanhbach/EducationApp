package com.example.educationapp.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModule)
    }

// Helper cho iOS vì iOS không dùng appDeclaration trực tiếp dễ dàng như Android
fun initKoinIos() = initKoin {}
