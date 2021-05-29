package com.example.smarttrade.ui.login

import android.os.Bundle
import android.view.View
import com.example.smarttrade.BR
import com.example.smarttrade.KiteConnect
import com.example.smarttrade.R
import com.example.smarttrade.databinding.FragmentWebViewBinding
import com.example.smarttrade.ui.base.BaseFragment
import org.koin.android.ext.android.inject

class WebViewFragment : BaseFragment<FragmentWebViewBinding, LoginViewModel>() {

    private val loginViewModel: LoginViewModel by inject()

    override fun getLayoutId(): Int = R.layout.fragment_web_view

    override fun getViewModel(): LoginViewModel = loginViewModel

    override fun getBindingVariable(): Int = BR.loginViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val kiteConnect = KiteConnect.getLoginUrl()
        viewDataBinding?.webViewLogin?.run {
            loadUrl(kiteConnect)
        }
    }
}