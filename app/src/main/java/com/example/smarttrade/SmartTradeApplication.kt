package com.example.smarttrade

import android.app.Application
import androidx.databinding.DataBindingUtil
import com.example.smarttrade.di.applicationModule
import com.example.smarttrade.ui.AppBindingAdapter
import com.example.smarttrade.ui.AppBindingComponent
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class SmartTradeApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SmartTradeApplication)
            modules(modules = applicationModule)
        }

        Timber.plant(Timber.DebugTree())

        DataBindingUtil.setDefaultComponent(object : AppBindingComponent {
            override fun getAppBindingAdapter(): AppBindingAdapter {
                return AppBindingAdapter()
            }

        })
    }
}