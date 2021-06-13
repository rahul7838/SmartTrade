package com.example.smarttrade.ui.position

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.smarttrade.repository.KiteConnectRepository
import com.example.smarttrade.repository.LocalPosition
import com.example.smarttrade.ui.base.BaseViewModel
import com.zerodhatech.models.Position
import kotlinx.coroutines.flow.collect
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
            kiteConnectRepository.fetchPositions()
        }
    }

    fun getPosition(): LiveData<List<LocalPosition>> {
       return liveData {
           emit(kiteConnectRepository.getPosition())
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