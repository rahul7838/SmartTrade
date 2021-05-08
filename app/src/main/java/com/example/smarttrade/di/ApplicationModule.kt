package com.example.smarttrade.di

import com.example.smarttrade.di.coroutineModule
import com.example.smarttrade.di.networkModule
import com.example.smarttrade.di.repositoryModule
import com.example.smarttrade.di.viewModelModule


// base module contains list of all module
val applicationModule = listOf(
    viewModelModule,
    networkModule,
    coroutineModule,
    repositoryModule
)