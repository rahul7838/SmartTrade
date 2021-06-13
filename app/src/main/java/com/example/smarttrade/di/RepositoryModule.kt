package com.example.smarttrade.di

import com.example.smarttrade.repository.KiteConnectRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { KiteConnectRepository(get()) }
}