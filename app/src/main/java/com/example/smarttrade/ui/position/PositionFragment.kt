package com.example.smarttrade.ui.position

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.smarttrade.BR
import com.example.smarttrade.R
import com.example.smarttrade.databinding.FragmentPositionBinding
import com.example.smarttrade.extension.getTimeAgo2
import com.example.smarttrade.extension.logI
import com.example.smarttrade.services.PositionUpdateService
import com.example.smarttrade.ui.base.BaseFragment
import com.example.smarttrade.ui.bottomsheet.BottomSheetFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class PositionFragment : BaseFragment<FragmentPositionBinding, PositionViewModel>(), SwipeRefreshLayout.OnRefreshListener {

    private val positionViewModel: PositionViewModel by sharedViewModel()

    override fun getViewModel(): PositionViewModel = positionViewModel

    override fun getLayoutId(): Int = R.layout.fragment_position

    override fun getBindingVariable(): Int = BR.portfolioViewModel

    private val adapter: PositionListAdapter by lazy { PositionListAdapter() }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logI("onViewCreated")
        viewDataBinding?.swipeToRefresh?.setOnRefreshListener(this)
        setLoader(true)
        initRecycler()
        initObserver()
        setHasOptionsMenu(true)
        parentActivity.setToolbar(getString(R.string.positions))
    }

    private fun initRecycler() {
        val layout = LinearLayoutManager(requireContext(), VERTICAL, false)
        viewDataBinding?.recyclerView?.run {
            this as RecyclerView
            layoutManager = layout
            adapter = this@PositionFragment.adapter
        }
        adapter.itemClickListener = {
            BottomSheetFragment.getInstance(it).show(
                requireActivity().supportFragmentManager,
                BottomSheetFragment::class.java.simpleName
            )
        }
        adapter.onCombineClickListener = {
            positionViewModel.updateGroup(it)
        }
    }

    private fun initObserver() {
        positionViewModel.getPosition().observe(viewLifecycleOwner, {
            viewDataBinding?.swipeToRefresh?.isRefreshing = false
            if (it.isNotEmpty()) {
                setLoader(false)
                logI(it.toString())
                adapter.updateList(it)
            }
        })
        logI("first")
        lifecycle.coroutineScope.launch {
            logI("delay-1")
            while (true) {
                positionViewModel.getTime()?.run {
                    val a = getTimeAgo2(this)
                    parentActivity.activityBaseBinding?.toolbar?.timer?.text = a
                    logI("delay0")
                }
                logI("delay1")
                delay(10000)
                logI("delay2")
            }
        }
        logI("second")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.position_menu1, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when(item.itemId) {
            R.id.update_position -> {
                logI("start position update")
                val intent = Intent(requireContext(), PositionUpdateService::class.java)
                ContextCompat.startForegroundService(requireContext(), intent)
            }
            R.id.stop_update -> {
                logI("stop position update")
                val intent = Intent(requireContext(), PositionUpdateService::class.java)
                requireContext().stopService(intent)
            }
        }
        return true
    }

    override fun onPause() {
        logI("onPause")
        super.onPause()
    }

    override fun onStop() {
        logI("onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        logI("onDestroy")
        super.onDestroyView()
    }

    override fun onRefresh() {
        logI("refresh")
        positionViewModel.refreshPosition()
    }
}