package com.example.smarttrade.ui.login

import com.example.smarttrade.R
import com.example.smarttrade.databinding.ActivityMainBinding
import com.example.smarttrade.ui.base.BaseActivity
import com.example.smarttrade.ui.base.BaseViewModel
import org.koin.android.ext.android.inject

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val loginViewModel: LoginViewModel by inject()

    override fun getViewModel(): BaseViewModel = loginViewModel

    override var layoutId: Int = R.layout.activity_main
}