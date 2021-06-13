package com.example.smarttrade.ui.position

import android.graphics.Color
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.ActionMode
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttrade.R
import com.example.smarttrade.databinding.PortfolioRecyclerItemBinding
import com.example.smarttrade.repository.LocalPosition
import com.zerodhatech.models.Position
import timber.log.Timber


class PositionListAdapter : RecyclerView.Adapter<PositionListAdapter.PortfolioViewHolder>() {

    var itemLongClickListener: ((position: Position) -> Unit)? = null
    lateinit var appCompatDelegate: () -> AppCompatDelegate
    val listOfSelectedItem: MutableList<LocalPosition> = mutableListOf()
    private val list: MutableList<LocalPosition> = mutableListOf()
    var isOn = false

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            isOn = true
            val inflater = mode?.menuInflater
            inflater?.inflate(R.menu.portfolio_menu, menu)
            mode?.title = "Group Item"
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.combine -> {
//                onActionClickListener?.invoke()
                    Timber.d("total list item: ${list.size} ")
                    Timber.d("new list item: ${listOfSelectedItem.size} ")
                    var heading: String = ""
                    listOfSelectedItem.forEach {
                        heading += (it.tradingSymbol + " ")
                        list.remove(it)
                    }
//                    val position = LocalPosition().apply {
//                        tradingSymbol = heading
//                    }
//                    list.add(position)
                    Timber.d("after total list item: ${list.size} ")
                    Timber.d("after new list item: ${listOfSelectedItem.size} ")
                    Timber.d("onActionItemClicked combine")
                }
            }

            mode?.finish()
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            isOn = false
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
        holder.update(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(newList: List<LocalPosition>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    //region
    inner class PortfolioViewHolder(private val viewDataBinding: PortfolioRecyclerItemBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {

        fun update(position: LocalPosition) {
            viewDataBinding.run {
                stockName.text = position.tradingSymbol
//                totalProfitLossAmount.text = position.pnl?.toString()
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

        private fun onClickListener(position: LocalPosition) {
            viewDataBinding.root.setOnClickListener {
                if (isOn) {
                    selectItem(position)
                } else {
                    if(position.tradingSymbol?.split(" ")?.size!! >= 2 ) {

                    }
                }
            }
        }

        private fun onLongClickListener(position: LocalPosition) {
            viewDataBinding.root.setOnLongClickListener {
                if (!isOn) {
                    (it.context as AppCompatActivity).startSupportActionMode(
                        actionModeCallback
                    )
                    selectItem(position)
                }
                true
            }
        }
    }
    //endregion
}