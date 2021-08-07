package com.example.smarttrade.ui.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttrade.R
import com.example.smarttrade.databinding.PortfolioRecyclerItemBinding
import com.example.smarttrade.db.entity.Group
import com.example.smarttrade.extension.gone
import com.example.smarttrade.extension.invisible

class GroupListAdapter : RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>() {

    private val listOfItem = arrayListOf<Group>()

    var itemClickListener: ((group: Group) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<PortfolioRecyclerItemBinding>(inflator, R.layout.portfolio_recycler_item, parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.update(listOfItem[position])
    }

    override fun getItemCount(): Int = listOfItem.size

    fun updateGroupList(list: List<Group>) {
        listOfItem.clear()
        listOfItem.addAll(list)
        notifyDataSetChanged()
    }


    inner class GroupViewHolder(val binding: PortfolioRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun update(group: Group) {
            binding.recyclerItemVisibilityGrp.gone()
            binding.stockName.text = group.groupName
            binding.pnlValue.text = group.totalPnl.toString()
            if(group.stopLossAmount != null) {
                binding.stplValue.text = group.stopLossAmount.toString()
            } else {
//                binding.stpl.gone()
            }
            onClickListener(group)
        }

        private fun onClickListener(group: Group) {
            binding.mtrlCardId.setOnClickListener {
                itemClickListener?.invoke(group)
            }
        }
    }
}