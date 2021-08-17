package com.example.smarttrade.db.dao

import androidx.room.*
import com.example.smarttrade.db.entity.BottomSheetDataObject
import com.example.smarttrade.db.entity.Position
import com.example.smarttrade.extension.parseLocal
import com.example.smarttrade.repository.LocalPosition
import kotlinx.coroutines.flow.Flow

@Dao
interface PositionDao : BaseDao<Position> {

    @Insert(entity = Position::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosition(position: Position)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPositions(positions: List<Position>)

    @Transaction
    @Query("select * from Position")
    fun getPosition1(): Flow<List<BottomSheetDataObject.PositionWithStopLoss>>

    fun getPosition(): Flow<List<BottomSheetDataObject.PositionWithStopLoss>> {
        return getPosition1()
//        return getPosition1().distinctUntilChanged()
    }

    @Query("Select * from Position where instrumentToken=:instrumentToken")
    suspend fun getPosition(instrumentToken: String): LocalPosition

    @Query("Select instrumentToken From Position")
    suspend fun getInstrument(): Array<String>

    @Query("Update Position SET lastPrice =:lastPrice, updatedAt=:updatedAt, pnl=:pnl where instrumentToken=:instrumentToken")
    suspend fun updatePosition(instrumentToken: String, lastPrice: Double, updatedAt: Long, pnl: Double)

    @Query("""UPDATE POSITION SET product=:product, exchange=:exchange, sellValue=:sellValue, lastPrice=:lastPrice, unrealised=:unrealised,
        buyPrice=:buyPrice, sellPrice=:sellPrice, m2m=:m2m, tradingSymbol=:tradingSymbol, netQuantity=:netQuantity, 
        sellQuantity=:sellQuantity, realised=:realised, buyQuantity=:buyQuantity, netValue=:netValue, buyValue=:buyValue,
        multiplier=:multiplier, closePrice=:closePrice, pnl=:pnl, overnightQuantity=:overnightQuantity,
        buym2m=:buym2m, sellm2m=:sellm2m, dayBuyQuantity=:dayBuyQuantity, daySellQuantity=:daySellQuantity, dayBuyPrice=:dayBuyPrice,
        daySellPrice=:daySellPrice, dayBuyValue=:dayBuyValue, daySellValue=:daySellValue, value=:value, averagePrice=:averagePrice 
        where instrumentToken=:instrumentToken""")
    suspend fun updatePosition(
        product: String,
        exchange: String,
        sellValue: Double,
        lastPrice: Double,
        unrealised: Double,
        buyPrice: Double,
        sellPrice: Double,
        m2m: Double,
        tradingSymbol: String,
        netQuantity: Int,
        sellQuantity: Int,
        realised: Double,
        buyQuantity: Int,
        netValue: Double?,
        buyValue: Double,
        multiplier: Double,
        instrumentToken: String,
        closePrice: Double,
        pnl: Double,
        overnightQuantity: Int,
        buym2m: Double,
        sellm2m: Double,
        dayBuyQuantity: Double,
        daySellQuantity: Double,
        dayBuyPrice: Double,
        daySellPrice: Double,
        dayBuyValue: Double,
        daySellValue: Double,
        value: Double,
        averagePrice: Double
    )

    suspend fun updateOrInsert(position: com.zerodhatech.models.Position) {
        val localPosition = getPosition(position.instrumentToken)
        if(localPosition != null) {
            position.run {
                updatePosition(
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
        } else {
            insertPosition(position.parseLocal())
        }
    }

    //region stop loss and position table query
    @Transaction
    suspend fun updateLastPriceAndStopLoss(instrumentToken: String, lastPrice: Double, newPnl: Double, stopLossPriceInPercent: Double?, stopLossPrice: Double?, stopLossAmount: Double?, updatedAt: Long) {
        updateStopLoss(instrumentToken, stopLossPriceInPercent, stopLossPrice, stopLossAmount)
        updateLastPrice(instrumentToken, lastPrice, newPnl, updatedAt)
    }

    @Query("update StopLoss set stopLossInPercent=:stopLossPriceInPercent, stopLossPrice=:stopLossPrice, stopLossInAmount=:stopLossAmount where instrumentToken=:instrumentToken")
    suspend fun updateStopLoss(instrumentToken: String, stopLossPriceInPercent: Double?, stopLossPrice: Double?, stopLossAmount: Double?)

    @Query("Update Position set lastPrice=:lastPrice, updatedAt=:updatedAt, pnl=:newPnl where instrumentToken=:instrumentToken")
    suspend fun updateLastPrice(instrumentToken: String, lastPrice: Double, newPnl: Double, updatedAt: Long)

    //endregion

    @Query("Select netQuantity from Position where instrumentToken=:instrumentToken")
    suspend fun getNetQuantity(instrumentToken: String): Int

    @Query("select lastPrice from Position where instrumentToken=:instrumentToken")
    suspend fun getOldLastPrice(instrumentToken: String): Double

    @Query("Delete from Position where instrumentToken=:instrumentToken")
    suspend fun deletePositionByInstrumentToken(instrumentToken: String)

    @Query("Select updatedAt from position order by updatedAt DESC")
    suspend fun getTime(): Long?
}