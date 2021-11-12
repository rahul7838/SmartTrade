package com.example.smarttrade.ui.homescreen

import androidx.lifecycle.MutableLiveData
import com.example.smarttrade.extension.*
import com.example.smarttrade.manager.PreferenceManager
import com.example.smarttrade.repository.UncleThetaRepository
import com.example.smarttrade.ui.base.BaseViewModel
import com.example.smarttrade.util.*
import com.zerodhatech.models.Instrument
import com.zerodhatech.models.Order
import com.zerodhatech.models.OrderParams
import com.zerodhatech.models.Quote
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

class HomeViewModel(
    private val uncleThetaRepository: UncleThetaRepository,
    requestToken: String?
) : BaseViewModel() {

    init {
        if (requestToken != null) {
            ioDispatcher.launch {
                uncleThetaRepository.createSession(requestToken)
            }
        }
    }

    private var order2Price: Double? = null
    private var order1Price: Double? = null
    val peOrderId = MutableLiveData<Resource<Order>>()
    val ceOrderId = MutableLiveData<Resource<Order>>()

    fun getNSEInstrument() {
        ioDispatcher.launch {
            uncleThetaRepository.getInstruments(NSE_EXCHANGE)
        }
    }

    /** last price of nifty 50 using [com.zerodhatech.models.Quote] API */
    fun getNifty50() {
        ioDispatcher.launch {
            val deferredExpiry = defaultDispatcher.async {
                logI("")
                findExpiryDate()
            }
            val deferredListOfNFOInstrument =
                ioDispatcher.async { uncleThetaRepository.getInstruments(NFO_EXCHANGE) }
            var peNiftyInstrument: Instrument
            var ceNiftyInstrument: Instrument
            var peNiftyQuote: Quote?
            var ceNiftyQuote: Quote?
            do {
                val deferredNifty50LastPrice =
                    ioDispatcher.async { uncleThetaRepository.getQuote(arrayOf(NIFTY_50_INSTRUMENT))[NIFTY_50_INSTRUMENT]?.lastPrice }
                val expiry = deferredExpiry.await()
                val nifty50LastPrice = deferredNifty50LastPrice.await()
                val listOfNFOInstrument = deferredListOfNFOInstrument.await()
                val strike = closestMultipleOf50(nifty50LastPrice).removeDecimalPart()
                val filteredInstrumentBasedOnStrikeAndExpiry =
                    listOfNFOInstrument.filter { instrument ->
                        val expiryDate = instrument.expiry
                        ((instrument.strike == strike && instrument.instrument_type == INSTRUMENT_TYPE_PE) || (instrument.strike == strike && instrument.instrument_type == INSTRUMENT_TYPE_CE)) && instrument.name == NAME_NIFTY
                                && expiryDate.year == expiry.year && expiryDate.month == expiry.month && expiryDate.date == expiry.date
                    }
                peNiftyInstrument = filteredInstrumentBasedOnStrikeAndExpiry[0]
                ceNiftyInstrument = filteredInstrumentBasedOnStrikeAndExpiry[1]

                val niftyNFOListOfQuote = uncleThetaRepository.getQuote(
                    arrayOf(
                        peNiftyInstrument.instrument_token.toString(),
                        ceNiftyInstrument.instrument_token.toString()
                    )
                )
                peNiftyQuote = niftyNFOListOfQuote[peNiftyInstrument.instrument_token.toString()]
                ceNiftyQuote = niftyNFOListOfQuote[ceNiftyInstrument.instrument_token.toString()]
                val skew = calculateSkew(peNiftyQuote?.lastPrice, ceNiftyQuote?.lastPrice) ?: -1.0
            } while (skew == -1.0 || skew > 0.3)
            val deferredOrderId1 = ioDispatcher.async {
                order1Price = peNiftyQuote?.depth?.buy?.get(0)?.price?.minus(0.25)
                placeOrder(peNiftyInstrument, peNiftyQuote)
            }
            val deferredOrderId2 = ioDispatcher.async {
                order2Price = ceNiftyQuote?.depth?.buy?.get(0)?.price?.minus(0.25)
                placeOrder(ceNiftyInstrument, ceNiftyQuote)
            }
            val orderId1 = deferredOrderId1.await()
            val orderId2 = deferredOrderId2.await()
            logI("orderId1 $orderId1")
            logI("orderId2 $orderId2")
            val deferredCeSLOrder = ioDispatcher.async {
                placeStopLossOrder(ceNiftyInstrument, ceNiftyQuote)
            }
            val deferredPeSLOrder = ioDispatcher.async {
                placeStopLossOrder(peNiftyInstrument, peNiftyQuote)
            }
            val peSLOrder = deferredPeSLOrder.await()
            val ceSLOrder = deferredCeSLOrder.await()
            PreferenceManager.setPeSLOrderId(peSLOrder?.orderId)
            PreferenceManager.setCeSLOrderId(ceSLOrder?.orderId)
        }
    }

    fun squareOffOrder() {
        val ceSLOrderId = PreferenceManager.getCeSLOrderId()
        ioDispatcher.launch {
            val listOfOrder = uncleThetaRepository.getOrderHistory(ceSLOrderId)
            val order = listOfOrder.lastOrNull()
            if (order?.status != Enums.Status.COMPLETE.toString()) {
                uncleThetaRepository.cancelOrder(ceSLOrderId, VARIETY_REGULAR)
                placeBuyOrder(PreferenceManager.getCeJsonInstrument().toObject())
            }
        }
        val peSLOrderId = PreferenceManager.getPeSLOrderId()
        ioDispatcher.launch {
            val listOfOrder = uncleThetaRepository.getOrderHistory(peSLOrderId)
            val order = listOfOrder.lastOrNull()
            if (order?.status != Enums.Status.COMPLETE.toString()) {
                uncleThetaRepository.cancelOrder(peSLOrderId, VARIETY_REGULAR)
                placeBuyOrder(PreferenceManager.getPeJsonInstrument().toObject())
            }
        }
    }

    private fun placeBuyOrder(instrument: Instrument) {
        val quote = uncleThetaRepository.getQuote(arrayOf(instrument.instrument_token.toString()))
        val orderParam = OrderParams().apply {
            exchange = NFO_EXCHANGE
            tradingsymbol = instrument.tradingsymbol
            transactionType = TRANSACTION_TYPE_BUY
            quantity = QUANTITY_LOT_SIZE
            price = quote[instrument.instrument_token.toString()]?.depth?.buy?.get(0)?.price
            product = PRODUCT_NRML
            orderType = ORDER_TYPE_LIMIT
            validity = VALIDITY_DAY
            disclosedQuantity = 0
            triggerPrice = 0.0
            squareoff = 0.0
            stoploss = 0.0
            trailingStoploss = 0.0
            tag = TAG_TEST_UNCLE_THETA_WEDNESDAY
            parentOrderId = ""
        }
        uncleThetaRepository.placeOrder(
            orderParam = orderParam,
            variety = VARIETY_REGULAR
        )
    }

    private fun placeStopLossOrder(instrument: Instrument, quote: Quote?): Order? {
        val price1: Double?
        val triggerPrice1: Double?
        when (LocalDate.now().dayOfWeek) {
            DayOfWeek.WEDNESDAY -> {
                price1 = quote?.depth?.buy?.get(0)?.price?.times(1.4)?.toInt()?.toDouble()?.plus(1)
                triggerPrice1 = quote?.depth?.buy?.get(0)?.price?.times(1.4)?.toInt()?.toDouble()
            }
            DayOfWeek.THURSDAY -> {
                price1 = quote?.depth?.buy?.get(0)?.price?.times(1.3)?.toInt()?.toDouble()?.plus(1)
                triggerPrice1 = quote?.depth?.buy?.get(0)?.price?.times(1.3)?.toInt()?.toDouble()
            }
            else -> {
                return null
            }
        }
        val orderParam = OrderParams().apply {
            exchange = NFO_EXCHANGE
            tradingsymbol = instrument.tradingsymbol
            transactionType = TRANSACTION_TYPE_BUY
            quantity = QUANTITY_LOT_SIZE
            price = price1
            product = PRODUCT_NRML
            orderType = ORDER_TYPE_SL
            validity = VALIDITY_DAY
            disclosedQuantity = 0
            triggerPrice = triggerPrice1
            squareoff = 0.0
            stoploss = 0.0
            trailingStoploss = 0.0
            tag = TAG_TEST_UNCLE_THETA_WEDNESDAY
            parentOrderId = ""
        }
        try {
            return uncleThetaRepository.placeOrder(
                orderParam = orderParam,
                variety = VARIETY_REGULAR
            )
        } catch (exception: Throwable) {
            logI("place order $exception")
            exception.printStackTrace()
        }
        return null
    }


    private fun placeOrder(instrument: Instrument, quote: Quote?): Order? {
        val orderParam = OrderParams().apply {
            exchange = NFO_EXCHANGE
            tradingsymbol = instrument.tradingsymbol
            transactionType = TRANSACTION_TYPE_SELL
            quantity = QUANTITY_LOT_SIZE
            price = quote?.depth?.buy?.get(0)?.price?.minus(0.25)
            product = PRODUCT_NRML
            orderType = ORDER_TYPE_LIMIT
            validity = VALIDITY_DAY
            disclosedQuantity = 0
            triggerPrice = 0.0
            squareoff = 0.0
            stoploss = 0.0
            trailingStoploss = 0.0
            tag = TAG_TEST_UNCLE_THETA_WEDNESDAY
            parentOrderId = ""
        }
        try {
            return uncleThetaRepository.placeOrder(
                orderParam = orderParam,
                variety = VARIETY_REGULAR
            )
        } catch (exception: Throwable) {
            logI("place order $exception")
            exception.printStackTrace()
        }
        return null
    }
}