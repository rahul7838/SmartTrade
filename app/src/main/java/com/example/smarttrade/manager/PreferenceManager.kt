package com.example.smarttrade.manager

import android.content.SharedPreferences
import com.example.smarttrade.di.editPreferences
import com.example.smarttrade.di.getPreferences
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
object PreferenceManager : KoinComponent {

    private const val PE_JSON_INSTRUMENT: String = "peJsonInstrument"
    private const val CE_JSON_INSTRUMENT: String = "ceJsonInstrument"
    private const val CE_PARENT_ORDER_ID: String = "ceParentOrderId"
    private const val PE_PARENT_ORDER_ID: String = "peParentOrderId"
    private val sharedPreferences: SharedPreferences by inject()

    private const val ACCESS_TOKEN = "accessToken"
    private const val ACCESS_TOKEN_EXPIRY_TIME = "accessTokenTime"
    private const val IS_USER_LOGGED_IN = "isUserLoggedIn"

    fun getAccessToken(): String = sharedPreferences.getPreferences(ACCESS_TOKEN, "")
    fun setAccessToken(value: String) = sharedPreferences.editPreferences(ACCESS_TOKEN, value)

    fun getAccessTokenExpiryTime(): Long =
        sharedPreferences.getPreferences(ACCESS_TOKEN_EXPIRY_TIME, -1L)

    fun setAccessTokenExpiryTime(value: Long) =
        sharedPreferences.editPreferences(ACCESS_TOKEN_EXPIRY_TIME, value)


    fun getUserLoggedIn(): Boolean = sharedPreferences.getPreferences(IS_USER_LOGGED_IN, false)
    fun setUserLoggedIn(value: Boolean) =
        sharedPreferences.editPreferences(IS_USER_LOGGED_IN, value)

    fun getPeSLOrderId(): String = sharedPreferences.getPreferences(PE_PARENT_ORDER_ID, "")
    fun setPeSLOrderId(orderId: String?) =
        orderId?.let { sharedPreferences.editPreferences(PE_PARENT_ORDER_ID, it) }

    fun getCeSLOrderId(): String = sharedPreferences.getPreferences(CE_PARENT_ORDER_ID, "")
    fun setCeSLOrderId(orderId: String?) =
        orderId?.let { sharedPreferences.editPreferences(CE_PARENT_ORDER_ID, it) }

    fun setPeJsonInstrument(peJsonInstrument: String) =
        sharedPreferences.editPreferences(PE_JSON_INSTRUMENT, peJsonInstrument)

    fun getPeJsonInstrument(): String = sharedPreferences.getPreferences(PE_JSON_INSTRUMENT, "")

    fun setCeJsonInstrument(ceJsonInstrument: String) =
        sharedPreferences.editPreferences(CE_JSON_INSTRUMENT, ceJsonInstrument)

    fun getCeJsonInstrument(): String = sharedPreferences.getPreferences(CE_JSON_INSTRUMENT, "")

}