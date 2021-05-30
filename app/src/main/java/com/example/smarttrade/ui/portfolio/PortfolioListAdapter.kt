package com.example.smarttrade.ui.portfolio

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttrade.R
import com.example.smarttrade.databinding.PortfolioRecyclerItemBinding
import com.example.smarttrade.util.MultiSelect
import com.zerodhatech.models.Position
import timber.log.Timber

class PortfolioListAdapter : MultiSelect<PortfolioListAdapter.PortfolioViewHolder>() {

    var itemLongClickListener: ((position: Position) -> Unit)? = null
    lateinit var appCompatDelegate: () -> AppCompatDelegate

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        Timber.d("onCreateViewHolder")
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewDataBinding: PortfolioRecyclerItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.portfolio_recycler_item, parent, false)
        return PortfolioViewHolder(viewDataBinding)
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        Timber.d("onBindViewHolder")
        holder.update(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    //region
    inner class PortfolioViewHolder(private val viewDataBinding: PortfolioRecyclerItemBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {

        fun update(position: Position) {
            viewDataBinding.run {
                stockName.text = position.tradingSymbol
                totalProfitLossAmount.text = position.pnl.toString()
            }

            if (listOfSelectedItem.contains(position)) {
                viewDataBinding.root.setBackgroundColor(Color.GRAY)
            } else {
                viewDataBinding.root.setBackgroundColor(Color.WHITE)
            }

            onLongClickListener(position)
            onClickListener(position)
        }

        private fun selectItem(item: Position) {
            if (isOn) {
                if (listOfSelectedItem.contains(item)) {
                    listOfSelectedItem.remove(item)
                    viewDataBinding.root.setBackgroundColor(Color.WHITE)
                } else {
                    listOfSelectedItem.add(item)
                    viewDataBinding.root.setBackgroundColor(Color.GRAY)
                }
            }
        }

        private fun onClickListener(position: Position) {
            viewDataBinding.root.setOnClickListener {
                if (isOn) {
                    selectItem(position)
                } else {
                    Timber.d("$bindingAdapterPosition is clicked $position ")
                }
            }
        }

        private fun onLongClickListener(position: Position) {
            viewDataBinding.root.setOnLongClickListener {
                if (!isOn) {
                    startActionMode(
                        appCompatDelegate.invoke(), R.menu.portfolio_menu, "Group Position"
                    )
                    selectItem(position)
                }
                true
            }
        }
    }
    //endregion
}