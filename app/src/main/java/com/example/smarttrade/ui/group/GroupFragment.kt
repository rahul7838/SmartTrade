package com.example.smarttrade.ui.group

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttrade.BR
import com.example.smarttrade.R
import com.example.smarttrade.databinding.FragmentGroupPositionBinding
import com.example.smarttrade.ui.base.BaseFragment
import com.example.smarttrade.ui.position.PositionBottomSheetFragment
import com.example.smarttrade.util.Constants.GROUP_ID
import org.koin.android.ext.android.inject

class GroupFragment : BaseFragment<FragmentGroupPositionBinding, GroupViewModel>() {

    private val groupViewModel: GroupViewModel by inject()

    override fun getLayoutId(): Int = R.layout.fragment_group_position

    override fun getViewModel(): GroupViewModel = groupViewModel

    override fun getBindingVariable(): Int = BR.groupViewModel

    private val adapter: GroupListAdapter by lazy { GroupListAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLoader(true)
        initRecycler()
        init()
        setHasOptionsMenu(true)
        parentActivity.setToolbar(getString(R.string.group))
    }

    private fun initRecycler() {
        viewDataBinding?.recyclerView?.run {
            this as RecyclerView
            adapter = this@GroupFragment.adapter
        }
        adapter.itemClickListener = {
            val bundle = bundleOf(GROUP_ID to it.groupName)
            findNavController().navigate(R.id.groupPositionDetailsFragment, bundle)
        }
    }

    private fun init() {
        groupViewModel.getListOfGroup().observe(viewLifecycleOwner, {
            setLoader(false)
            adapter.updateGroupList(it)
        })
    }
}