package com.example.smarttrade.repository

import com.example.smarttrade.KiteConnect
import com.example.smarttrade.db.dao.PositionDao
import com.example.smarttrade.extension.logI
import com.example.smarttrade.extension.parseLocal
import com.zerodhatech.models.Position
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinApiExtension


@KoinApiExtension
class KiteConnectRepository(private val positionDao: PositionDao) {

    suspend fun createSession(requestToken: String) {
        KiteConnect.createSession(requestToken)
        logI("session created")
        KiteConnect.sessionExpiryHook()
    }

    /**
     * Fetch positions - Fetches position from server and insert into local database
     *
     * @return
     */
    suspend fun fetchAndInsertPosition() {
        logI("fetch and insert positions")
        val positions = KiteConnect.getPosition()
        val oldInstrumentToken = positionDao.getInstrument()
        val newInstrumentToken = arrayListOf<String>()
        positions.values.forEach { list ->
            list.forEach {
                newInstrumentToken.add(it.instrumentToken)
            }
        }
        oldInstrumentToken.forEach {
            if(!newInstrumentToken.contains(it)) {
                deletePositionByInstrumentToken(it)
            }
        }
        positions.values.forEach {
            positionDao.insertPositions(it.parseLocal())
        }
    }

    suspend fun deletePositionByInstrumentToken(instrumentToken: String) {
        positionDao.deletePositionByInstrumentToken(instrumentToken)
    }

    fun getQuote(instrumentToken: Array<String>) =
        KiteConnect.getQuote(instrumentToken)


    suspend fun getInstrument(): Array<String> {
        return positionDao.getInstrument()
    }

    suspend fun updatePosition(instrumentToken: String, lastPrice: Double) {
        positionDao.updatePosition(instrumentToken, lastPrice = lastPrice)
    }

    suspend fun updatePosition(instrumentToken: String, lastPrice: Double, stopLossInPercent: Double, stopLossPrice: Double) {
        positionDao.updatePosition(instrumentToken, lastPrice, stopLossInPercent, stopLossPrice)
    }

    suspend fun getNetQuantity(instrumentToken: String): Int {
        return positionDao.getNetQuantity(instrumentToken)
    }

    suspend fun insertPositionInBatch(positionMap: Map<String, List<Position>>) {
        positionMap.values.forEach {
            positionDao.insertPositions(it.parseLocal())
        }
    }

    suspend fun insertPosition(position: LocalPosition) {
        logI("Position Updated")
        positionDao.insertPosition(position)
    }

    suspend fun fetchPosition(): Map<String, List<Position>> {
        logI("fetch position")
        return KiteConnect.getPosition()
    }

    fun getPosition(): Flow<List<LocalPosition>> {
        return positionDao.getPosition()
    }

    suspend fun getPosition(instrumentToken: String): LocalPosition {
        return positionDao.getPosition(instrumentToken)
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