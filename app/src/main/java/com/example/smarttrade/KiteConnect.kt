package com.example.smarttrade

import android.content.Context
import android.content.Intent
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
import java.time.LocalTime

@KoinApiExtension
object KiteConnect : KoinComponent {

    private val context: Context by inject()

    init {
        logI("Singleton class kite connect created")
    }

    private val kiteConnect: KiteConnect by lazy {
        KiteConnect("8tadu34g4bw6hz5c", true)
    }

    fun initKitConnect() {
        kiteConnect.userId = ""
        val url = kiteConnect.loginURL
    }

    fun getLoginUrl(): String {
        return kiteConnect.loginURL
    }

    fun getQuote(instruments: Array<String>): Map<String, Quote> {
        return try {
            kiteConnect.getQuote(instruments)
        } catch (tokenException: TokenException) {
            CoroutineScope(Dispatchers.Main).launch {
                logI("$tokenException")
                SmartTradeAlarmManager.stopUpdatingPosition()
                PreferenceManager.setUserLoggedIn(false)
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
            tokenException.printStackTrace()
            emptyMap()
        }
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
        return try {
            kiteConnect.positions
        } catch (tokenException: TokenException) {
            CoroutineScope(Dispatchers.Main).launch {
                logI("$tokenException")
                SmartTradeAlarmManager.stopUpdatingPosition()
                PreferenceManager.setUserLoggedIn(false)
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
            tokenException.printStackTrace()
            emptyMap()
        }
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

    fun handleTokenFailure() {
        try {

        } catch (tokenException: TokenException) {

        }
    }
}
