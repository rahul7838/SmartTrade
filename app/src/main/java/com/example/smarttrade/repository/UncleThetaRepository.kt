package com.example.smarttrade.repository

import com.example.smarttrade.KiteConnectService
import com.example.smarttrade.extension.logI
import com.example.smarttrade.util.NFO_EXCHANGE
import com.example.smarttrade.util.Resource
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException
import com.zerodhatech.models.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.json.JSONException
import java.io.IOException

class UncleThetaRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    @Throws(KiteException::class, IOException::class, JSONException::class)
    fun getInstruments(exchange: String): List<Instrument> {
        return KiteConnectService.kiteConnect.getInstruments(exchange)
    }

    @Throws(KiteException::class, IOException::class, JSONException::class)
    suspend fun getQuote(instrumentToken: Array<String>): Map<String, Quote> {
        logI("fetch quotes")
        return KiteConnectService.getQuote(instrumentToken)
    }

    @Throws(KiteException::class, IOException::class, JSONException::class)
    suspend fun placeOrder(orderParam: OrderParams, variety: String): Order {
        return KiteConnectService.placeOrder(orderParam, variety)
    }

    fun createSession(requestToken: String) {
        KiteConnectService.createSession(requestToken)
        logI("session created")
        KiteConnectService.sessionExpiryHook()
    }

    @Throws(KiteException::class, IOException::class, JSONException::class)
    fun getOrderHistory(orderId: String): List<Order> {
        return KiteConnectService.getOrderHistory(orderId)
    }

    @Throws(KiteException::class, IOException::class, JSONException::class)
    fun cancelOrder(orderId: String, variety: String): Order {
        return KiteConnectService.cancelOrder(orderId, variety)
    }

    @Throws(KiteException::class, IOException::class, JSONException::class)
    fun getOrderTrades(orderId: String): List<Trade> {
        return KiteConnectService.kiteConnect.getOrderTrades(orderId)
    }

    suspend fun getNFOInstrument(): Resource<List<Instrument>> {
        return KiteConnectService.getInstruments(NFO_EXCHANGE)
    }


}