package com.example.smarttrade.di

import com.example.smarttrade.ui.login.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { LoginViewModel() }
//    viewModel { NewsDescriptionViewModel(get()) }
}