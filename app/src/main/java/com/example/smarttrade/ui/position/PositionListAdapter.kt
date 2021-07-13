package com.example.smarttrade.ui.position

import android.graphics.Color
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttrade.R
import com.example.smarttrade.databinding.PortfolioRecyclerItemBinding
import com.example.smarttrade.extension.invisible
import com.example.smarttrade.extension.visible
import com.example.smarttrade.repository.LocalPosition
import com.zerodhatech.models.Position
import timber.log.Timber
import java.text.DecimalFormat


class PositionListAdapter : RecyclerView.Adapter<PositionListAdapter.PortfolioViewHolder>() {

    var itemLongClickListener: ((position: Position) -> Unit)? = null
    var itemClickListener: ((localPosition: LocalPosition) -> Unit)? = null

    //    lateinit var appCompatDelegate: () -> AppCompatDelegate
    val listOfSelectedItem: MutableList<LocalPosition> = mutableListOf()
    private val listOfItem: MutableList<LocalPosition> = mutableListOf()
    var isActionModeOn = false

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            isActionModeOn = true
            val inflater = mode?.menuInflater
            inflater?.inflate(R.menu.portfolio_menu_multi_select, menu)
            mode?.title = "Group Item"
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.combine -> {

                }
            }

            mode?.finish()
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            isActionModeOn = false
            listOfSelectedItem.clear()
            notifyDataSetChanged()
            Timber.d("onDestroyActionMode")
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        Timber.d("onCreateViewHolder")
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewDataBinding: PortfolioRecyclerItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.portfolio_recycler_item, parent, false)
        return PortfolioViewHolder(viewDataBinding)
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        Timber.d("onBindViewHolder")
        holder.update(listOfItem[position])
    }

    override fun getItemCount(): Int {
        return listOfItem.size
    }

    fun updateList(newList: List<LocalPosition>) {
        listOfItem.clear()
        listOfItem.addAll(newList)
        notifyDataSetChanged()
    }

    //region
    inner class PortfolioViewHolder(private val viewDataBinding: PortfolioRecyclerItemBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {

        val decimalFormat = DecimalFormat("##,##,###.##")

        fun update(position: LocalPosition) {
            viewDataBinding.run {
                stockName.text = position.tradingSymbol
                quantityValue.text = position.netQuantity.toString()
                averageValue.text = position.averagePrice.toString()
                investedValue.text = position.value.toString()
                lastTradingPriceValue.text = position.lastPrice.toString()
                if(position.stopLossPrice != null) {
                    stpl.visible()
                    stplValue.visible()
                    stplValue.text = decimalFormat.format(position.stopLossPrice)
                } else {
                    stpl.invisible()
                    stplValue.invisible()
                }
            }

            if (listOfSelectedItem.contains(position)) {
                viewDataBinding.root.setBackgroundColor(Color.GRAY)
            } else {
                viewDataBinding.root.setBackgroundColor(Color.WHITE)
            }

            onLongClickListener(position)
            onClickListener(position)
        }

        private fun selectItem(item: LocalPosition) {
            if (isActionModeOn) {
                if (listOfSelectedItem.contains(item)) {
                    listOfSelectedItem.remove(item)
                    viewDataBinding.root.setBackgroundColor(Color.WHITE)
                } else {
                    listOfSelectedItem.add(item)
                    viewDataBinding.root.setBackgroundColor(Color.GRAY)
                }
            }
        }

        private fun onClickListener(position: LocalPosition) {
            viewDataBinding.root.setOnClickListener {
                if (isActionModeOn) {
                    selectItem(position)
                } else {
                    itemClickListener?.invoke(position)
                }
            }
        }

        private fun onLongClickListener(position: LocalPosition) {
            viewDataBinding.root.setOnLongClickListener {
                if (!isActionModeOn) {
                    (it.context as AppCompatActivity).startSupportActionMode(actionModeCallback)
                    selectItem(position)
                }
                true
            }
        }
    }
    //endregion
}