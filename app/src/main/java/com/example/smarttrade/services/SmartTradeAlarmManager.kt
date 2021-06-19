package com.example.smarttrade.services

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import com.example.smarttrade.util.START_UPDATING_POSITION_REQUEST_CODE
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.*

@KoinApiExtension
object SmartTradeAlarmManager : KoinComponent {

    private val context: Context by inject()

    private val alarmManager: AlarmManager? = context.getSystemService(ALARM_SERVICE) as? AlarmManager

    private val calender  = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 9)
//        set(Calendar.MINUTE, 20)
    }

    fun startUpdatingPosition() {
        Timber.d("set alarm")
//        alarmManager?.setExactAndAllowWhileIdle(RTC_WAKEUP, calender.timeInMillis, createPendingIntent())
        alarmManager?.setRepeating(RTC_WAKEUP, calender.timeInMillis, 300000, createPendingIntent())
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(context, StartUpdatingPositionBroadCastReceiver::class.java)
        return PendingIntent.getBroadcast(context, START_UPDATING_POSITION_REQUEST_CODE, intent, FLAG_CANCEL_CURRENT)
    }
}