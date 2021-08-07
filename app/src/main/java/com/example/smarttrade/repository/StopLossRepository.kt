package com.example.smarttrade.repository

import com.example.smarttrade.db.dao.StopLossDao
import com.example.smarttrade.db.entity.StopLoss
import java.time.Instant

class StopLossRepository(private val stopLossDao: StopLossDao) {

    suspend fun getStopLossInPercent(instrumentToken: String): Double? {
        return stopLossDao.getStopLossInPercent(instrumentToken)
    }

    suspend fun updateStopLossInPercent(instrumentToken: String, stopLossInPercent: Double?, updatedAt: Long = Instant.now().toEpochMilli()) {
        stopLossDao.updateOrInsertStopLossInPercent(instrumentToken, stopLossInPercent, updatedAt)
    }

    suspend fun getStopLossPrice(instrumentToken: String): Double? {
        return stopLossDao.getStopLossPrice(instrumentToken)
    }

    suspend fun updateStopLossPrice(instrumentToken: String, stopLossPrice: Double?, updatedAt: Long = Instant.now().toEpochMilli()) {
        stopLossDao.updateAndInsertStopLossPrice(instrumentToken, stopLossPrice, updatedAt)
    }

    suspend fun updateAndInsertStopLoss(stopLoss : StopLoss) {
        stopLossDao.updateAndInsertStopLoss(stopLoss)
    }

    suspend fun getStopLossAmount(instrumentToken: String): Double? {
        return stopLossDao.getStopLossAmount(instrumentToken)
    }

}