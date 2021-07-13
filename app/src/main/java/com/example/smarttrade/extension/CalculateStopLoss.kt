package com.example.smarttrade.extension

import com.example.smarttrade.manager.SmartTradeNotificationManager
import com.example.smarttrade.repository.KiteConnectRepository
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
suspend fun calculateTrigger(kiteConnectRepository: KiteConnectRepository, instrumentToken: String, lastPrice: Double) {
//    logI("calculate trigger")
    val position = kiteConnectRepository.getPosition(instrumentToken)
    val isPositionBuyCall = position.netQuantity.isBuyCall()// if we have buy the share trailing stop loss will be x% less than last/current price
//    logI("isBuyCall: $isPositionBuyCall")
    val trailingStopLossInPercent =
        kiteConnectRepository.getStopLossInPercent(instrumentToken)
//    logI("Trailing stop loss: $trailingStopLossInPercent")

    if (trailingStopLossInPercent == null) {
        kiteConnectRepository.updatePosition(instrumentToken, lastPrice)
    } else {
//        logI("Last Price: $lastPrice")
        val oldStopLossPrice = kiteConnectRepository.getOldStopLossPrice(instrumentToken) ?: 0.0
        if (isPositionBuyCall) {
            if (oldStopLossPrice >= lastPrice) {
                SmartTradeNotificationManager.buildNotification(stopLossPrice = oldStopLossPrice.toString(), position.tradingSymbol)
            } else {
                val newStopLossPrice = lastPrice * (1 - trailingStopLossInPercent / 100)
                val maxOfStopLossPrice = maxOf(oldStopLossPrice, newStopLossPrice)
                kiteConnectRepository.updatePosition(instrumentToken, lastPrice, trailingStopLossInPercent, maxOfStopLossPrice)
            }
        } else {
            if(lastPrice >= oldStopLossPrice) {
                SmartTradeNotificationManager.buildNotification(oldStopLossPrice.toString(), position.tradingSymbol)
            } else {
                val newStopLossPrice = lastPrice * (1 + trailingStopLossInPercent / 100)
                val minOfStopLossPrice = minOf(oldStopLossPrice, newStopLossPrice)
                kiteConnectRepository.updatePosition(instrumentToken, lastPrice, trailingStopLossInPercent, minOfStopLossPrice)
            }
        }
    }
}

fun Int.isBuyCall(): Boolean {
    return this > 0
}