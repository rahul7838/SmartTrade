package com.example.smarttrade.extension

import android.annotation.SuppressLint
import android.util.Log

private const val DEBUG_TAG = "Debugging: "

@SuppressLint("LogNotTimber")
fun Any.logI(message: String) = Log.i(this.toString(),DEBUG_TAG.plus(message))

@SuppressLint("LogNotTimber")
fun Any.logD(message: String) = Log.d(this.toString(), DEBUG_TAG.plus(message))