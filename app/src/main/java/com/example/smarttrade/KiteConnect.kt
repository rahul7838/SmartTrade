package com.example.smarttrade

import android.os.NetworkOnMainThreadException
import com.zerodhatech.kiteconnect.KiteConnect
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException
import com.zerodhatech.models.Position
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import kotlin.jvm.Throws

object KiteConnect {

//    private val kiteConnect: KiteConnect by lazy { com.zerodhatech.kiteconnect.KiteConnect("fwyko6uvpf7r8i07") }

    private val kiteConnect: KiteConnect = KiteConnect("fwyko6uvpf7r8i07")

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
            kiteConnect.accessToken = user.accessToken
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


}