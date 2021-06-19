package com.example.smarttrade.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smarttrade.extension.parseLocal
import com.example.smarttrade.repository.KiteConnectRepository
import com.zerodhatech.models.Position
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber


@KoinApiExtension
class SyncPositionWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {

    private val kiteConnectRepository: KiteConnectRepository by inject()

    override suspend fun doWork(): Result {
        Timber.d("do work")
        val positionMap = kiteConnectRepository.fetchPosition()
        positionMap.values.forEach { listOfPosition ->
            listOfPosition.forEach {
                calculateTrigger(it)
            }
        }
        return Result.success()
    }

    private suspend fun calculateTrigger(position: Position) {
        Timber.d("calculate trigger")
        val instrumentToken = position.instrumentToken
        val isPositionBuyCall =
            position.netQuantity >= 0// if we have buy the share trailing stop loss will be x% less than last/current price
        Timber.d("isBuyCall: $isPositionBuyCall")
        val trailingStopLossInPercent =
            kiteConnectRepository.getStopLossInPercent(instrumentToken)
        Timber.d("Trailing stop loss: $trailingStopLossInPercent")

        if (trailingStopLossInPercent == null) {
            kiteConnectRepository.insertPosition(position.parseLocal())
        } else {
            val lastPrice = position.lastPrice
            Timber.d("Last Price: $lastPrice")
            val oldStopLossPrice = kiteConnectRepository.getOldStopLossPrice(instrumentToken) ?: 0.0
            if (isPositionBuyCall) {
                if (oldStopLossPrice >= lastPrice) {
                    // show notification
                } else {
                    val newStopLossPrice = lastPrice * (1 - trailingStopLossInPercent / 100)
                    val maxOfStopLossPrice = maxOf(oldStopLossPrice, newStopLossPrice)
                    kiteConnectRepository.updateOldStopLossPrice(
                        instrumentToken,
                        maxOfStopLossPrice
                    )
                }
            } else {
                if(lastPrice >= oldStopLossPrice ) {
                    //show notification
                } else {
                    val newStopLossPrice = lastPrice * (1 + trailingStopLossInPercent / 100)
                    val minOfStopLossPrice = minOf(oldStopLossPrice, newStopLossPrice)
                    kiteConnectRepository.updateOldStopLossPrice(
                        instrumentToken,
                        minOfStopLossPrice
                    )
                }
            }
        }
    }
}