package com.example.smarttrade.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.smarttrade.extension.calculateTrigger
import com.example.smarttrade.extension.logI
import com.example.smarttrade.extension.startCoroutineTimer
import com.example.smarttrade.manager.SmartTradeNotificationManager
import com.example.smarttrade.repository.KiteConnectRepository
import com.example.smarttrade.repository.StopLossRepository
import com.example.smarttrade.util.Constants
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

private const val TWO_MIN = 2*60*1000L
@KoinApiExtension
class PositionUpdateService : Service(), KoinComponent {

    private val kiteConnectRepository: KiteConnectRepository by inject()
    private val stopLossRepository: StopLossRepository by inject()

    override fun onCreate() {
        super.onCreate()
        val notification = SmartTradeNotificationManager.getNotification(Constants.FOREGROUND_NOTIFICATION_DESC)
        startForeground(Constants.NOTIFI_ID_1978, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startUpdatingPosition1()
        return START_STICKY
    }

    private fun startUpdatingPosition1() {
        startCoroutineTimer(0L, TWO_MIN) {
            val hr = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val min = Calendar.getInstance().get(Calendar.MINUTE)
            logI("Minutes $hr:hr$min:min")
            val instrument = kiteConnectRepository.getInstrument()
            val quotes = kiteConnectRepository.getQuote(instrument)
            quotes.forEach {
                calculateTrigger(kiteConnectRepository, stopLossRepository, it.key, it.value.lastPrice)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}