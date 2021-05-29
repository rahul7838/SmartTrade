package com.example.smarttrade.ui.portfolio

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.example.smarttrade.BR
import com.example.smarttrade.R
import com.example.smarttrade.databinding.FragmentPortfolioBinding
import com.example.smarttrade.ui.base.BaseFragment
import com.example.smarttrade.util.REQUEST_TOKEN
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension
import timber.log.Timber

@KoinApiExtension
class PortfolioFragment : BaseFragment<FragmentPortfolioBinding, PortfolioViewModel>() {

    private val portfolioViewModel: PortfolioViewModel by inject()

    override fun getLayoutId(): Int = R.layout.fragment_portfolio

    override fun getViewModel(): PortfolioViewModel = portfolioViewModel

    override fun getBindingVariable(): Int = BR.portfolioViewModel

    private val adapter: PortfolioListAdapter by lazy { PortfolioListAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLoader(true)
        val requestToken = arguments?.get(REQUEST_TOKEN)
        Timber.d(requestToken.toString())
        initRecycler()
        portfolioViewModel.createSession(requestToken.toString())
        initObserver()
    }

    private fun initRecycler() {
        val layout = LinearLayoutManager(requireContext(), VERTICAL, false)
        viewDataBinding?.positionRecycler?.run {
            layoutManager = layout
            adapter = this@PortfolioFragment.adapter
        }
        adapter.itemLongClickListener = {
//            (requireActivity() as Activity).startActionMode()
        }
        adapter.appCompatDelegate = { createAppCompatDelegate() }
    }

    private fun initObserver() {
        portfolioViewModel.listOfPositionLiveData.observe(viewLifecycleOwner, {
            setLoader(false)
            Timber.d(it.toString())
            adapter.updateList(it)
        })
//        AppCompatActivity.
    }

    private fun createAppCompatDelegate(): AppCompatDelegate {
        return AppCompatDelegate.create(requireContext(), requireActivity(), null)
    }



}