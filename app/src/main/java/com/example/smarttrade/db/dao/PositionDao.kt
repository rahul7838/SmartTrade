package com.example.smarttrade.db.dao

import androidx.room.*
import com.example.smarttrade.db.entity.Position
import kotlinx.coroutines.flow.Flow

@Dao
interface PositionDao {

    @Insert(entity = Position::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosition(position: Position)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPositions(positions: List<Position>)

    @Query("select * from Position")
    fun getPosition(): Flow<List<Position>>

    @Query("select stopLossInPercent from Position where instrumentToken = :instrumentToken")
    suspend fun getStopLossInPercent(instrumentToken: String): Double?

    @Query("select lastPrice from Position where instrumentToken=:instrumentToken")
    suspend fun getOldLastPrice(instrumentToken: String): Double

    @Query("UPDATE Position SET stopLossPrice = :stopLossPrice where instrumentToken=:instrumentToken")
    suspend fun updateOldStopLossPrice(instrumentToken: String, stopLossPrice: Double?)

    @Query("select stopLossPrice from Position where instrumentToken=:instrumentToken")
    suspend fun getOldStopLossPrice(instrumentToken: String): Double?

    @Query("UPDATE Position SET stopLossInPercent=:stopLossPriceInPercent WHERE instrumentToken=:instrumentToken")
        suspend fun updateStopLossInPercent(instrumentToken: String, stopLossPriceInPercent: Double?)
}