package com.example.smarttrade

import android.app.Application
import androidx.databinding.DataBindingUtil
import com.example.smarttrade.di.applicationModule
import com.example.smarttrade.ui.AppBindingAdapter
import com.example.smarttrade.ui.AppBindingComponent
import com.onesignal.OneSignal
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class SmartTradeApplication : Application() {
    //    lateinit var firebaseAnalytics: FirebaseAnalytics
    private val ONESIGNAL_APP_ID: String = "b96dd563-ce83-4c52-89a2-99e10127d8a2"

    override fun onCreate() {
        super.onCreate()
        // Obtain the FirebaseAnalytics instance.
//        firebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext)

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        startKoin {
//            if (BuildConfig.DEBUG) logger(AndroidLogger(Level.DEBUG)) need to update to latest version of koin
            androidContext(this@SmartTradeApplication)
            modules(modules = applicationModule)
        }

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

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