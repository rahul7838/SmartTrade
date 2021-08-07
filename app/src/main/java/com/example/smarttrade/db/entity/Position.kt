package com.example.smarttrade.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.Instant

@Entity(tableName = "Position")
data class Position(
    var product: String? = null,

    var exchange: String? = null,

    var sellValue: Double? = null,

    var lastPrice: Double,

    var unrealised: Double? = null,

    var buyPrice: Double? = null,

    var sellPrice: Double? = null,

    var m2m: Double? = null,

    var tradingSymbol: String,

    var netQuantity: Int = 0,

    var sellQuantity: Int = 0,

    var realised: Double? = null,

    var buyQuantity: Int = 0,

    var netValue: Double? = null,

    var buyValue: Double? = null,

    var multiplier: Double? = null,

    @PrimaryKey
    var instrumentToken: String,

    var closePrice: Double? = null,

    var pnl: Double,

    var overnightQuantity: Int = 0,

    var buym2m: Double? = 0.0,

    var sellm2m: Double? = 0.0,

    var dayBuyQuantity: Double? = 0.0,

    var daySellQuantity: Double? = 0.0,

    var dayBuyPrice: Double? = 0.0,

    var daySellPrice: Double? = 0.0,

    var dayBuyValue: Double? = 0.0,

    var daySellValue: Double? = 0.0,

    var value: Double? = 0.0,

    var averagePrice: Double = 0.0,

    var updatedAt: Long = Instant.now().toEpochMilli()

//    @Embedded(prefix = "stopLoss")
//    var stopLoss: StopLoss
//    // local properties
//    var stopLossInPercent: Double? = null,
//
//    var stopLossPrice: Double? = null,
//
//    var isStopLossNotificationActive: Boolean = true,
//
//
)

data class PositionWithStopLoss(
    @Embedded
    val position: Position,
    @Relation(
        parentColumn = "instrumentToken",
        entityColumn = "instrumentToken"
    )
    val stopLoss: StopLoss?
)
