package com.example.smarttrade.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import com.example.smarttrade.KiteConnect
import com.example.smarttrade.R
import com.example.smarttrade.databinding.ActivityMainBinding
import com.example.smarttrade.extension.logI
import com.example.smarttrade.extension.startActivity
import com.example.smarttrade.manager.PreferenceManager
import com.example.smarttrade.services.SmartTradeAlarmManager
import com.example.smarttrade.ui.base.BaseActivity
import com.example.smarttrade.ui.base.BaseViewModel
import com.example.smarttrade.ui.position.PortfolioActivity
import com.example.smarttrade.util.REQUEST_TOKEN
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension
import java.time.Instant

@KoinApiExtension
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val loginViewModel: LoginViewModel by viewModel()

    override fun getViewModel(): BaseViewModel = loginViewModel

    override var layoutId: Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar("SmartTrade")
//        if(PreferenceManager.getUserLoggedIn()) {
            val expiryTime = PreferenceManager.getAccessTokenExpiryTime()
            val currentTime = Instant.now().epochSecond
            if(expiryTime < currentTime) {
                //token is expired
                PreferenceManager.setUserLoggedIn(false)
            } else {
                val accessToken = PreferenceManager.getAccessToken()
                KiteConnect.setAccessToken(accessToken)
                startActivity<PortfolioActivity>()
                finish()
            }
//        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        logI("executed")
        val requestToken = intent?.data?.getQueryParameter("request_token")
        val bundle = bundleOf(REQUEST_TOKEN to requestToken)
        PreferenceManager.setUserLoggedIn(true)
//        setAlarmToUpdatePosition()
        startActivity<PortfolioActivity>(bundle)
        finish()
    }

    private fun setAlarmToUpdatePosition() {
//        val calender = Calendar.getInstance()
//        val dayOfWeek = calender.get(Calendar.DAY_OF_WEEK)
//        if( dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
            SmartTradeAlarmManager.scheduleTask()
//        }
    }

}

//https://com.kite.login/?request_token=xa1JmvuUU6FyguvzSow2EOaxkk8iYOWS&action=login&status=success