package com.example.smarttrade.ui.groupdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttrade.R
import com.example.smarttrade.databinding.PortfolioRecyclerItemBinding
import com.example.smarttrade.db.entity.BottomSheetDataObject
import com.example.smarttrade.db.entity.Position
import com.example.smarttrade.extension.gone
import timber.log.Timber
import java.text.DecimalFormat

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

    fun updateList(newList: BottomSheetDataObject.GroupDetails) {
        list.clear()
        list.addAll(newList.listOfPosition)
        notifyDataSetChanged()
    }

    inner class GroupDetailsViewHolder(private val binding: PortfolioRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val decimalFormat: DecimalFormat by lazy { DecimalFormat("###,###,###,###.##") }

        fun update(position: Position) {
            binding.run {
                position.let { position ->
                    stockName.text = position.tradingSymbol
                    quantityValue.text = position.netQuantity.toString()
                    averageValue.text = position.averagePrice.toString()
                    investedValue.text = position.value.toString()
                    lastTradingPriceValue.text = position.lastPrice.toString()
                    pnlValue.text = decimalFormat.format(position.pnl)
                    stpl.gone()
                }
            }

        }
    }

}