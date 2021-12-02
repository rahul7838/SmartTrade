package com.example.smarttrade.ui.homescreen

import com.example.smarttrade.extension.*
import com.example.smarttrade.manager.PreferenceManager
import com.example.smarttrade.repository.UncleThetaRepository
import com.example.smarttrade.ui.base.BaseViewModel
import com.example.smarttrade.util.*
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException
import com.zerodhatech.models.Instrument
import com.zerodhatech.models.Order
import com.zerodhatech.models.OrderParams
import com.zerodhatech.models.Quote
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import org.json.JSONException
import org.koin.core.component.KoinApiExtension
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*

@KoinApiExtension
class HomeViewModel(
    private val uncleThetaRepository: UncleThetaRepository,
    requestToken: String?,
    private val io: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    @Volatile
    var inProgress = false

    init {
        if (requestToken != null) {
            ioDispatcher.launch {
                uncleThetaRepository.createSession(requestToken)
            }
        }
    }

    private var order2Price: Double? = null
    private var order1Price: Double? = null

    private val _overallResult = MutableStateFlow<Resource<Boolean>>(Resource.Success(true))
    val overallResult = _overallResult.asStateFlow()
    private val _peSingleShotBusEvent = Channel<Resource<OrderSuccess>>(Channel.BUFFERED)
    val singleShotEventBus = _peSingleShotBusEvent.receiveAsFlow()


    // region
    fun getNSEInstrument() {
        ioDispatcher.launch {
            try {
                uncleThetaRepository.getInstruments(NSE_EXCHANGE)
            } catch (error: Throwable) {

            }
        }
    }

    suspend fun getExpiryDate() = ioDispatcher.async {
        findExpiryDate()
    }

    val exceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable -> }
