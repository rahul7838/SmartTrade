package com.example.smarttrade.manager

import android.content.SharedPreferences
import com.example.smarttrade.di.editPreferences
import com.example.smarttrade.di.getPreferences
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
object PreferenceManager : KoinComponent {

    private val sharedPreferences: SharedPreferences by inject()

    private const val ACCESS_TOKEN = "accessToken"
    private const val IS_USER_LOGGED_IN = "isUserLoggedIn"

    fun getAccessToken(): String = sharedPreferences.getPreferences(ACCESS_TOKEN, "")
    fun setAccessToken(value: String) = sharedPreferences.editPreferences(ACCESS_TOKEN, value)

    fun getUserLoggedIn(): Boolean = sharedPreferences.getPreferences(IS_USER_LOGGED_IN, false)
    fun setUserLoggedIn(value: Boolean) = sharedPreferences.editPreferences(IS_USER_LOGGED_IN, value)


}