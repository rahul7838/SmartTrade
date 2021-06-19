package com.example.smarttrade.ui.login

import com.example.smarttrade.ui.base.BaseViewModel
import org.koin.core.component.KoinApiExtension
import timber.log.Timber

@KoinApiExtension
class LoginViewModel : BaseViewModel {

    init {
        Timber.d("Instance created for login viewModel")
    }

    constructor() : super() {
        Timber.d("Constructor for Login view model created")
    }
}