//endregion

    /** last price of nifty 50 using [com.zerodhatech.models.Quote] API */
    fun getNifty50() {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            _overallResult.value = Resource.Loading()
            supervisorScope {
                try {
                    val deferredExpiry = async {
                        logI("calculate expiry date")
                        findExpiryDate()
                    }
                    val deferredListOfNFOInstrument =
                        async { uncleThetaRepository.getInstruments(NFO_EXCHANGE) }
                    val deferredNifty50LastPrice =
                        async { uncleThetaRepository.getQuote(arrayOf(NIFTY_50_INSTRUMENT))[NIFTY_50_INSTRUMENT]?.lastPrice }
                    val nifty50LastPrice: Double? = deferredNifty50LastPrice.await()
                    val expiryDate: Date = deferredExpiry.await()
                    val listOfNFOInstrument: List<Instrument> = deferredListOfNFOInstrument.await()
                    processDate(
                        nifty50LastPrice,
                        listOfNFOInstrument,
                        expiryDate
                    )
                } catch (ioException: IOException) {
                    _overallResult.value = (Resource.Error(ioException.message))
                } catch (jsonException: JSONException) {
                    _overallResult.value = (Resource.Error(jsonException.message))
                } catch (kiteException: KiteException) {
                    _overallResult.value = (Resource.Error((kiteException.message)))
                }
            }
        }
    }

    private suspend fun processDate(
        nifty50LastPrice: Double?,
        listOfNFOInstrument: List<Instrument>,
        expiry: Date
    ) {
        val peNiftyInstrument: Instrument
        val ceNiftyInstrument: Instrument
        val peNiftyQuote: Quote?
        val ceNiftyQuote: Quote?
        val strike = closestMultipleOf50(nifty50LastPrice).removeDecimalPart()
        val filteredInstrumentBasedOnStrikeAndExpiry =
            listOfNFOInstrument.filter { instrument ->
                val expiryDate = instrument.expiry
                ((instrument.strike == strike && instrument.instrument_type == INSTRUMENT_TYPE_PE) || (instrument.strike == strike && instrument.instrument_type == INSTRUMENT_TYPE_CE)) && instrument.name == NAME_NIFTY
                        && expiryDate.year == expiry.year && expiryDate.month == expiry.month && expiryDate.date == expiry.date
            }
        peNiftyInstrument = filteredInstrumentBasedOnStrikeAndExpiry[0]
        ceNiftyInstrument = filteredInstrumentBasedOnStrikeAndExpiry[1]
        val niftyNFOListOfQuote: Map<String, Quote>
        try {
            niftyNFOListOfQuote = uncleThetaRepository.getQuote(
                arrayOf(
                    peNiftyInstrument.instrument_token.toString(),
                    ceNiftyInstrument.instrument_token.toString()
                )
            )
            peNiftyQuote = niftyNFOListOfQuote[peNiftyInstrument.instrument_token.toString()]
            ceNiftyQuote = niftyNFOListOfQuote[ceNiftyInstrument.instrument_token.toString()]
            checkSkew(
                peNiftyQuote,
                peNiftyInstrument,
                ceNiftyQuote,
                ceNiftyInstrument
            )
        } catch (ioException: IOException) {
            _overallResult.value = (Resource.Error(ioException.message))
        } catch (jsonException: JSONException) {
            _overallResult.value = (Resource.Error(jsonException.message))
        } catch (kiteException: KiteException) {
            _overallResult.value = (Resource.Error((kiteException.message)))
        }
    }

    private suspend fun checkSkew(
        peNiftyQuote: Quote?,
        peNiftyInstrument: Instrument,
        ceNiftyQuote: Quote?,
        ceNiftyInstrument: Instrument
    ) {
        val skew: Double
        try {
            skew = calculateSkew(peNiftyQuote?.lastPrice, ceNiftyQuote?.lastPrice)
            if (skew > 16) {
                _overallResult.value = (Resource.Error("Skew value is $skew which is > 0.3"))
            } else {
                logI("Execute sell order")
                supervisorScope {
                    val executePeOrder =
                        launch { executePeSellOrder(peNiftyQuote, peNiftyInstrument) }
                    val executeCeOrder =
                        launch { executeCeSellOrder(ceNiftyQuote, ceNiftyInstrument) }
                    executeCeOrder.join()
                    executePeOrder.join()
                    _overallResult.value = (Resource.Success(true))
                }
                logI("Execute sell order 2")
            }
        } catch (argumentException: IllegalArgumentException) {
            _overallResult.value = (Resource.Error(argumentException.message))
        }
    }

    private suspend fun executePeSellOrder(peNiftyQuote: Quote?, peNiftyInstrument: Instrument) {
        try {
            order1Price = peNiftyQuote?.depth?.buy?.get(0)?.price?.minus(0.25)
            val sellOrder = placeSellOrder(peNiftyInstrument, peNiftyQuote)
            _peSingleShotBusEvent.send(
                Resource.Success(
                    OrderSuccess(
                        order1Price.toString(),
                        peNiftyInstrument.tradingsymbol,
                        TRANSACTION_TYPE_SELL
                    )
                )
            )
            val priceAndTriggerPrice: Pair<Double?, Double?> = calculateStopLossPrice(peNiftyQuote)
            val slOrder = placeStopLossOrder(
                peNiftyInstrument,
                priceAndTriggerPrice.first,
                priceAndTriggerPrice.second
            )
            _peSingleShotBusEvent.send(
                Resource.Success(
                    OrderSuccess(
                        priceAndTriggerPrice.second.toString(),
                        peNiftyInstrument.tradingsymbol,
                        TRANSACTION_TYPE_BUY
                    )
                )
            )
            PreferenceManager.setPeSLOrderId(slOrder.orderId)
        } catch (ioException: IOException) {
            _peSingleShotBusEvent.send(Resource.Error(ioException.message))
        } catch (jsonException: JSONException) {
            _peSingleShotBusEvent.send(Resource.Error(jsonException.message))
        } catch (kiteException: KiteException) {
            _peSingleShotBusEvent.send(Resource.Error((kiteException.message)))
        }
    }

    private suspend fun executeCeSellOrder(ceNiftyQuote: Quote?, ceNiftyInstrument: Instrument) {
        try {
            order2Price = ceNiftyQuote?.depth?.buy?.get(0)?.price?.minus(0.25)
            val sellOrder = placeSellOrder(ceNiftyInstrument, ceNiftyQuote)
            _peSingleShotBusEvent.send(
                Resource.Success(
                    OrderSuccess(
                        order2Price.toString(),
                        ceNiftyInstrument.tradingsymbol,
                        TRANSACTION_TYPE_SELL
                    )
                )
            )
            val priceAndTriggerPrice: Pair<Double?, Double?> = calculateStopLossPrice(ceNiftyQuote)
            val slOrder = placeStopLossOrder(
                ceNiftyInstrument,
                priceAndTriggerPrice.first,
                priceAndTriggerPrice.second
            )
            _peSingleShotBusEvent.send(
                Resource.Success(
                    OrderSuccess(
                        priceAndTriggerPrice.second.toString(),
                        ceNiftyInstrument.tradingsymbol,
                        TRANSACTION_TYPE_BUY
                    )
                )
            )
            PreferenceManager.setCeSLOrderId(slOrder.orderId)
        } catch (ioException: IOException) {
            _peSingleShotBusEvent.send(Resource.Error(ioException.message))
        } catch (jsonException: JSONException) {
            _peSingleShotBusEvent.send(Resource.Error(jsonException.message))
        } catch (kiteException: KiteException) {
            _peSingleShotBusEvent.send(Resource.Error((kiteException.message)))
        }
    }

    @Throws(KiteException::class, IOException::class, JSONException::class)
    private suspend fun placeSellOrder(instrument: Instrument, quote: Quote?): Order {
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
        return uncleThetaRepository.placeOrder(orderParam = orderParam, variety = VARIETY_REGULAR)
//        return getMockOrder()
    }

    @Throws(KiteException::class, IOException::class, JSONException::class)
    private suspend fun placeStopLossOrder(
        instrument: Instrument,
        price1: Double?,
        triggerPrice1: Double?
    ): Order {
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
        return uncleThetaRepository.placeOrder(
            orderParam = orderParam,
            variety = VARIETY_REGULAR
        )
//        return getMockOrder()
    }

    private fun calculateStopLossPrice(quote: Quote?): Pair<Double?, Double?> {
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
                //                TODO Handle else scenario
                price1 = quote?.depth?.buy?.get(0)?.price?.times(1.4)?.toInt()?.toDouble()?.plus(1)
                triggerPrice1 = quote?.depth?.buy?.get(0)?.price?.times(1.4)?.toInt()?.toDouble()
            }
        }
        logI("Price:$price1, Trigger price $triggerPrice1")
        return Pair(price1, triggerPrice1)
