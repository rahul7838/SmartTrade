package com.example.smarttrade.extension

import com.example.smarttrade.db.entity.Position

fun com.zerodhatech.models.Position.parseLocal(): Position {
    return Position(
        product,
        exchange,
        sellValue,
        lastPrice,
        unrealised,
        buyPrice,
        sellPrice,
        m2m,
        tradingSymbol,
        netQuantity,
        sellQuantity,
        realised,
        buyQuantity,
        netValue,
        buyValue,
        multiplier,
        instrumentToken,
        closePrice,
        pnl,
        overnightQuantity,
        buym2m,
        sellm2m,
        dayBuyQuantity,
        daySellQuantity,
        dayBuyPrice,
        daySellPrice,
        dayBuyValue,
        daySellValue,
        value,
        averagePrice
    )
}

fun List<com.zerodhatech.models.Position>.parseLocal(): List<Position> {
    return this.map {
        it.parseLocal()
    }
}

