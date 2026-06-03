package com.example.educationapp.di

import com.example.educationapp.presentation.screenmodel.login.LoginScreenModel
import com.example.educationapp.presentation.screenmodel.profile.ProfileScreenModel
import com.example.educationapp.presentation.screen.setting.SettingScreenModel
import org.koin.dsl.module

val screenModelModule = module {
    factory { LoginScreenModel(get(), get()) }
    factory { SettingScreenModel(get()) }
    factory { ProfileScreenModel(get(), get()) }
}
