package com.example.smarttrade.db.entity

import androidx.room.*
import androidx.room.ForeignKey.SET_NULL
import java.time.Instant

@Entity(tableName = "StopLoss", indices = [Index(value = ["instrumentToken"], unique = true)])
data class StopLoss(
    @ForeignKey(
        entity = Position::class,
        parentColumns = ["instrumentToken"],
        childColumns = ["instrumentToken"],
        onDelete = SET_NULL
    )
    var instrumentToken: String,
    var stopLossInPercent: Double?,
    var stopLossPrice: Double?,
    var stopLossInAmount: Double? = null,
    var updatedAt: Long = Instant.now().toEpochMilli(),
    var isNotificationActive: Boolean = true,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
    )