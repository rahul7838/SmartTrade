package com.example.smarttrade.di

import android.content.SharedPreferences
import java.lang.IllegalArgumentException

fun SharedPreferences.editPreferences(key: String, value: Any) {
    when (value) {
        is Boolean -> this.edit().putBoolean(key, value).apply()
        is String -> this.edit().putString(key, value).apply()
        is Float -> this.edit().putFloat(key, value).apply()
        is Long -> this.edit().putLong(key, value).apply()
        is Int -> this.edit().putInt(key, value).apply()
        else -> { throw IllegalArgumentException("Argument of type ${value::class} not supported")}
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> SharedPreferences.getPreferences(key: String, defaultValue: T): T {
    return when (defaultValue) {
        is Boolean -> this.getBoolean(key, defaultValue) as T
        is String -> this.getString(key, defaultValue) as T
        is Long -> this.getLong(key, defaultValue) as T
        is Int -> this.getInt(key, defaultValue) as T
        else -> { defaultValue }
    }
}
