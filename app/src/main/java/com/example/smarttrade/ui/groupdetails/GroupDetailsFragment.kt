package com.example.smarttrade.ui.groupdetails

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttrade.BR
import com.example.smarttrade.R
import com.example.smarttrade.databinding.FragmentGroupPositionDetailsBinding
import com.example.smarttrade.extension.logI
import com.example.smarttrade.ui.base.BaseFragment
import com.example.smarttrade.ui.base.BaseViewModel
import com.example.smarttrade.util.Constants.GROUP_ID
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class GroupDetailsFragment : BaseFragment<FragmentGroupPositionDetailsBinding, BaseViewModel>() {

    private val groupDetailsViewModel: GroupDetailsViewModel by viewModel()

    override fun getLayoutId(): Int = R.layout.fragment_group_position_details

    override fun getViewModel(): BaseViewModel = groupDetailsViewModel

    override fun getBindingVariable(): Int = BR.portfolioDetailsViewModel

    private val adapter: GroupDetailsAdapter by lazy { GroupDetailsAdapter() }

    private var groupName: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        groupName = arguments?.getString(GROUP_ID)
        setLoader(true)
        initRecycler()
        init()
        setHasOptionsMenu(true)
        groupName?.let { parentActivity.setToolbar(it) }
    }

    private fun initRecycler() {
        viewDataBinding?.recyclerView?.run {
            this as RecyclerView
            adapter = this@GroupDetailsFragment.adapter
        }
    }

    private fun init() {
        groupName?.let { groupName ->
            groupDetailsViewModel.getListOfGroupDetails(groupName).observe(viewLifecycleOwner, {
                logI("Group Details list updated")
                setLoader(false)
                adapter.updateList(it)
            })
        }
    }
}