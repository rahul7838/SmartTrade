package com.example.smarttrade.ui.position

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.smarttrade.extension.calculateTrigger
import com.example.smarttrade.extension.isBuyCall
import com.example.smarttrade.extension.logI
import com.example.smarttrade.repository.KiteConnectRepository
import com.example.smarttrade.repository.LocalPosition
import com.example.smarttrade.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class PositionViewModel(
    private val kiteConnectRepository: KiteConnectRepository,
    requestToken: String?
) :
    BaseViewModel() {

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
                calculateTrigger(kiteConnectRepository, it.key, it.value.lastPrice)
            }
        }
    }


    fun getPosition(): LiveData<List<LocalPosition>> {
        return kiteConnectRepository.getPosition().asLiveData()
    }

    fun updateStopLoss(
        instrumentToken: String,
        lastPrice: Double,
        isInPercent: Boolean,
        stopLoss: Double
    ) {
        ioDispatcher.launch {
            val isBuyCall = kiteConnectRepository.getNetQuantity(instrumentToken).isBuyCall()
            if(isBuyCall) {
                if (isInPercent) {
                    kiteConnectRepository.updateStopLossInPercent(instrumentToken, stopLoss)
                    val findStopLossValue = lastPrice * (1 - stopLoss / 100)
                    kiteConnectRepository.updateOldStopLossPrice(instrumentToken, findStopLossValue)
                } else {
                    kiteConnectRepository.updateOldStopLossPrice(instrumentToken, stopLoss)
                    val findStopLossInPercent = 100 * (1 - stopLoss / lastPrice)
                    kiteConnectRepository.updateStopLossInPercent(
                        instrumentToken,
                        findStopLossInPercent
                    )
                }
            } else {
                if (isInPercent) {
                    kiteConnectRepository.updateStopLossInPercent(instrumentToken, stopLoss)
                    val findStopLossValue = lastPrice * (1 + stopLoss / 100)
                    kiteConnectRepository.updateOldStopLossPrice(instrumentToken, findStopLossValue)
                } else {
                    kiteConnectRepository.updateOldStopLossPrice(instrumentToken, stopLoss)
                    val findStopLossInPercent = 100 * (1 + stopLoss / lastPrice)
                    kiteConnectRepository.updateStopLossInPercent(
                        instrumentToken,
                        findStopLossInPercent
                    )
                }
            }
        }
    }

    fun removeStopLoss(instrumentToken: String) {
        ioDispatcher.launch {
            kiteConnectRepository.updateStopLossInPercent(instrumentToken, null)
            kiteConnectRepository.updateOldStopLossPrice(instrumentToken, null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        logI("onClear")
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