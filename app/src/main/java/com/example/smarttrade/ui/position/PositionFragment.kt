package com.example.smarttrade.ui.position

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttrade.BR
import com.example.smarttrade.R
import com.example.smarttrade.databinding.FragmentPositionBinding
import com.example.smarttrade.ui.base.BaseFragment
import com.example.smarttrade.util.REQUEST_TOKEN
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinApiExtension
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.lang.IllegalStateException

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
    }

    private fun initRecycler() {
        val layout = LinearLayoutManager(requireContext(), VERTICAL, false)
        viewDataBinding?.recyclerView?.run {
            this as RecyclerView
            layoutManager = layout
            adapter = this@PositionFragment.adapter
        }
        adapter.itemLongClickListener = {

        }
        adapter.appCompatDelegate = { createAppCompatDelegate }
    }

    private fun initObserver() {
        positionViewModel.getPosition().observe(viewLifecycleOwner, {
            setLoader(false)
            Timber.d(it.toString())
            adapter.updateList(it)
        })
    }

    private val createAppCompatDelegate: AppCompatDelegate by lazy {
        AppCompatDelegate.create(requireContext(), requireActivity(), null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}