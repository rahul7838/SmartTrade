package com.example.smarttrade.ui.groupdetails

import android.os.Bundle
import android.view.View
import com.example.smarttrade.BR
import com.example.smarttrade.R
import com.example.smarttrade.databinding.FragmentGroupPositionDetailsBinding
import com.example.smarttrade.ui.base.BaseFragment
import com.example.smarttrade.ui.base.BaseViewModel
import org.koin.android.ext.android.inject

class GroupPositionDetailsFragment : BaseFragment<FragmentGroupPositionDetailsBinding, BaseViewModel>() {

    private val groupPositionDetailsViewModel: GroupPositionDetailsViewModel by inject()

    override fun getLayoutId(): Int = R.layout.fragment_group_position_details

    override fun getViewModel(): BaseViewModel = groupPositionDetailsViewModel

    override fun getBindingVariable(): Int = BR.portfolioDetailsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}