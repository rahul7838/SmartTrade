package com.example.smarttrade.di

import com.example.smarttrade.repository.*
import org.koin.dsl.module

val repositoryModule = module {
    single { PositionRepository(get()) }
    single { StopLossRepository(get()) }
    single { GroupRepository(get(), get(), get()) }
    single { GroupDetailsRepository(get()) }
    single { UncleThetaRepository() }
}