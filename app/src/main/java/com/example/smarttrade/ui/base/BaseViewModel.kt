package com.example.smarttrade.ui.base

import androidx.lifecycle.ViewModel
import com.example.smarttrade.util.SingleLiveEvent
import com.example.smarttrade.di.DEFAULT
import com.example.smarttrade.di.IO
import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

@KoinApiExtension
abstract class BaseViewModel : ViewModel(), KoinComponent {

    val showProgressBar = SingleLiveEvent<Boolean>()

    val ioDispatcher: CoroutineScope by inject(named(IO))

    val defaultDispatcher: CoroutineScope by inject(named(DEFAULT))
}