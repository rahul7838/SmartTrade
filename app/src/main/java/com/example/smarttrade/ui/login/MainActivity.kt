package com.example.smarttrade.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.example.smarttrade.KiteConnect
import com.example.smarttrade.R
import com.example.smarttrade.databinding.ActivityMainBinding
import com.example.smarttrade.extension.startActivity
import com.example.smarttrade.ui.base.BaseActivity
import com.example.smarttrade.ui.base.BaseViewModel
import com.example.smarttrade.ui.position.PortfolioActivity
import com.example.smarttrade.util.REQUEST_TOKEN
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension
import timber.log.Timber

@KoinApiExtension
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val loginViewModel: LoginViewModel by viewModel()

    override fun getViewModel(): BaseViewModel = loginViewModel

    override var layoutId: Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(activityBaseBinding?.toolbar)
        setActionBarTitle("Rahul")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Timber.d("executed")
        val requestToken = intent?.data?.getQueryParameter("request_token")
        val bundle = bundleOf(REQUEST_TOKEN to requestToken)
        startActivity<PortfolioActivity>(bundle)
    }

    private fun setActionBarTitle(title: String) {
        actionBar?.title = title
    }
}

//https://com.kite.login/?request_token=xa1JmvuUU6FyguvzSow2EOaxkk8iYOWS&action=login&status=success