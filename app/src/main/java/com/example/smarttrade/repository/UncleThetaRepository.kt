package com.example.smarttrade.repository

import com.example.smarttrade.KiteConnect
import com.example.smarttrade.extension.logI
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException
import com.zerodhatech.models.*
import org.json.JSONException
import java.io.IOException

class UncleThetaRepository() {

    fun getInstruments(exchange: String): List<Instrument> = KiteConnect.getInstruments(exchange)

    fun getQuote(instrumentToken: Array<String>): Map<String, Quote> {
        logI("fetch quotes")
        return KiteConnect.getQuote(instrumentToken)
    }

    @Throws(KiteException::class, IOException::class, JSONException::class)
    fun placeOrder(orderParam: OrderParams, variety: String): Order {
        return KiteConnect.placeOrder(orderParam, variety)
    }

    fun createSession(requestToken: String) {
        KiteConnect.createSession(requestToken)
        logI("session created")
        KiteConnect.sessionExpiryHook()
    }

    @Throws(KiteException::class, IOException::class, JSONException::class)
    fun getOrderHistory(orderId: String): List<Order> {
        return KiteConnect.getOrderHistory(orderId)
    }

    @Throws(KiteException::class, IOException::class, JSONException::class)
    fun cancelOrder(orderId: String, variety: String): Order {
        return KiteConnect.cancelOrder(orderId, variety)
    }

    @Throws(KiteException::class, IOException::class, JSONException::class)
    fun getOrderTrades(orderId: String): List<Trade> {
        return KiteConnect.kiteConnect.getOrderTrades(orderId)
    }


}