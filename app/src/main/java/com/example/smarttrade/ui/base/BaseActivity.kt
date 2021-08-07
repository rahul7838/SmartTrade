package com.example.smarttrade.ui.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.smarttrade.R
import com.example.smarttrade.databinding.ActivityBaseBinding
import com.example.smarttrade.extension.gone
import com.example.smarttrade.extension.logI
import com.example.smarttrade.extension.visible
import org.koin.core.component.KoinApiExtension
import timber.log.Timber

@KoinApiExtension
abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity() {

    private var baseViewModel: BaseViewModel? = null

    abstract fun getViewModel(): BaseViewModel

    var viewBinding: VB? = null

    abstract var layoutId: Int

    var activityBaseBinding: ActivityBaseBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        activityBaseBinding = DataBindingUtil.setContentView(this, R.layout.activity_base)
        activityBaseBinding?.executePendingBindings()
        activityBaseBinding?.lifecycleOwner = this
        viewBinding = DataBindingUtil.inflate(
            layoutInflater,
            layoutId,
            activityBaseBinding?.layoutContainer,
            false
        )
        activityBaseBinding?.layoutContainer?.addView(viewBinding?.root)
        baseViewModel = getViewModel()
        initObserver()
    }

    private fun initObserver() {
        baseViewModel?.showProgressBar?.observe(this, { isShowProgress ->
            setLoader(isShowProgress)
        })
    }

    fun setLoader(isShowProgress: Boolean) {
        activityBaseBinding?.frameLayout?.let {
            logI("progress bar: $isShowProgress")
            if (isShowProgress) it.visible() else it.gone()
        }
    }

    fun setToolbar(toolbarTitle: String, isVisible: Boolean = true) {
        when {
            isVisible -> {
                setSupportActionBar(activityBaseBinding?.toolbar)
                supportActionBar?.elevation = 2f
                activityBaseBinding?.toolbar?.txtTitle?.text = toolbarTitle
//                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
            else -> {logI("isVisible:$isVisible")}
        }
    }
}