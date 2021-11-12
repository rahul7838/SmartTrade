package com.example.smarttrade.ui.homescreen

import android.os.Bundle
import android.view.View
import com.example.smarttrade.BR
import com.example.smarttrade.R
import com.example.smarttrade.databinding.HomeScreenBinding
import com.example.smarttrade.ui.base.BaseFragment
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
        parentActivity.setToolbar("Uncle Theta")
    }

    private fun setClickListener() {
        viewDataBinding?.placeOrder?.setOnClickListener {
            homeViewModel.getNifty50()
        }

        viewDataBinding?.cancelOrder?.setOnClickListener {
            homeViewModel.squareOffOrder()
        }
    }

    private fun init() {

    }
}