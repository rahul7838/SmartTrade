package com.example.smarttrade.ui.groupdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttrade.R
import com.example.smarttrade.databinding.PortfolioRecyclerItemBinding
import com.zerodhatech.models.Position
import timber.log.Timber

class PortfolioDetailsAdapter : RecyclerView.Adapter<PortfolioDetailsAdapter.PortfolioDetailsViewHolder>() {

    private val list: MutableList<Position> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioDetailsViewHolder {
        Timber.d("onCreateViewHolder")
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewDataBinding: PortfolioRecyclerItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.portfolio_recycler_item, parent, false)
        return PortfolioDetailsViewHolder(viewDataBinding)
    }

    override fun onBindViewHolder(holder: PortfolioDetailsViewHolder, position: Int) {
        Timber.d("onBindViewHolder")
        holder.update(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(newList: List<Position>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    inner class PortfolioDetailsViewHolder(viewDataBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewDataBinding.root) {

        fun update(position: Position) {

        }
    }

}