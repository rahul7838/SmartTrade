package com.example.smarttrade

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.NetworkOnMainThreadException
import com.example.smarttrade.extension.getExpiryTime
import com.example.smarttrade.extension.logI
import com.example.smarttrade.manager.PreferenceManager
import com.example.smarttrade.services.SmartTradeAlarmManager
import com.example.smarttrade.ui.login.MainActivity
import com.zerodhatech.kiteconnect.KiteConnect
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException
import com.zerodhatech.kiteconnect.kitehttp.exceptions.TokenException
import com.zerodhatech.models.Position
import com.zerodhatech.models.Quote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.io.IOException

@KoinApiExtension
object KiteConnect : KoinComponent {

    private val context: Context by inject()

    init {
        logI("Singleton class kite connect created")
    }

    private val kiteConnect: KiteConnect by lazy {
        KiteConnect("8tadu34g4bw6hz5c", true)
    }

    fun getLoginUrl(): String {
        return kiteConnect.loginURL
    }

    fun getQuote(instruments: Array<String>): Map<String, Quote> {
        return fetch { kiteConnect.getQuote(instruments) }
    }

    @Throws(NetworkOnMainThreadException::class)
    fun createSession(requestToken: String) {
        try {
            val user = kiteConnect.generateSession(requestToken, "2z4p8vzajhsw81w4kltbatc3ce4ghkto")
            user.accessToken.also {
                PreferenceManager.setAccessToken(it)
                PreferenceManager.setAccessTokenExpiryTime(getExpiryTime())
                kiteConnect.accessToken = it
                logI("access token $it")
                logI("expiryTime ${PreferenceManager.getAccessTokenExpiryTime()}")
            }
            kiteConnect.publicToken = user.publicToken
        } catch (ioException: IOException) {
            Timber.e(ioException)
            ioException.printStackTrace()
        } catch (kiteException: KiteException) {
            Timber.e(kiteException)
            kiteException.printStackTrace()
        }
    }

    @Throws(NetworkOnMainThreadException::class)
    fun getPosition(): Map<String, List<Position>> {
        return fetch { kiteConnect.positions }
    }

    fun sessionExpiryHook() {
        kiteConnect.setSessionExpiryHook {
            Timber.i("Session expired")
        }
    }

    fun setAccessToken(accessToken: String) {
        kiteConnect.accessToken = accessToken
    }

    fun setPublicToken(publicToken: String) {
        kiteConnect.publicToken = publicToken
    }

    fun finalize() {
        // finalization logic
        Timber.d("Before garbage collection")
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <K, V> fetch(action: () -> Map<K, V>): Map<K, V> {
        var result = mapOf<Any, Any>() as Map<K, V>
        return try {
            result = action()
            result
        } catch (tokenException: TokenException) {
            handleTokenFailure(tokenException)
            tokenException.printStackTrace()
            result
        }
    }

    private fun handleTokenFailure(tokenException: TokenException) {
        logI("$tokenException")
        CoroutineScope(Dispatchers.Main).launch {
            SmartTradeAlarmManager.stopUpdatingPosition()
            PreferenceManager.setUserLoggedIn(false)
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }
}
