package com.example.smarttrade.di

import com.example.smarttrade.ui.login.LoginViewModel
import com.example.smarttrade.ui.position.PositionViewModel
import com.example.smarttrade.ui.groupdetails.GroupPositionDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { LoginViewModel() }
    viewModel { params -> PositionViewModel(get(), params.getOrNull(String::class)) }
    viewModel { GroupPositionDetailsViewModel() }
}

