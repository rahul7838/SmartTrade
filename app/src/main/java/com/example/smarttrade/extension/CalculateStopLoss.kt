package com.example.smarttrade.extension

import com.example.smarttrade.manager.SmartTradeNotificationManager
import com.example.smarttrade.repository.PositionRepository
import com.example.smarttrade.repository.StopLossRepository
import org.koin.core.component.KoinApiExtension
import java.time.Instant

@KoinApiExtension
suspend fun calculateTrigger(
    positionRepository: PositionRepository,
    stopLossRepository: StopLossRepository,
    instrumentToken: String,
    lastPrice: Double
) {
//    logI("calculate trigger")
    val position = positionRepository.getPosition(instrumentToken)
    val isPositionBuyCall =
        position.netQuantity.isBuyCall()// if we have buy the share trailing stop loss will be x% less than last/current price
//    logI("isBuyCall: $isPositionBuyCall")
    val trailingStopLossInPercent =
        stopLossRepository.getStopLossInPercent(instrumentToken)
//    logI("Trailing stop loss: $trailingStopLossInPercent")
    val updatedAt = Instant.now().toEpochMilli()
    val stopLossAmount = stopLossRepository.getStopLossAmount(instrumentToken)
    val newPnl = ((lastPrice - position.averagePrice) * position.netQuantity)
    if (trailingStopLossInPercent == null) {
        positionRepository.updatePosition(instrumentToken, lastPrice, updatedAt, newPnl)
    } else if (stopLossAmount != null) {
        if (newPnl <= stopLossAmount) {
            SmartTradeNotificationManager.buildStopLossAmountNotification(
                stopLossAmount = stopLossAmount.toString(),
                position.tradingSymbol
            )
            positionRepository.updatePosition(instrumentToken, lastPrice, updatedAt, newPnl)
        } else {
            val newStopLossAmount = newPnl + trailingStopLossInPercent
            val maxOfStopLossAmount = maxOf(newStopLossAmount, stopLossAmount)
            positionRepository.updateLastPriceAndStopLoss(
                instrumentToken,
                lastPrice,
                newPnl,
                trailingStopLossInPercent,
                null,
                maxOfStopLossAmount,
                updatedAt
            )
        }
    } else {
        val oldStopLossPrice = stopLossRepository.getStopLossPrice(instrumentToken) ?: 0.0
        if (isPositionBuyCall) {
            if (oldStopLossPrice >= lastPrice) {
                SmartTradeNotificationManager.buildStopLossPriceNotification(
                    stopLossPrice = oldStopLossPrice.toString(),
                    position.tradingSymbol
                )
                positionRepository.updateLastPriceAndStopLoss(
                    instrumentToken,
                    lastPrice,
                    newPnl,
                    trailingStopLossInPercent,
                    oldStopLossPrice,
                    null,
                    updatedAt
                )
            } else {
                val newStopLossPrice = lastPrice * (1 - trailingStopLossInPercent / 100)
                val maxOfStopLossPrice = maxOf(oldStopLossPrice, newStopLossPrice)
                positionRepository.updateLastPriceAndStopLoss(
                    instrumentToken,
                    lastPrice,
                    newPnl,
                    trailingStopLossInPercent,
                    maxOfStopLossPrice,
                    null,
                    updatedAt
                )
            }
        } else {
            if (lastPrice >= oldStopLossPrice) {
                SmartTradeNotificationManager.buildStopLossPriceNotification(
                    oldStopLossPrice.toString(),
                    position.tradingSymbol
                )
                positionRepository.updateLastPriceAndStopLoss(
                    instrumentToken,
                    lastPrice,
                    newPnl,
                    trailingStopLossInPercent,
                    oldStopLossPrice,
                    null,
                    updatedAt
                )
            } else {
                val newStopLossPrice = lastPrice * (1 + trailingStopLossInPercent / 100)
                val minOfStopLossPrice = minOf(oldStopLossPrice, newStopLossPrice)
                positionRepository.updateLastPriceAndStopLoss(
                    instrumentToken,
                    lastPrice,
                    newPnl,
                    trailingStopLossInPercent,
                    minOfStopLossPrice,
                    null,
                    updatedAt
                )
            }
        }
    }
}

fun Int.isBuyCall(): Boolean {
    return this > 0
}