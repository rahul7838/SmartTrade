package com.example.smarttrade.ui.group

import com.example.smarttrade.BR
import com.example.smarttrade.R
import com.example.smarttrade.databinding.FragmentGroupPositionBinding
import com.example.smarttrade.ui.base.BaseFragment
import org.koin.android.ext.android.inject

class GroupFragment : BaseFragment<FragmentGroupPositionBinding, GroupViewModel>() {

    private val groupViewModel: GroupViewModel by inject()

    override fun getLayoutId(): Int = R.layout.fragment_group_position

    override fun getViewModel(): GroupViewModel = groupViewModel

    override fun getBindingVariable(): Int = BR.groupViewModel


}