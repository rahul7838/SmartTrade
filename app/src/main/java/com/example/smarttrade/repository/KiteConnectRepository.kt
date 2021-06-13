package com.example.smarttrade.repository

import com.example.smarttrade.KiteConnect
import com.example.smarttrade.db.dao.PositionDao
import com.example.smarttrade.extension.parseLocal
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
    suspend fun fetchPositions() {
        Timber.d("fetch positions")
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

    suspend fun getPosition(): List<LocalPosition> {
        return positionDao.getPosition()
    }

}

typealias LocalPosition = com.example.smarttrade.db.entity.Position