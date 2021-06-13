package com.example.smarttrade.di

import androidx.room.Room
import com.example.smarttrade.db.PositionDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        val builder = Room.databaseBuilder(
            androidContext(),
            PositionDatabase::class.java,
            "SmartTrade.db"
        )
        builder.build()
    }

    single { get<PositionDatabase>().getGroupDao() }
    single { get<PositionDatabase>().getPositionDao() }
    single { get<PositionDatabase>().getGroupPositionDao() }
}