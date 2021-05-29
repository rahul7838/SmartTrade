package com.example.smarttrade.ui.login

import android.os.Bundle
import android.view.View
import android.webkit.WebSettings.PluginState
import android.webkit.WebView
import androidx.navigation.fragment.findNavController
import com.example.smarttrade.BR
import com.example.smarttrade.KiteConnect
import com.example.smarttrade.R
import com.example.smarttrade.databinding.FragmentLoginBinding
import com.example.smarttrade.ui.base.BaseFragment
import org.koin.android.ext.android.inject
import timber.log.Timber


class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    private val loginViewModel: LoginViewModel by inject()

    override fun getBindingVariable(): Int = BR.loginViewModel

    override fun getViewModel(): LoginViewModel = loginViewModel

    override fun getLayoutId(): Int = R.layout.fragment_login

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding?.zerodhaSignInBtn?.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_webViewFragment)
//            Timber.d("create kite connect session")
//            KiteConnect.createSession("9suojKGQZGnnJ3n8XcmipmzLMOGWGbwH")
//            KiteConnect.getPosition()
        }
    }

}