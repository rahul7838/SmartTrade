package com.example.smarttrade.db.dao

import androidx.room.*
import com.example.smarttrade.db.entity.Position
import com.example.smarttrade.repository.LocalPosition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface PositionDao {

    @Insert(entity = Position::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosition(position: Position)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPositions(positions: List<Position>)

    @Query("select * from Position")
    fun getPosition1(): Flow<List<Position>>

    fun getPosition(): Flow<List<Position>> {
        return getPosition1()
//        return getPosition1().distinctUntilChanged()
    }

    @Query("Select * from Position where instrumentToken=:instrumentToken")
    suspend fun getPosition(instrumentToken: String): LocalPosition

    @Query("Select instrumentToken From Position")
    suspend fun getInstrument(): Array<String>

    @Query("Update Position SET lastPrice =:lastPrice where instrumentToken=:instrumentToken")
    suspend fun updatePosition(instrumentToken: String, lastPrice: Double)

    @Query("Update Position SET lastPrice=:lastPrice, stopLossInPercent=:stopLossPriceInPercent, stopLossPrice=:stopLossPrice where instrumentToken=:instrumentToken")
    suspend fun updatePosition(instrumentToken: String, lastPrice: Double, stopLossPriceInPercent: Double, stopLossPrice: Double)

    @Query("Select netQuantity from Position where instrumentToken=:instrumentToken")
    suspend fun getNetQuantity(instrumentToken: String): Int
//
//    @Delete(entity = Position::class)
//    suspend fun deletePositions()

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