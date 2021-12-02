package com.example.smarttrade.ui.login

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.smarttrade.BR
import com.example.smarttrade.R
import com.example.smarttrade.databinding.FragmentLoginBinding
import com.example.smarttrade.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinApiExtension


@KoinApiExtension
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    private val loginViewModel: LoginViewModel by sharedViewModel()

    override fun getBindingVariable(): Int = BR.loginViewModel

    override fun getViewModel(): LoginViewModel = loginViewModel

    override fun getLayoutId(): Int = R.layout.fragment_login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding?.zerodhaSignInBtn?.setOnClickListener {
//            openLink(requireContext(), KiteConnect.getLoginUrl().toUri())
            findNavController().navigate(R.id.action_loginFragment_to_webViewFragment)
        }
    }
}