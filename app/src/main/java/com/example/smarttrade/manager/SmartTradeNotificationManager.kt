package com.example.smarttrade.manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smarttrade.R
import com.example.smarttrade.ui.position.PortfolioActivity
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

@KoinApiExtension
object SmartTradeNotificationManager : KoinComponent {

    private val context: Context by inject()

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(
            NotificationManager::class.java
        )
    }

    fun buildStopLossPriceNotification(stopLossPrice: String, positionName: String) {
        val notification = getNotification("$positionName triggered at stop loss price $stopLossPrice")
        val random = Random(1000)
        with(NotificationManagerCompat.from(context)) {
            notify(random.nextInt(), notification)
        }
    }

    fun buildStopLossAmountNotification(stopLossAmount: String, positionName: String) {
        val notification = getNotification("$positionName triggered at stop loss Amount $stopLossAmount")
        val random = Random(1000)
        with(NotificationManagerCompat.from(context)) {
            notify(random.nextInt(), notification)
        }
    }

    fun getNotification(descriptionText: String): Notification {
        createChannel()
        val intent = Intent(context, PortfolioActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        return NotificationCompat.Builder(context, "notify001")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.resources.getString(R.string.app_name))
            .setContentText(descriptionText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setChannelId("channelId")
            .build()
    }

    private fun createChannel() {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("channelId", "Notification", IMPORTANCE_HIGH)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}