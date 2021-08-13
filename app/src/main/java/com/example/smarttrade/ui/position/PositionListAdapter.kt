package com.example.smarttrade.ui.position

import android.content.res.Resources
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
import com.example.smarttrade.db.entity.BottomSheetDataObject
import com.example.smarttrade.extension.appendCurrency
import com.example.smarttrade.extension.invisible
import com.example.smarttrade.extension.visible
import com.zerodhatech.models.Position
import timber.log.Timber
import java.text.DecimalFormat


class PositionListAdapter : RecyclerView.Adapter<PositionListAdapter.PortfolioViewHolder>() {

    var itemLongClickListener: ((position: Position) -> Unit)? = null
    var itemClickListener: ((positionWithStopLoss: BottomSheetDataObject.PositionWithStopLoss) -> Unit)? = null
    var onGroupClickListener: ((listOfPositionWithStopLoss: List<BottomSheetDataObject.PositionWithStopLoss>) -> Unit)? = null

    //    lateinit var appCompatDelegate: () -> AppCompatDelegate
    val listOfSelectedItem: MutableList<BottomSheetDataObject.PositionWithStopLoss> = mutableListOf()
    private val listOfItem: MutableList<BottomSheetDataObject.PositionWithStopLoss> = mutableListOf()
    var isActionModeOn = false
    var actionMode: ActionMode? = null

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            actionMode = mode
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
                    val list = arrayListOf<BottomSheetDataObject.PositionWithStopLoss>().apply { addAll(listOfSelectedItem) }
                    onGroupClickListener?.invoke(list)
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

    fun updateList(newList: List<BottomSheetDataObject.PositionWithStopLoss>) {
        listOfItem.clear()
        listOfItem.addAll(newList)
        notifyDataSetChanged()
    }

    //region
    inner class PortfolioViewHolder(private val viewDataBinding: PortfolioRecyclerItemBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {

        private val decimalFormat: DecimalFormat by lazy { DecimalFormat("###,###,###,###.##") }
        private val resources: Resources by lazy { viewDataBinding.root.context.resources }

        fun update(positionWithStopLoss: BottomSheetDataObject.PositionWithStopLoss) {
            viewDataBinding.run {
                stockName.text = positionWithStopLoss.position.tradingSymbol
                quantityValue.text = positionWithStopLoss.position.netQuantity.toString()
                averageValue.text = positionWithStopLoss.position.averagePrice.toString()
                investedValue.text = positionWithStopLoss.position.value.toString()
                lastTradingPriceValue.text = positionWithStopLoss.position.lastPrice.toString()
                pnlValue.text = decimalFormat.format(positionWithStopLoss.position.pnl)
                val stopLossInAmount = positionWithStopLoss.stopLoss?.stopLossInAmount
                val stopLossPrice = positionWithStopLoss.stopLoss?.stopLossPrice
                when {
                    stopLossInAmount != null -> {
                        stpl.visible()
                        stplValue.visible()
                        stplValue.text = decimalFormat.format(stopLossInAmount).appendCurrency()
                    }
                    stopLossPrice != null -> {
                        stpl.visible()
                        stplValue.visible()
                        stplValue.text = decimalFormat.format(stopLossPrice)
                    }
                    else -> {
                        stpl.invisible()
                        stplValue.invisible()
                    }
                }
            }

            if (listOfSelectedItem.contains(positionWithStopLoss)) {
                viewDataBinding.mtrlCardId.setCardBackgroundColor(resources.getColor(R.color.light_grey, null))
            } else {
                viewDataBinding.mtrlCardId.setCardBackgroundColor(resources.getColor(R.color.white, null))
            }

            onLongClickListener(positionWithStopLoss)
            onClickListener(positionWithStopLoss)
        }

        private fun selectItem(item: BottomSheetDataObject.PositionWithStopLoss) {
            if (isActionModeOn) {
                if (listOfSelectedItem.contains(item)) {
                    listOfSelectedItem.remove(item)
                    viewDataBinding.mtrlCardId.setCardBackgroundColor(resources.getColor(R.color.white, null))
                    if(listOfSelectedItem.size == 0) {
                        actionMode?.finish()
                    }
                } else {
                    listOfSelectedItem.add(item)
                    viewDataBinding.mtrlCardId.setCardBackgroundColor(resources.getColor(R.color.light_grey, null))
                }
            }
        }

        private fun onClickListener(positionWithStopLoss: BottomSheetDataObject.PositionWithStopLoss) {
            viewDataBinding.mtrlCardId.setOnClickListener {
                if (isActionModeOn) {
                    selectItem(positionWithStopLoss)
                } else {
                    itemClickListener?.invoke(positionWithStopLoss)
                }
            }
        }

        private fun onLongClickListener(positionWithStopLoss: BottomSheetDataObject.PositionWithStopLoss) {
            viewDataBinding.mtrlCardId.setOnLongClickListener {
                if (!isActionModeOn) {
                    (it.context as AppCompatActivity).startSupportActionMode(actionModeCallback)
                    selectItem(positionWithStopLoss)
                }
                true
            }
        }
    }
    //endregion
}