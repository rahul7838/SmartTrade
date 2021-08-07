package com.example.smarttrade.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smarttrade.db.entity.StopLoss
import com.example.smarttrade.extension.logI

@Dao
interface StopLossDao : BaseDao<StopLoss> {

    // region update get and insert stop loss in percent
    @Query("UPDATE StopLoss SET stopLossInPercent=:stopLossPriceInPercent, updatedAt=:updatedAt WHERE instrumentToken=:instrumentToken")
    suspend fun updateStopLossInPercent(instrumentToken: String, stopLossPriceInPercent: Double?, updatedAt: Long)

    suspend fun updateOrInsertStopLossInPercent(instrumentToken: String, stopLossPriceInPercent: Double?, updatedAt: Long, isNotification: Boolean = true) {
        val doesExists = getStopLossInPercent(instrumentToken)
        if(doesExists != null) {
            updateStopLossInPercent(instrumentToken, stopLossPriceInPercent, updatedAt)
        } else {
            logI("instrumentToken $instrumentToken")
            insertStopLossInPercent(instrumentToken, stopLossPriceInPercent, updatedAt, isNotification)
        }
    }

    @Query("select stopLossInPercent from StopLoss where instrumentToken = :instrumentToken")
    suspend fun getStopLossInPercent(instrumentToken: String): Double?

    @Query("INSERT INTO StopLoss (instrumentToken, stopLossInPercent, updatedAt, isNotificationActive) values (:instrumentToken, :stopLossPriceInPercent, :updatedAt, :isNotification)")
    suspend fun insertStopLossInPercent(instrumentToken: String, stopLossPriceInPercent: Double?, updatedAt: Long, isNotification: Boolean)
    //endregion

    //region update and insert stop loss price

    suspend fun updateAndInsertStopLossPrice(instrumentToken: String, stopLossPrice: Double?, updatedAt: Long, isNotification: Boolean = true) {
        val doesExists = getStopLossPrice(instrumentToken)
        if(doesExists!=null) {
            updateStopLossPrice(instrumentToken, stopLossPrice, updatedAt)
        } else {
            logI("instrumentToken $instrumentToken")
            insertStopLossPrice(instrumentToken, stopLossPrice, updatedAt, isNotification)
        }
    }

    @Query("INSERT INTO StopLoss (instrumentToken, stopLossPrice, updatedAt, isNotificationActive) values (:instrumentToken, :stopLossPrice, :updatedAt, :isNotification)")
    suspend fun insertStopLossPrice(instrumentToken: String, stopLossPrice: Double?, updatedAt: Long, isNotification: Boolean)

    @Query("UPDATE StopLoss SET stopLossPrice = :stopLossPrice, updatedAt=:updatedAt where instrumentToken=:instrumentToken")
    suspend fun updateStopLossPrice(instrumentToken: String, stopLossPrice: Double?, updatedAt: Long)

    @Query("select stopLossPrice from StopLoss where instrumentToken=:instrumentToken")
    suspend fun getStopLossPrice(instrumentToken: String): Double?
    //endregion

    //region update and insert stop loss
    suspend fun updateAndInsertStopLoss(stopLoss:StopLoss) {
        val doesExists = getStopLossPrice(stopLoss.instrumentToken)
        stopLoss.run {
            if(doesExists != null) {
               updateStopLoss(instrumentToken, stopLossPrice, stopLossInPercent, updatedAt, isNotificationActive)
            } else {
                logI("instrumentToken $instrumentToken")
                insert(stopLoss)
            }
        }
    }

    @Query("UPDATE StopLoss set stopLossPrice=:stopLossPrice, stopLossInPercent=:stopLossInPercent, updatedAt=:updatedAt, isNotificationActive=:isNotification where instrumentToken=:instrumentToken")
    suspend fun updateStopLoss(instrumentToken: String, stopLossPrice: Double?, stopLossInPercent: Double?, updatedAt: Long, isNotification: Boolean)

    @Query("Select stopLossInAmount from StopLoss where instrumentToken=:instrumentToken")
    fun getStopLossAmount(instrumentToken: String): Double?

    //endregion
}