package com.example.smarttrade.ui.portfolio

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttrade.R
import com.example.smarttrade.databinding.PortfolioRecyclerItemBinding
import com.example.smarttrade.util.MultiSelect
import com.zerodhatech.models.Position
import timber.log.Timber

class PortfolioListAdapter : RecyclerView.Adapter<PortfolioListAdapter.PortfolioViewHolder>() {

    private val list: MutableList<Position> = mutableListOf()
    var itemLongClickListener: ((position: Position) -> Unit)? = null

    val multiSelect: MultiSelect by lazy { MultiSelect() }
    lateinit var appCompatDelegate: () -> AppCompatDelegate


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewDataBinding: PortfolioRecyclerItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.portfolio_recycler_item, parent, false)
        return PortfolioViewHolder(viewDataBinding)
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        holder.update(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(newList: List<Position>) {
        val diffCallback = DiffPortfolioList(list, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        list.clear()
        list.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    class DiffPortfolioList(
        private val oldList: List<Position>,
        private val newList: List<Position>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] === newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldPosition = oldList[oldItemPosition]
            val newPosition = newList[newItemPosition]
            return oldPosition.run {
                newPosition.let {
                    averagePrice == it.averagePrice &&
                            buyPrice == it.buyPrice &&
                            buyQuantity == it.buyQuantity &&
                            buyValue == it.buyValue &&
                            buym2m == it.buym2m &&
                            closePrice == it.closePrice &&
                            sellPrice == it.sellPrice &&
                            sellQuantity == it.sellQuantity &&
                            sellValue == it.sellValue &&
                            dayBuyPrice == it.dayBuyPrice &&
                            dayBuyQuantity == it.dayBuyQuantity &&
                            dayBuyValue == it.dayBuyValue &&
                            daySellPrice == it.daySellPrice &&
                            daySellQuantity == it.daySellQuantity &&
                            daySellValue == it.daySellValue &&
                            product == it.product &&
                            exchange == it.exchange &&
                            lastPrice == it.lastPrice &&
                            unrealised == it.unrealised &&
                            m2m == it.m2m &&
                            tradingSymbol == it.tradingSymbol &&
                            netQuantity == it.netQuantity &&
                            netValue == it.netValue
                }
            }
        }

    }


    inner class PortfolioViewHolder(private val viewDataBinding: PortfolioRecyclerItemBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {

        fun update(position: Position) {
            viewDataBinding.run {
                stockName.text = position.tradingSymbol
                totalProfitLossAmount.text = position.pnl.toString()
            }

            if (multiSelect.listOfSelectedItem.contains(position)) {
                viewDataBinding.root.setBackgroundColor(Color.WHITE)
            } else {
                viewDataBinding.root.setBackgroundColor(Color.GRAY)
            }

            onLongClickListener(position)
            onClickListener(position)
        }

        private fun selectItem(item: Position) {
            if(multiSelect.isOn) {
                if(multiSelect.listOfSelectedItem.contains(item)) {
                    multiSelect.listOfSelectedItem.remove(item)
                    viewDataBinding.root.setBackgroundColor(Color.WHITE)
                } else {
                    multiSelect.listOfSelectedItem.add(item)
                    viewDataBinding.root.setBackgroundColor(Color.GRAY)
                }
            }
        }

        private fun onClickListener(position: Position) {
            if(multiSelect.isOn) {
                selectItem(position)
            } else {
                Timber.d("$bindingAdapterPosition is clicked $position ")
            }
        }

        private fun onLongClickListener(position: Position) {
            viewDataBinding.root.setOnLongClickListener {
                if(!multiSelect.isOn) {
                    multiSelect.startActionMode(
                        appCompatDelegate.invoke(), R.menu.portfolio_menu, "Group Position"
                    )
                    selectItem(position)
                }
                true
            }
        }
    }
}