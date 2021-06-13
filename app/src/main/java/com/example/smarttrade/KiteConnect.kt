package com.example.smarttrade

import android.os.NetworkOnMainThreadException
import com.example.smarttrade.manager.PreferenceManager
import com.zerodhatech.kiteconnect.KiteConnect
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException
import com.zerodhatech.models.Position
import org.koin.core.component.KoinApiExtension
import timber.log.Timber
import java.io.IOException

@KoinApiExtension
object KiteConnect {


    init {
        Timber.d("Singleton class kite connect created")
    }

    private val kiteConnect: KiteConnect by lazy {
        KiteConnect("fwyko6uvpf7r8i07", true)
    }

    fun initKitConnect() {
        kiteConnect.userId = ""
        val url = kiteConnect.loginURL
    }

    fun getLoginUrl(): String {
        return kiteConnect.loginURL
    }

    @Throws(NetworkOnMainThreadException::class)
    fun createSession(requestToken: String) {
        try {
            val user = kiteConnect.generateSession(requestToken, "os0xtu7l5mkzvtfp281skxyy4iatqrnz")
            user.accessToken.also {
                PreferenceManager.setAccessToken(it)
                kiteConnect.accessToken = it
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
        return kiteConnect.positions
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
}
