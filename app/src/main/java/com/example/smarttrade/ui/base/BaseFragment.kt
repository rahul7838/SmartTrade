package com.example.smarttrade.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import org.koin.core.component.KoinApiExtension

/**
 *  BaseFragment for keeping an instance of [ViewDataBinding]
 */
@KoinApiExtension
abstract class BaseFragment<VB : ViewDataBinding, VM : BaseViewModel> : Fragment() {

    protected var viewDataBinding: VB? = null

    protected val parentActivity by lazy { activity as BaseActivity<*> }

    /**
     *  Provides layout id to be inflated
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * Override for get view model
     * @return view model instance
     */
    abstract fun getViewModel(): VM

    /**
     * Override for get binding variable for the view model
     * @return variable id
     */
    abstract fun getBindingVariable(): Int


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        performDataBinding()
        return viewDataBinding?.root
    }

    private fun performDataBinding() {
        viewDataBinding?.setVariable(getBindingVariable(), getViewModel())
        viewDataBinding?.executePendingBindings()
        viewDataBinding?.lifecycleOwner = viewLifecycleOwner
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
    }

    private fun initObserver() {
        getViewModel().showProgressBar.observe(viewLifecycleOwner, {
            setLoader(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewDataBinding = null
    }

    fun setLoader(isVisible: Boolean) {
        parentActivity.setLoader(isVisible)
    }
}