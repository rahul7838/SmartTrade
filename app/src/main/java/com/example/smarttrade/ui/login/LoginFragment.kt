package com.example.smarttrade.ui.login

import com.example.smarttrade.BR
import com.example.smarttrade.R
import com.example.smarttrade.databinding.FragmentLoginBinding
import com.example.smarttrade.ui.base.BaseFragment
import org.koin.android.ext.android.inject

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    private val loginViewModel: LoginViewModel by inject()

    override fun getBindingVariable(): Int = BR.loginViewModel

    override fun getViewModel(): LoginViewModel = loginViewModel

    override fun getLayoutId(): Int = R.layout.fragment_login

}