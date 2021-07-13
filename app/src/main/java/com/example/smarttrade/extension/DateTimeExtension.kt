package com.example.smarttrade.extension

import timber.log.Timber
import java.time.*
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