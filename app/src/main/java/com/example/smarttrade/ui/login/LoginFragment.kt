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
import com.example.smarttrade.extension.startActivity
import com.example.smarttrade.manager.PreferenceManager
import com.example.smarttrade.ui.base.BaseFragment
import com.example.smarttrade.ui.position.PortfolioActivity
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinApiExtension
import timber.log.Timber
import kotlin.coroutines.suspendCoroutine


@KoinApiExtension
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    private val loginViewModel: LoginViewModel by sharedViewModel()

    override fun getBindingVariable(): Int = BR.loginViewModel

    override fun getViewModel(): LoginViewModel = loginViewModel

    override fun getLayoutId(): Int = R.layout.fragment_login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = PreferenceManager.getAccessToken()
        KiteConnect.setAccessToken(accessToken)
        // to check the access token has expired or not we are making
        // get position call
        CoroutineScope(Dispatchers.IO).launch {
            try {
                KiteConnect.getPosition()
                withContext(Dispatchers.Main) {
                    PreferenceManager.setUserLoggedIn(true)
                    requireActivity().startActivity<PortfolioActivity>()
                }
            } catch (e: KiteException) {
                PreferenceManager.setUserLoggedIn(false)
                e.printStackTrace()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding?.zerodhaSignInBtn?.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_webViewFragment)
        }
    }

}