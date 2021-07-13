package com.example.smarttrade.extension

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

inline fun <reified T> create(context: Context, requestCode: Int, flag: Int): PendingIntent {
    val intent = Intent(context, T::class.java)
    return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT)
}