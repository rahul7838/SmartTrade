package com.example.smarttrade.repository

import com.example.smarttrade.KiteConnect
import com.example.smarttrade.db.dao.PositionDao
import com.example.smarttrade.extension.parseLocal
import com.zerodhatech.models.Position
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinApiExtension
import timber.log.Timber


@KoinApiExtension
class KiteConnectRepository(private val positionDao: PositionDao) {

    suspend fun createSession(requestToken: String) {
        KiteConnect.createSession(requestToken)
        Timber.d("session created")
        KiteConnect.sessionExpiryHook()
    }

    /**
     * Fetch positions - Fetches position from server and insert into local database
     *
     * @return
     */
    suspend fun fetchAndInsertPosition() {
        Timber.d("fetch and insert positions")
        val positions = KiteConnect.getPosition()

        positions.values.forEach {
            positionDao.insertPositions(it.parseLocal())
        }
        //region
//        positions.values.forEach {
//            Timber.d("hashmap %s", positions.keys.size)
//            it.forEach {
//                Timber.d(it.run {
//                    "product:$product " + "exchange:$exchange" + "sellValue:$sellValue" + "lastPrice:$lastPrice " + "unrealised:$unrealised " + "buyPrice:$buyPrice " +
//                            "sellPrice:$sellPrice " + "m2m:$m2m " + "tradingSymbol:$tradingSymbol " + "netQuantity:$netQuantity " +
//                            "sellQuantity:$sellQuantity " + "realised:$realised " + "buyQuantity:$buyQuantity " +
//                            "netValue:$netValue " + "buyValue$buyValue " + "multiplier:$multiplier " + "instrumentToken:$instrumentToken " +
//                            "closePrice:$closePrice " + "pnl:$pnl " + "overnightQuantity:$overnightQuantity " + "buym2m:$buym2m " +
//                            "sellm2m:$sellm2m " + "dayBuyQuantity:$dayBuyQuantity " + "daySellQuantity:$daySellQuantity " +
//                            "dayBuyPrice:$dayBuyPrice " + "daySellPrice:$daySellPrice " + "dayBuyValue:$dayBuyValue " +
//                            "daySellValue:$daySellValue " + "value:$value " + "averagePrice$averagePrice "
//                })
//            }
//        }
        //endregion
    }

    suspend fun insertPositionInBatch(positionMap: Map<String, List<Position>>) {
        positionMap.values.forEach {
            positionDao.insertPositions(it.parseLocal())
        }
    }

    suspend fun insertPosition(position: LocalPosition) {
        positionDao.insertPosition(position)
    }

    suspend fun fetchPosition(): Map<String, List<Position>> {
        Timber.d("fetch position")
        return KiteConnect.getPosition()
    }

    fun getPosition(): Flow<List<LocalPosition>> {
        return positionDao.getPosition()
    }

    suspend fun getStopLossInPercent(instrumentToken: String): Double? {
        return positionDao.getStopLossInPercent(instrumentToken)
    }

    suspend fun updateStopLossInPercent(instrumentToken: String, stopLossInPercent: Double?) {
        positionDao.updateStopLossInPercent(instrumentToken, stopLossInPercent)
    }

    suspend fun getOldStopLossPrice(instrumentToken: String): Double? {
        return positionDao.getOldStopLossPrice(instrumentToken)
    }

    suspend fun updateOldStopLossPrice(instrumentToken: String, stopLossPrice: Double?) {
        positionDao.updateOldStopLossPrice(instrumentToken, stopLossPrice)
    }

    suspend fun getOldLastPrice(instrumentToken: String): Double {
        return positionDao.getOldLastPrice(instrumentToken)
    }

    suspend fun insertOldStopLossPriceAndTrailingStopLossPercent() {

    }
}

typealias LocalPosition = com.example.smarttrade.db.entity.Position