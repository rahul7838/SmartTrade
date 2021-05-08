package com.example.smarttrade.ui.portfolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smarttrade.BR
import com.example.smarttrade.R
import com.example.smarttrade.databinding.FragmentPortfolioBinding
import com.example.smarttrade.ui.base.BaseFragment
import org.koin.android.ext.android.inject

class PortfolioFragment : BaseFragment<FragmentPortfolioBinding, PortfolioViewModel>() {

    private val portfolioViewModel: PortfolioViewModel by inject()

    override fun getLayoutId(): Int = R.layout.fragment_portfolio

    override fun getViewModel(): PortfolioViewModel = portfolioViewModel

    override fun getBindingVariable(): Int = BR.portfolioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}