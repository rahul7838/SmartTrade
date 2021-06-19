package com.example.smarttrade.ui.position

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttrade.BR
import com.example.smarttrade.R
import com.example.smarttrade.databinding.FragmentPositionBinding
import com.example.smarttrade.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinApiExtension
import timber.log.Timber

@KoinApiExtension
class PositionFragment : BaseFragment<FragmentPositionBinding, PositionViewModel>() {

    private val positionViewModel: PositionViewModel by sharedViewModel()

    override fun getViewModel(): PositionViewModel = positionViewModel

    override fun getLayoutId(): Int = R.layout.fragment_position

    override fun getBindingVariable(): Int = BR.portfolioViewModel

    private val adapter: PositionListAdapter by lazy { PositionListAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLoader(true)
        initRecycler()
        initObserver()
        parentActivity.setToolbar("Positions")
    }

    private fun initRecycler() {
        val layout = LinearLayoutManager(requireContext(), VERTICAL, false)
        viewDataBinding?.recyclerView?.run {
            this as RecyclerView
            layoutManager = layout
            adapter = this@PositionFragment.adapter
        }
        adapter.itemClickListener = {
            PositionBottomSheetFragment.getInstance(it).show(
                requireActivity().supportFragmentManager,
                PositionBottomSheetFragment::class.java.simpleName
            )
        }
    }

    private fun initObserver() {
        positionViewModel.getPosition().observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                setLoader(false)
                Timber.d(it.toString())
                adapter.updateList(it)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}