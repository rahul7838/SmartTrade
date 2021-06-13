package com.example.smarttrade.util

import android.view.Menu
import android.view.MenuItem
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttrade.R
import com.zerodhatech.models.Position
import timber.log.Timber

abstract class MultiSelect<VH : RecyclerView.ViewHolder> : ActionMode.Callback,
    RecyclerView.Adapter<VH>() {

    @MenuRes
    var menuResId: Int = 0
    var title: String? = null
    var isOn = false
    val listOfSelectedItem: MutableList<Position> = mutableListOf()

    protected val list: MutableList<Position> = mutableListOf()

    val onActionClickListener: (() -> Unit)? = null

    private var mode: ActionMode? = null

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        this.mode = mode
        isOn = true
        val inflater = mode?.menuInflater
        inflater?.inflate(menuResId, menu)
        mode?.title = title
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
                val heading = ""
                listOfSelectedItem.forEach {
                    heading.plus(it.tradingSymbol + " ")
                    list.remove(it)
                }
                val position = Position().apply {
                    tradingSymbol = heading
                }
                list.add(position)
                Timber.d("after total list item: ${list.size} ")
                Timber.d("after new list item: ${listOfSelectedItem.size} ")
                Timber.d("onActionItemClicked combine")
            }
        }

        finishActionMode()
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        isOn = false
        listOfSelectedItem.clear()
        notifyDataSetChanged()
        Timber.d("onDestroyActionMode")
    }

    fun startActionMode(
        appCompatDelegate: AppCompatDelegate,
        @MenuRes menuResId: Int,
        title: String? = null
    ) {
        this.menuResId = menuResId
        this.title = title
        appCompatDelegate.startSupportActionMode(this@MultiSelect)
    }

    fun finishActionMode() {
        mode?.finish()
    }

    fun updateList(newList: List<Position>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }


}