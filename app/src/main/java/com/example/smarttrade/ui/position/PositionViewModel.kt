package com.example.smarttrade.ui.position

import androidx.annotation.StringDef
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.smarttrade.db.entity.PositionWithStopLoss
import com.example.smarttrade.db.entity.StopLoss
import com.example.smarttrade.extension.calculateTrigger
import com.example.smarttrade.extension.isBuyCall
import com.example.smarttrade.extension.logI
import com.example.smarttrade.repository.GroupRepository
import com.example.smarttrade.repository.KiteConnectRepository
import com.example.smarttrade.repository.StopLossRepository
import com.example.smarttrade.ui.base.BaseViewModel
import com.example.smarttrade.ui.position.PositionViewModel.StopLossType.Companion.STOP_LOSS_PERCENT
import com.example.smarttrade.ui.position.PositionViewModel.StopLossType.Companion.STOP_LOSS_PNL
import com.example.smarttrade.ui.position.PositionViewModel.StopLossType.Companion.STOP_LOSS_PRICE
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import java.time.Instant
import kotlin.math.abs

@KoinApiExtension
class PositionViewModel(
    private val kiteConnectRepository: KiteConnectRepository,
    private val stopLossRepository: StopLossRepository,
    private val groupRepository: GroupRepository,
    requestToken: String?
) : BaseViewModel() {

    init {
        if (requestToken != null) {
            createSession(requestToken)
        }
    }

    private fun createSession(requestToken: String) {
        ioDispatcher.launch {
            kiteConnectRepository.createSession(requestToken)
            kiteConnectRepository.fetchAndInsertPosition()
        }
    }

    fun refreshPosition() {
        ioDispatcher.launch {
            val instrumentToken = kiteConnectRepository.getInstrument()
            val quotes = kiteConnectRepository.getQuote(instrumentToken)
            quotes.forEach {
                calculateTrigger(
                    kiteConnectRepository,
                    stopLossRepository,
                    it.key,
                    it.value.lastPrice
                )
            }
        }
    }

    fun getPosition(): LiveData<List<PositionWithStopLoss>> {
        return kiteConnectRepository.getPosition().asLiveData()
    }

    suspend fun getTime(): Long {
        return kiteConnectRepository.getTime()
    }

    fun updateStopLoss(
        instrumentToken: String,
        lastPrice: Double,
        averagePrice: Double,
        netQuantity: Int,
        @StopLossType stopLossType: String,
        stopLoss: Double
    ) {
        ioDispatcher.launch {
            val isBuyCall = netQuantity.isBuyCall()
            if (isBuyCall) {
                when (stopLossType) {
                    STOP_LOSS_PERCENT -> {
                        val findStopLossValue = lastPrice * (1 - stopLoss / 100)
                        stopLossRepository.updateAndInsertStopLoss(
                            StopLoss(
                                instrumentToken,
                                stopLossInPercent = stopLoss,
                                stopLossPrice = findStopLossValue
                            )
                        )
                    }
                    STOP_LOSS_PRICE -> {
                        val findStopLossInPercent = 100 * (1 - stopLoss / lastPrice)
                        stopLossRepository.updateAndInsertStopLoss(
                            StopLoss(
                                instrumentToken,
                                findStopLossInPercent,
                                stopLoss
                            )
                        )
                    }
                    STOP_LOSS_PNL -> {
                        stopLossRepository.updateAndInsertStopLoss(
                            StopLoss(
                                instrumentToken,
                                stopLoss,
                                null,
                                stopLoss
                            )
                        )
                    }
                }
            } else {
                when (stopLossType) {
                    STOP_LOSS_PERCENT -> {
                        val findStopLossValue = lastPrice * (1 + stopLoss / 100)
                        stopLossRepository.updateAndInsertStopLoss(
                            StopLoss(
                                instrumentToken,
                                stopLoss,
                                findStopLossValue
                            )
                        )
                    }
                    STOP_LOSS_PRICE -> {
                        val findStopLossInPercent = 100 * (1 + stopLoss / lastPrice)
                        stopLossRepository.updateAndInsertStopLoss(
                            StopLoss(
                                instrumentToken,
                                findStopLossInPercent,
                                stopLoss
                            )
                        )
                    }
                    STOP_LOSS_PNL -> {
                        stopLossRepository.updateAndInsertStopLoss(
                            StopLoss(
                                instrumentToken,
                                stopLoss,
                                null,
                                stopLoss
                            )
                        )
                    }
                }
            }
        }
    }

    fun removeStopLoss(instrumentToken: String) {
        ioDispatcher.launch {
            stopLossRepository.updateAndInsertStopLoss(StopLoss(instrumentToken, null, null))
        }
    }

    override fun onCleared() {
        super.onCleared()
        logI("onClear")
    }

    fun updateGroup(listOfPositionWithStopLoss: List<PositionWithStopLoss>) {
        viewModelScope.launch {
            groupRepository.updateGroup(listOfPositionWithStopLoss)
        }
    }

    @StringDef(value = [STOP_LOSS_PERCENT, STOP_LOSS_PRICE, STOP_LOSS_PNL], open = false)
    annotation class StopLossType {
        companion object {
            const val STOP_LOSS_PERCENT = "stopLossPercent"
            const val STOP_LOSS_PRICE = "stopLossPrice"
            const val STOP_LOSS_PNL = "stopLossPnl"
        }
    }
//region
//    fun createSession(requestToken: String) {
//        val jobCreateSession = ioDispatcher.launch {
//            KiteConnect.createSession(requestToken)
//            Timber.d("session created")
//            KiteConnect.sessionExpiryHook()
//        }
//        runBlocking {
//            jobCreateSession.join()
//            getPosition()
//        }
//    }

//    private fun getPosition() {
//        val deferred = ioDispatcher.async {
//            KiteConnect.getPosition()
//        }
//        defaultDispatcher.launch {
//            val listOfPosition = deferred.await()
//            listOfPosition.values.first {
//                listOfPositionLiveData.postValue(it)
//                it.first {
//                    Timber.d(it.run {
//                        "product:$product " + "exchange:$exchange" + "sellValue:$sellValue" + "lastPrice:$lastPrice " + "unrealised:$unrealised " + "buyPrice:$buyPrice " +
//                                "sellPrice:$sellPrice " + "m2m:$m2m " + "tradingSymbol:$tradingSymbol " + "netQuantity:$netQuantity " +
//                                "sellQuantity:$sellQuantity " + "realised:$realised " + "buyQuantity:$buyQuantity " +
//                                "netValue:$netValue " + "buyValue$buyValue " + "multiplier:$multiplier " + "instrumentToken:$instrumentToken " +
//                                "closePrice:$closePrice " + "pnl:$pnl " + "overnightQuantity:$overnightQuantity " + "buym2m:$buym2m " +
//                                "sellm2m:$sellm2m " + "dayBuyQuantity:$dayBuyQuantity " + "daySellQuantity:$daySellQuantity " +
//                                "dayBuyPrice:$dayBuyPrice " + "daySellPrice:$daySellPrice " + "dayBuyValue:$dayBuyValue " +
//                                "daySellValue:$daySellValue " + "value:$value " + "averagePrice$averagePrice "
//                    })
//                    true
//                }
//                true
//            }
//        }
//
//    }
//endregion
}