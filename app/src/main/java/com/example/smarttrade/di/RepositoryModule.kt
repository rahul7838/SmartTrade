package com.example.smarttrade.di

import com.example.smarttrade.repository.GroupDetailsRepository
import com.example.smarttrade.repository.GroupRepository
import com.example.smarttrade.repository.PositionRepository
import com.example.smarttrade.repository.StopLossRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { PositionRepository(get()) }
    single { StopLossRepository(get()) }
    single { GroupRepository(get(), get(), get()) }
    single { GroupDetailsRepository(get()) }
}