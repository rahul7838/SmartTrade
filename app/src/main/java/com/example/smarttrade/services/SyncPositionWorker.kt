package com.example.smarttrade.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smarttrade.extension.calculateTrigger
import com.example.smarttrade.extension.logI
import com.example.smarttrade.extension.parseLocal
import com.example.smarttrade.manager.SmartTradeNotificationManager
import com.example.smarttrade.repository.KiteConnectRepository
import com.zerodhatech.models.Position
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime


@KoinApiExtension
class SyncPositionWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {

    private val kiteConnectRepository: KiteConnectRepository by inject()
    var now1 = 0L
    override suspend fun doWork(): Result {
        logI("do work")
        now1 = Instant.now().epochSecond - now1
        val inMin = now1/60
        logI("Minutes $inMin")
        val instrument = kiteConnectRepository.getInstrument()
        val quotes = kiteConnectRepository.getQuote(instrument)
        quotes.forEach {
            calculateTrigger(kiteConnectRepository, it.key, it.value.lastPrice)
        }
        return Result.success()
    }


}