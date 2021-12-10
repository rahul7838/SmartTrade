package com.example.smarttrade.ui.homescreen

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.smarttrade.BR
import com.example.smarttrade.R
import com.example.smarttrade.databinding.HomeScreenBinding
import com.example.smarttrade.manager.SmartTradeNotificationManager
import com.example.smarttrade.ui.base.BaseFragment
import com.example.smarttrade.util.BANK_NIFTY_INSTRUMENT
import com.example.smarttrade.util.NIFTY_50_INSTRUMENT
import com.example.smarttrade.util.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.time.DayOfWeek
import java.time.LocalDate

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
        setSpinner()
        setClickListener()
        initObserver()
        parentActivity.setToolbar("Uncle Theta")
    }

    private fun setSpinner() {
        viewDataBinding?.instrumentDropDown?.apply {
            adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.list_of_instrument,
                R.layout.drop_down_list_of_instrument_item_view
            ).also {
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if((view as TextView).text == resources.getStringArray(R.array.list_of_instrument)[0]) {
                            viewDataBinding?.lotSize?.text = resources.getString(R.string.lot_size, "50")
                            homeViewModel.instrumentNumber = NIFTY_50_INSTRUMENT
                        } else {
                            viewDataBinding?.lotSize?.text = resources.getString(R.string.lot_size, "25")
                            homeViewModel.instrumentNumber = BANK_NIFTY_INSTRUMENT
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        Toast.makeText(requireContext(), "Not implemented", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        viewDataBinding?.lotSizeDropDown?.apply {
            adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.list_of_lot_size,
                R.layout.drop_down_list_of_instrument_item_view
            ).also {
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        homeViewModel.lotSize = it.getItem(position).toString().toInt()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }
            }
            setSelection(0)
        }
        viewDataBinding?.stopLossDropDown?.apply {
            adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.list_of_percent,
                R.layout.drop_down_list_of_instrument_item_view
            ).also {
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        homeViewModel.stopLossValue = it.getItem(position).toString().toInt()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                }
            }
            when(LocalDate.now().dayOfWeek) {
                DayOfWeek.WEDNESDAY -> {
                    setSelection(3)
                }
                DayOfWeek.THURSDAY -> {
                    setSelection(2)
                }
                else -> setSelection(3)
            }

        }
        viewDataBinding?.skewDropDown?.apply {
            adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.list_of_percent,
                R.layout.drop_down_list_of_instrument_item_view
            ).also {
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        homeViewModel.skewPercent = it.getItem(position).toString().toInt()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                }
            }
            setSelection(2)
        }
    }

    private fun setClickListener() {
        viewDataBinding?.placeOrder?.setOnClickListener {
            homeViewModel.getNifty50()
        }

        viewDataBinding?.cancelOrder?.setOnClickListener {
//            homeViewModel.squareOffOrder()
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.singleShotEventBus
                    .collect {
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
                    }

            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.overallResult.collect {
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
                }
            }
        }
    }
}

interface MyClickListener {
    fun onClick(position: Int)
}