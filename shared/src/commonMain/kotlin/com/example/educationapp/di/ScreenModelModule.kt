package com.example.educationapp.di

import com.example.educationapp.presentation.screenmodel.login.LoginScreenModel
import com.example.educationapp.presentation.screen.main.MainScreenModel
import org.koin.dsl.module

val screenModelModule = module {
    factory { LoginScreenModel(get(), get()) }
    factory { MainScreenModel(get()) }
}
