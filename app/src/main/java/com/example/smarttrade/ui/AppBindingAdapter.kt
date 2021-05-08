package com.example.smarttrade.ui

import android.widget.TextView
import androidx.databinding.BindingAdapter

interface AppBindingComponent : androidx.databinding.DataBindingComponent {

}

class AppBindingAdapter() {

    // BindingAdapter : we can write methods with binding adapter

    @BindingAdapter("bind_text", requireAll = false)
    fun setAppearance(view: TextView, text: String?) {
        view.text = text
    }
}