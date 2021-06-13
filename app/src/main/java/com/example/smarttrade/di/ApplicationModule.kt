package com.example.smarttrade.di


// base module contains list of all module
val applicationModule = listOf(
    viewModelModule,
    networkModule,
    coroutineModule,
    repositoryModule,
    databaseModule,
    encryptedSharedPreferences
)