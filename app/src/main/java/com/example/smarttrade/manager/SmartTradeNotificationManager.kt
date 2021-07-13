package com.example.smarttrade.manager

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat.from
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationManager : KoinComponent {

    private val context: Context by inject()

    val notificationManager: NotificationManager by lazy { context.getSystemService(NotificationManager::class.java) }

    fun createChannel() {
        Notification.
    }

    fun buildNotification() {
        Notification.Builder(context, channelId)
//        notificationManager.
    }
}