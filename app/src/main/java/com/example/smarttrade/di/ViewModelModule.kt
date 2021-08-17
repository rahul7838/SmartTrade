package com.example.smarttrade.di

import com.example.smarttrade.ui.bottomsheet.BottomSheetViewModel
import com.example.smarttrade.ui.group.GroupViewModel
import com.example.smarttrade.ui.login.LoginViewModel
import com.example.smarttrade.ui.position.PositionViewModel
import com.example.smarttrade.ui.groupdetails.GroupDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { LoginViewModel() }
    viewModel { params -> PositionViewModel(get(), get(),get(),get(), params.getOrNull(String::class)) }
    viewModel { GroupDetailsViewModel(get()) }
    viewModel { GroupViewModel(get()) }
    viewModel { BottomSheetViewModel(get(), get(), get()) }
}

