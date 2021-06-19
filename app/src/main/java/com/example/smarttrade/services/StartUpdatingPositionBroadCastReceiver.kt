package com.example.smarttrade.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import org.koin.core.component.KoinApiExtension
import timber.log.Timber

const val UPDATE_POSITION = "updatePosition"
class StartUpdatingPositionBroadCastReceiver : BroadcastReceiver() {

    @KoinApiExtension
    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("on receive")

        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<SyncPositionWorker>().build()

        val workManager: WorkManager? = context?.let { WorkManager.getInstance(it) }

        workManager?.beginUniqueWork(UPDATE_POSITION, ExistingWorkPolicy.KEEP, oneTimeWorkRequest)?.enqueue()
    }
}