package com.example.smarttrade.ui.group

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
import com.example.smarttrade.db.entity.Group
import com.example.smarttrade.extension.gone
import com.example.smarttrade.extension.visible
import timber.log.Timber
import java.text.DecimalFormat
class GroupListAdapter : RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>() {

    private val listOfItem = arrayListOf<Group>()

    var itemClickListener: ((group: Group) -> Unit)? = null

    val listOfSelectedItem: MutableList<Group> = mutableListOf()
    var isActionModeOn = false
    var actionMode: ActionMode? = null
    var onDeleteClickListener: ((listOfGroup: List<Group>) -> Unit)? = null

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            actionMode = mode
            isActionModeOn = true
            val inflater = mode?.menuInflater
            inflater?.inflate(R.menu.group_menu, menu)
//            mode?.title = ""
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.delete -> {
                    val list = arrayListOf<Group>().apply { addAll(listOfSelectedItem) }
                    onDeleteClickListener?.invoke(list)
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

        private val decimalFormat: DecimalFormat by lazy { DecimalFormat("###,###,###,###.##") }
        private val resources: Resources by lazy { binding.root.context.resources }

        fun update(group: Group) {
            binding.recyclerItemVisibilityGrp.gone()
            binding.stockName.text = group.groupName
            binding.pnlValue.text = group.totalPnl.toString()
            if(group.trailingSL != null) {
                binding.stpl.visible()
                binding.stplValue.visible()
                binding.stplValue.text = group.trailingSL.toString()
            } else {
                binding.stplValue.gone()
                binding.stpl.gone()
            }
            if (listOfSelectedItem.contains(group)) {
                binding.mtrlCardId.setCardBackgroundColor(resources.getColor(R.color.light_grey, null))
            } else {
                binding.mtrlCardId.setCardBackgroundColor(resources.getColor(R.color.background, null))
            }

            onLongClickListener(group)
            onClickListener(group)
        }

        private fun onClickListener(group: Group) {
            binding.mtrlCardId.setOnClickListener {
                if (isActionModeOn) {
                    selectItem(group)
                } else {
                    itemClickListener?.invoke(group)
                }
            }
        }

        private fun onLongClickListener(group: Group) {
            binding.mtrlCardId.setOnLongClickListener {
                if (!isActionModeOn) {
                    (it.context as AppCompatActivity).startSupportActionMode(actionModeCallback)
                    selectItem(group)
                }
                true
            }
        }

        private fun selectItem(item: Group) {
            if (isActionModeOn) {
                if (listOfSelectedItem.contains(item)) {
                    listOfSelectedItem.remove(item)
                    binding.mtrlCardId.setCardBackgroundColor(resources.getColor(R.color.white, null))
                    if(listOfSelectedItem.size == 0) {
                        actionMode?.finish()
                    }
                } else {
                    listOfSelectedItem.add(item)
                    binding.mtrlCardId.setCardBackgroundColor(resources.getColor(R.color.light_grey, null))
                }
            }
        }
    }
}

