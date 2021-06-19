package com.example.smarttrade.extension

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

fun <T: ViewDataBinding> Fragment.getViewDataBinding(
    @LayoutRes layoutId: Int,
    viewGroup: ViewGroup?,
    viewModel: ViewModel,
    bindingVariable: Int,
    attachToParent: Boolean = false,
): T {
    val viewDataBinding = DataBindingUtil.inflate<T>(layoutInflater, layoutId, viewGroup, attachToParent)
    viewDataBinding?.setVariable(bindingVariable, viewModel)
    viewDataBinding?.executePendingBindings()
    viewDataBinding?.lifecycleOwner = viewLifecycleOwner
    return viewDataBinding
}