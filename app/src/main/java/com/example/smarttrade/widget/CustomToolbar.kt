package com.example.smarttrade.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import com.example.smarttrade.R
import com.example.smarttrade.databinding.CustomToolbarBinding
import com.google.android.material.appbar.MaterialToolbar

class CustomToolbar : MaterialToolbar {
    var txtTitle: AppCompatTextView? = null

    var timer: AppCompatTextView? = null

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context, attributeSet, 0)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init(context, attributeSet, defStyleAttr)
    }

    private var toolbarBinding: CustomToolbarBinding? = null
    private fun init(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) {
        val layoutInflater = LayoutInflater.from(context)
        toolbarBinding = DataBindingUtil.inflate<CustomToolbarBinding>(
            layoutInflater,
            R.layout.custom_toolbar,
            this,
            true
        )
        timer = toolbarBinding?.timer
        txtTitle = toolbarBinding?.title
        if (attributeSet != null) {
            val typedValue = context.theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.CustomToolbar,
                defStyleAttr,
                0
            )
            val title = typedValue.getText(R.styleable.Toolbar_title)
            toolbarBinding?.title?.text = title
            val timeAgo = typedValue.getText(R.styleable.CustomToolbar_timeAgo)
            toolbarBinding?.timer?.text = timeAgo
            typedValue.recycle()
        }
    }

    override fun setTitle(resId: Int) {
        toolbarBinding?.title?.setText(resId)
    }


}