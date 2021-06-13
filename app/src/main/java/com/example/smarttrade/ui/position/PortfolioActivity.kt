package com.example.smarttrade.ui.position

import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.findNavController
import com.example.smarttrade.R
import com.example.smarttrade.databinding.ActivityPortfolioBinding
import com.example.smarttrade.manager.PreferenceManager
import com.example.smarttrade.ui.base.BaseActivity
import com.example.smarttrade.ui.base.BaseViewModel
import com.example.smarttrade.util.REQUEST_TOKEN
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension
import org.koin.core.parameter.parametersOf
import timber.log.Timber

@KoinApiExtension
class PortfolioActivity : BaseActivity<ActivityPortfolioBinding>(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    private var requestToken: String? = null

    private val positionViewModel: PositionViewModel by viewModel { parametersOf(requestToken) }

    override fun getViewModel(): BaseViewModel = positionViewModel

    override var layoutId: Int = R.layout.activity_portfolio

    override fun onCreate(savedInstanceState: Bundle?) {
        requestToken = intent.getStringExtra(REQUEST_TOKEN)
            ?: if (PreferenceManager.getUserLoggedIn()) null else throw IllegalStateException("Request token can not be null") // must be before super.onCreate()
        super.onCreate(savedInstanceState)
        viewBinding?.bottomNavigation?.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.position -> {
                findNavController(R.id.portfolio_nav_host).navigate(R.id.portfolioFragment3)
            }
            R.id.group -> {
                findNavController(R.id.portfolio_nav_host).navigate(R.id.groupFragment)
            }
        }
        return true
    }

    protected fun finalize() {
        // finalization logic
        Timber.d("Before garbage collection")
    }
}