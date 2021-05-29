package com.example.smarttrade.util

import android.view.Menu
import android.view.MenuItem
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.ActionMode
import com.example.smarttrade.R
import com.zerodhatech.models.Position
import timber.log.Timber

class MultiSelect : ActionMode.Callback {


    @MenuRes
    var menuResId: Int = 0
    var title: String? = null
    var isOn = false
    val listOfSelectedItem: MutableList<Position> = mutableListOf()


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
                onActionClickListener?.invoke()
                Timber.d("onActionItemClicked combine")
            }
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        isOn = false
        listOfSelectedItem.clear()
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

}