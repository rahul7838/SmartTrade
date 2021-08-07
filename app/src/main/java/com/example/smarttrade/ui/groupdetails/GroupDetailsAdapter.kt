package com.example.smarttrade.ui.groupdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttrade.R
import com.example.smarttrade.databinding.PortfolioRecyclerItemBinding
import com.example.smarttrade.db.entity.GroupDetails
import com.example.smarttrade.db.entity.Position
import com.example.smarttrade.extension.invisible
import com.example.smarttrade.extension.visible
import timber.log.Timber

class GroupDetailsAdapter : RecyclerView.Adapter<GroupDetailsAdapter.GroupDetailsViewHolder>() {

    private val list: MutableList<Position> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupDetailsViewHolder {
        Timber.d("onCreateViewHolder")
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewDataBinding: PortfolioRecyclerItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.portfolio_recycler_item, parent, false)
        return GroupDetailsViewHolder(viewDataBinding)
    }

    override fun onBindViewHolder(holder: GroupDetailsViewHolder, position: Int) {
        Timber.d("onBindViewHolder")
        holder.update(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(newList: GroupDetails) {
        list.clear()
        list.addAll(newList.listOfPosition)
        notifyDataSetChanged()
    }

    inner class GroupDetailsViewHolder(private val binding: PortfolioRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun update(position: Position) {
            binding.run {
                position.let { position ->
                    stockName.text = position.tradingSymbol
                    quantityValue.text = position.netQuantity.toString()
                    averageValue.text = position.averagePrice.toString()
                    investedValue.text = position.value.toString()
                    lastTradingPriceValue.text = position.lastPrice.toString()
                }
            }

        }
    }

}