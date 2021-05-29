package com.example.smarttrade.di

import com.example.smarttrade.ui.login.LoginViewModel
import com.example.smarttrade.ui.portfolio.PortfolioViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { LoginViewModel() }
    viewModel { PortfolioViewModel() }
}