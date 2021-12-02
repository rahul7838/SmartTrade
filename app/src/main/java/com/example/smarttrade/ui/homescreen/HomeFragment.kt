package com.example.smarttrade.ui.homescreen

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.example.smarttrade.BR
import com.example.smarttrade.R
import com.example.smarttrade.databinding.HomeScreenBinding
import com.example.smarttrade.manager.SmartTradeNotificationManager
import com.example.smarttrade.ui.base.BaseFragment
import com.example.smarttrade.util.Resource
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeFragment : BaseFragment<HomeScreenBinding, HomeViewModel>() {

    private val homeViewModel: HomeViewModel by sharedViewModel()

    override fun getLayoutId(): Int {
        return R.layout.home_screen
    }

    override fun getViewModel(): HomeViewModel {
        return homeViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.homeViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
        initObserver()
        parentActivity.setToolbar("Uncle Theta")
    }

    private fun setClickListener() {
        viewDataBinding?.placeOrder?.setOnClickListener {
//            if (!homeViewModel.inProgress) {
//                homeViewModel.inProgress = (true)
            homeViewModel.getNifty50()
//            }
        }

        viewDataBinding?.cancelOrder?.setOnClickListener {
//            homeViewModel.squareOffOrder()
        }
    }

    private fun initObserver() {
        homeViewModel.peResult.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    SmartTradeNotificationManager.buildTradeExecuteNotification(
                        it.data!!.price, it.data.tradeName, it.data.isBuyOrSell
                    )
                }
                is Resource.Error -> {
                    SmartTradeNotificationManager.buildTradeFailureNotification(it.message)
                }
                is Resource.Loading -> Unit
            }
        })

        homeViewModel.ceResult.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    SmartTradeNotificationManager.buildTradeExecuteNotification(
                        it.data!!.price, it.data.tradeName, it.data.isBuyOrSell
                    )
                }
                is Resource.Error -> {
                    SmartTradeNotificationManager.buildTradeFailureNotification(it.message)
                }
                is Resource.Loading -> Unit
            }
        })

        homeViewModel.overallResult.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    viewDataBinding?.progressBar?.isVisible = false
                }
                is Resource.Error -> {
                    SmartTradeNotificationManager.buildTradeFailureNotification(it.message)
                    viewDataBinding?.progressBar?.isVisible = false
                }
                is Resource.Loading -> {
                    viewDataBinding?.progressBar?.isVisible = true
                }
            }
        })
    }
}