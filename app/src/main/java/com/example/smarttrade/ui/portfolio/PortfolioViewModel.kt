package com.example.smarttrade.ui.portfolio

import androidx.lifecycle.MutableLiveData
import com.example.smarttrade.KiteConnect
import com.example.smarttrade.ui.base.BaseViewModel
import com.zerodhatech.models.Position
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinApiExtension
import timber.log.Timber

@KoinApiExtension
class PortfolioViewModel : BaseViewModel() {

    val listOfPositionLiveData = MutableLiveData<List<Position>>()

    fun createSession(requestToken: String) {
        val jobCreateSession = ioDispatcher.launch {
            KiteConnect.createSession(requestToken)
            Timber.d("session created")
            KiteConnect.sessionExpiryHook()
        }
        runBlocking {
            jobCreateSession.join()
            getPosition()
        }
    }

    private fun getPosition() {
        val deferred = ioDispatcher.async {
            KiteConnect.getPosition()
        }
        defaultDispatcher.launch {
            val listOfPosition = deferred.await()
            listOfPosition.values.first {
                listOfPositionLiveData.postValue(it)
                it.first {
                    Timber.d(it.run {
                        "product:$product "+ "exchange:$exchange" + "sellValue:$sellValue" + "lastPrice:$lastPrice "+ "unrealised:$unrealised "+ "buyPrice:$buyPrice "+
                                "sellPrice:$sellPrice "+ "m2m:$m2m "+"tradingSymbol:$tradingSymbol "+"netQuantity:$netQuantity "+
                                "sellQuantity:$sellQuantity "+"realised:$realised "+"buyQuantity:$buyQuantity "+
                                "netValue:$netValue "+"buyValue$buyValue "+"multiplier:$multiplier "+"instrumentToken:$instrumentToken "+
                                "closePrice:$closePrice "+ "pnl:$pnl "+"overnightQuantity:$overnightQuantity "+"buym2m:$buym2m "+
                                "sellm2m:$sellm2m "+"dayBuyQuantity:$dayBuyQuantity "+"daySellQuantity:$daySellQuantity "+
                                "dayBuyPrice:$dayBuyPrice "+"daySellPrice:$daySellPrice "+"dayBuyValue:$dayBuyValue "+
                                "daySellValue:$daySellValue "+"value:$value "+"averagePrice$averagePrice "
                    })
                    true
                }
                true
            }
        }

    }
}