//        return Pair(quote?.upperCircuitLimit, quote?.upperCircuitLimit)
    }

    //region square-off
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

    private suspend fun placeBuyOrder(instrument: Instrument) {
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
    //endregion


//    private suspend fun executeSellOrder(
//        peNiftyQuote: Quote?,
//        peNiftyInstrument: Instrument,
//        ceNiftyQuote: Quote?,
//        ceNiftyInstrument: Instrument
//    ) {
//        supervisorScope {
//            val deferredOrderId1 = async {
//                order1Price = peNiftyQuote?.depth?.buy?.get(0)?.price?.minus(0.25)
//                placeSellOrder(peNiftyInstrument, peNiftyQuote)
//            }
//            val deferredOrderId2 = async {
//                order2Price = ceNiftyQuote?.depth?.buy?.get(0)?.price?.minus(0.25)
//                placeSellOrder(ceNiftyInstrument, ceNiftyQuote)
//            }
//            try {
//                deferredOrderId1.await()
//                result.postValue(
//                    Resource.Success(
//                        OrderSuccess(
//                            order1Price.toString(),
//                            peNiftyInstrument.tradingsymbol,
//                            TRANSACTION_TYPE_BUY
//                        )
//                    )
//                )
//            } catch (ioException: IOException) {
//                result.postValue(Resource.Error(ioException.message))
//            } catch (jsonException: JSONException) {
//                result.postValue(Resource.Error(jsonException.message))
//            } catch (kiteException: KiteException) {
//                result.postValue(Resource.Error((kiteException.message)))
//            }
//
//            try {
//                deferredOrderId2.await()
//                result.postValue(
//                    Resource.Success(
//                        OrderSuccess(
//                            order2Price.toString(),
//                            ceNiftyInstrument.tradingsymbol,
//                            TRANSACTION_TYPE_BUY
//                        )
//                    )
//                )
//                executeStopLossOrder(
//                    ceNiftyInstrument,
//                    ceNiftyQuote,
//                    peNiftyInstrument,
//                    peNiftyQuote
//                )
//            } catch (ioException: IOException) {
//                result.postValue(Resource.Error(ioException.message))
//            } catch (jsonException: JSONException) {
//                result.postValue(Resource.Error(jsonException.message))
//            } catch (kiteException: KiteException) {
//                result.postValue(Resource.Error((kiteException.message)))
//            }
//        }
//    }


//    private suspend fun executeStopLossOrder(
//        ceNiftyInstrument: Instrument,
//        ceNiftyQuote: Quote?,
//        peNiftyInstrument: Instrument,
//        peNiftyQuote: Quote?
//    ) {
//        supervisorScope {
//            val deferredCeSLOrder = async {
//                placeStopLossOrder(ceNiftyInstrument, ceNiftyQuote)
//            }
//            val deferredPeSLOrder = async {
//                placeStopLossOrder(peNiftyInstrument, peNiftyQuote)
//            }
//            try {
//                val peSLOrder = deferredPeSLOrder.await()
//                val ceSLOrder = deferredCeSLOrder.await()
//                PreferenceManager.setPeSLOrderId(peSLOrder?.orderId)
//                PreferenceManager.setCeSLOrderId(ceSLOrder?.orderId)
//            } catch (ioException: IOException) {
//                result.postValue(Resource.Error(ioException.message))
//            } catch (jsonException: JSONException) {
//                result.postValue(Resource.Error(jsonException.message))
//            } catch (kiteException: KiteException) {
//                result.postValue(Resource.Error((kiteException.message)))
//            }
//        }
//    }

}