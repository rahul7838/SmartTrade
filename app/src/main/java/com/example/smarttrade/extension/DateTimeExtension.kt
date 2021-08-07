package com.example.smarttrade.extension

import android.text.format.DateUtils
import timber.log.Timber
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*

fun getExpiryTime(): Long {
    //given every morning between 8 and 9 old token will expire
    return if(isCurrentTimeGreaterThan8AM()) {
        val d = LocalDate.now().plusDays(1)
        val t = LocalTime.of(8, 0)
        val zoneDateTime = ZonedDateTime.of(d, t, ZoneId.systemDefault())
        zoneDateTime.toInstant().toEpochMilli()
    } else {
        val d = LocalDate.now()
        val t = LocalTime.of(8, 0)
        val zoneDateTime = ZonedDateTime.of(d, t, ZoneId.systemDefault())
        zoneDateTime.toInstant().toEpochMilli()
    }
}

fun isCurrentTimeGreaterThan8AM(): Boolean {
    val calender = Calendar.getInstance()
    calender.set(Calendar.HOUR_OF_DAY, 8)
    val currentTime = Calendar.getInstance().time.time
    val amInMilliSec = calender.timeInMillis
    Timber.i("currentTime $currentTime")
    Timber.i("amInMilliSec $amInMilliSec")
    return currentTime > amInMilliSec
}

fun getTimeAgo(oldTime: Long?): Triple<String, String, String> {
    val currentTime = Instant.now()
    val oldTimeInstant = oldTime?.let { Instant.ofEpochMilli(it) }
    val duration = Duration.between(oldTimeInstant, currentTime)
//    val diff = oldTimeInstant?.let { currentTime.minus(it, ChronoUnit.MILLIS) }
//    val zonedDateTime = diff?.atZone(ZoneId.systemDefault())
    val min = duration?.seconds
    val hr = duration?.toMinutes()
    val sec = duration?.toHours()
    return Triple(hr.toString(), min.toString(), sec.toString())
}

fun getTimeAgo2(oldTime: Long?): String {
    var timeAgo = ""
    if (oldTime != null) {
       timeAgo = DateUtils.getRelativeTimeSpanString(oldTime, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString()
    }
    return timeAgo
}