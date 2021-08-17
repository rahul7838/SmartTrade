package com.example.smarttrade

import android.app.Application
import androidx.databinding.DataBindingUtil
import com.example.smarttrade.di.applicationModule
import com.example.smarttrade.ui.AppBindingAdapter
import com.example.smarttrade.ui.AppBindingComponent
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class SmartTradeApplication : Application() {
//    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()
        // Obtain the FirebaseAnalytics instance.
//        firebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext)

        startKoin {
            if(BuildConfig.DEBUG) logger(AndroidLogger(Level.DEBUG))
            androidContext(this@SmartTradeApplication)
            modules(modules = applicationModule)
        }

        if(BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        DataBindingUtil.setDefaultComponent(object : AppBindingComponent {
            override fun getAppBindingAdapter(): AppBindingAdapter {
                return AppBindingAdapter()
            }

        })
    }

    companion object {
        private var instance: SmartTradeApplication? = null

        fun getInstance() = instance ?: SmartTradeApplication().also { instance = it }
    }
}