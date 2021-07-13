package com.example.smarttrade.services

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import com.example.smarttrade.extension.logI
import com.example.smarttrade.util.*
import com.example.smarttrade.util.STOP_UPDATING_POSITION_BROADCAST_INTENT_IDENTIFIER
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

@KoinApiExtension
object SmartTradeAlarmManager : KoinComponent {

    private val context: Context by inject()

    private val alarmManager: AlarmManager? =
        context.getSystemService(ALARM_SERVICE) as? AlarmManager


    fun scheduleTask() {
        logI("schedule task")
        startUpdatingPosition()
//        stopUpdatingPosition()
//        setTokenExpire()
    }

    private fun startUpdatingPosition() {
        val calender = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
//            set(Calendar.HOUR_OF_DAY, 9)
        }
        alarmManager?.setRepeating(RTC_WAKEUP, calender.timeInMillis, 60000, createPendingIntent())
    }

    fun stopUpdatingPosition() {
        val calenderTimeToCancelSetAlarm = Calendar.getInstance()
        calenderTimeToCancelSetAlarm.run {
            timeInMillis = System.currentTimeMillis()
//            set(Calendar.HOUR_OF_DAY, 15)
//            set(Calendar.MINUTE, 30)
//            set(Calendar.DATE, 1)// for testing
        }
        alarmManager?.setExactAndAllowWhileIdle(
            RTC_WAKEUP,
            calenderTimeToCancelSetAlarm.timeInMillis,
            cancelPendingIntent()
        )
    }

    private fun setTokenExpire() {
        val calender = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
//            set(Calendar.DATE, 1) // testing
        }
        alarmManager?.setExactAndAllowWhileIdle(
            RTC_WAKEUP,
            calender.timeInMillis,
            setUserLoggedOffPendingIntent()
        )
    }

    fun createPendingIntent(): PendingIntent {
        val intent = Intent(context, StartUpdatingPositionBroadCastReceiver::class.java)
        intent.identifier = START_UPDATING_POSITION_BROADCAST_INTENT_IDENTIFIER
        return PendingIntent.getBroadcast(
            context,
            START_UPDATING_POSITION_REQUEST_CODE,
            intent,
            FLAG_CANCEL_CURRENT
        )
    }

    private fun cancelPendingIntent(): PendingIntent {
        val intent = Intent(context, StartUpdatingPositionBroadCastReceiver::class.java)
        intent.identifier = STOP_UPDATING_POSITION_BROADCAST_INTENT_IDENTIFIER
        return PendingIntent.getBroadcast(
            context,
            CANCEL_UPDATING_POSITION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )
    }

    private fun setUserLoggedOffPendingIntent(): PendingIntent {
        val intent = Intent(context, StartUpdatingPositionBroadCastReceiver::class.java)
        intent.identifier = SET_USER_LOGGED_OFF_BROADCAST_INTENT_IDENTIFIER
        return PendingIntent.getBroadcast(
            context,
            USER_LOGGED_OFF_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )
    }


}