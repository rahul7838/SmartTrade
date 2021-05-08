package com.example.smarttrade.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

// add all the dependency related to Coroutine in this module
val coroutineModule = module {

    factory(named(IO)) { CoroutineScope(Dispatchers.IO) }

    factory(named(DEFAULT)) { CoroutineScope(Dispatchers.Default) }

}

const val IO = "IO"
const val DEFAULT = "default"