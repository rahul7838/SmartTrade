package com.example.smarttrade.ui.group

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttrade.BR
import com.example.smarttrade.R
import com.example.smarttrade.databinding.FragmentGroupPositionBinding
import com.example.smarttrade.extension.logI
import com.example.smarttrade.ui.base.BaseFragment
import com.example.smarttrade.ui.bottomsheet.BottomSheetFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class GroupFragment : BaseFragment<FragmentGroupPositionBinding, GroupViewModel>() {

    private val groupViewModel: GroupViewModel by viewModel()

    override fun getLayoutId(): Int = R.layout.fragment_group_position

    override fun getViewModel(): GroupViewModel = groupViewModel

    override fun getBindingVariable(): Int = BR.groupViewModel

    private val adapter: GroupListAdapter by lazy { GroupListAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logI("onViewCreated")
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
            BottomSheetFragment.getInstance(it).show(
                requireActivity().supportFragmentManager,
                BottomSheetFragment::class.java.simpleName
            )
        }
        adapter.onDeleteClickListener = {
            groupViewModel.deleteListOfGroup(it)
        }
    }

    private fun init() {
        groupViewModel.getListOfGroup().observe(viewLifecycleOwner, {
            setLoader(false)
            adapter.updateGroupList(it)
        })
    }
}