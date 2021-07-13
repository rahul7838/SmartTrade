package com.example.smarttrade.services

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.smarttrade.extension.logI
import com.example.smarttrade.services.SmartTradeAlarmManager.createPendingIntent
import com.example.smarttrade.util.STOP_UPDATING_POSITION_BROADCAST_INTENT_IDENTIFIER
import com.example.smarttrade.util.SET_USER_LOGGED_OFF_BROADCAST_INTENT_IDENTIFIER
import com.example.smarttrade.util.START_UPDATING_POSITION_BROADCAST_INTENT_IDENTIFIER
import org.koin.core.component.KoinApiExtension
import timber.log.Timber

const val UPDATE_POSITION = "updatePosition"
class StartUpdatingPositionBroadCastReceiver : BroadcastReceiver() {

    @KoinApiExtension
    override fun onReceive(context: Context?, intent: Intent?) {
        logI("on receive")

        when(intent?.identifier) {
            SET_USER_LOGGED_OFF_BROADCAST_INTENT_IDENTIFIER -> {
                logI("on receive user set user logged in false")
//                PreferenceManager.setUserLoggedIn(false)
            }
            STOP_UPDATING_POSITION_BROADCAST_INTENT_IDENTIFIER -> {
                logI("STOP UPDATING position")

                val alarmManager: AlarmManager? = context?.getSystemService(
                    Context.ALARM_SERVICE
                ) as? AlarmManager
                alarmManager?.cancel(createPendingIntent())
            }
            START_UPDATING_POSITION_BROADCAST_INTENT_IDENTIFIER -> {
                logI("start the position update task")

                val oneTimeWorkRequest = OneTimeWorkRequestBuilder<SyncPositionWorker>().build()

                val workManager: WorkManager? = context?.let { WorkManager.getInstance(it) }

                workManager?.beginUniqueWork(UPDATE_POSITION, ExistingWorkPolicy.KEEP, oneTimeWorkRequest)?.enqueue()
            }
        }


    }
}