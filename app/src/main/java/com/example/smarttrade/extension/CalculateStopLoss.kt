package com.example.smarttrade.extension

import com.example.smarttrade.manager.SmartTradeNotificationManager
import com.example.smarttrade.repository.GroupDetailsRepository
import com.example.smarttrade.repository.GroupRepository
import com.example.smarttrade.repository.PositionRepository
import com.example.smarttrade.repository.StopLossRepository
import com.zerodhatech.models.MarketDepth
import org.koin.core.component.KoinApiExtension
import java.time.Instant

@KoinApiExtension
suspend fun calculateTrigger(
    positionRepository: PositionRepository,
    stopLossRepository: StopLossRepository,
    instrumentToken: String,
    lastPrice: Double,
    marketDepth: MarketDepth,
    groupDetailsRepository: GroupDetailsRepository,
    groupRepository: GroupRepository
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
//    val newPnl = ((lastPrice - position.averagePrice) * position.netQuantity)
    val buyDepth = marketDepth.buy.first().price
    val sellDepth = marketDepth.sell.first().price
    val midPrice = (buyDepth + sellDepth)/2
    val newPnl = (midPrice - position.averagePrice)* position.netQuantity
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
    triggerGroupStopLoss(groupDetailsRepository, groupRepository)
}

@KoinApiExtension
suspend fun triggerGroupStopLoss(
    groupDetailsRepository: GroupDetailsRepository,
    groupRepository: GroupRepository
) {
    val groupDetails = groupDetailsRepository.getAllGroupPosition()
    groupDetails.forEach {
        var pnl = 0.0
        it.listOfPosition.forEach { position ->
            pnl += position.pnl
        }
        val trailingSL = it.group.trailingSL
        val stopLoss = it.group.stopLoss
        if (trailingSL != null && trailingSL >= pnl) {
            SmartTradeNotificationManager.buildStopLossAmountNotification(
                trailingSL.toString(),
                it.group.groupName
            )
            groupRepository.updatePnl(it.group.groupName, pnl, trailingSL)
        } else {
            val newStopLoss = if (stopLoss != null) pnl - stopLoss else stopLoss
            val maxOfStopLoss = if (newStopLoss != null && trailingSL != null) maxOf(
                newStopLoss,
                trailingSL
            ) else trailingSL
            groupRepository.updatePnl(it.group.groupName, pnl, maxOfStopLoss)
        }
    }
}


fun Int.isBuyCall(): Boolean {
    return this > 0
}