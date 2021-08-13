package com.example.smarttrade.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smarttrade.extension.logI
import com.example.smarttrade.repository.PositionRepository
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*


@KoinApiExtension
class SyncPositionWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {

    private val positionRepository: PositionRepository by inject()
    override suspend fun doWork(): Result {
        logI("do work")
        val hr = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val min = Calendar.getInstance().get(Calendar.MINUTE)
        logI("Minutes $hr:hr$min:min")
        val instrument = positionRepository.getInstrument()
        val quotes = positionRepository.getQuote(instrument)
        quotes.forEach {
//            calculateTrigger(kiteConnectRepository, it.key, it.value.lastPrice)
        }
        return Result.success()
    }
}