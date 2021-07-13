package com.example.smarttrade.extension

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.example.smarttrade.R

fun openLink(context: Context, url: Uri) {
  val customTabIntent = CustomTabsIntent.Builder()
      .setStartAnimations(context, R.anim.enter_from_right, R.anim.exit_to_left)
      .setExitAnimations(context, R.anim.enter_from_right, R.anim.exit_to_left)
      .build()
    customTabIntent.launchUrl(context, url)
}