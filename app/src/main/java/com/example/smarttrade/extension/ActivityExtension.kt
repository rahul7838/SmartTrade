package com.example.smarttrade.extension

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.example.smarttrade.R

/**
 * This extension method responsible to navigating to Activity with below parameter.
 *
 * @param T is Activity name that needs to be navigated
 * @param bundle that needs to be passed to the activity.
 * @param navOptions specifies the animation & launch mode for the activity.
 * @param navigatorExtra check[Navigator.Extras]
 * @param finishActivity true when your activity is done and should be closed.
 */
inline fun <reified T> Activity.startActivity(
    bundle: Bundle? = null,
    navOptions: NavOptions = enterRight(),
    navigatorExtra: Navigator.Extras? = null,
    finishActivity: Boolean = false
) {
    ActivityNavigator(this).run {
        navigate(
            createDestination().setIntent(Intent(this@startActivity, T::class.java)),
            bundle,
            navOptions,
            navigatorExtra
        )
    }
    if (finishActivity) finish()
}

/**
 * Extension function to provide the navigation option to open the activity from right to left.
 *
 * @param isSingleTop Launch mode of the activity.
 * @return NavOptions
 */
fun Activity.enterRight(isSingleTop: Boolean = false): NavOptions {
    return NavOptions.Builder()
        .setEnterAnim(R.anim.enter_from_right)
        .setExitAnim(R.anim.exit_to_left)
        .setLaunchSingleTop(isSingleTop)
        .setPopEnterAnim(R.anim.enter_from_left)
        .setPopExitAnim(R.anim.exit_to_right)
        .build()
}

/**
 * Navigation extras
 *
 * @param flag Set special flags controlling how this intent is handled
 *
 */
fun Activity.navigationExtras(vararg flag: Int): ActivityNavigator.Extras {
    return ActivityNavigator.Extras.Builder().apply {
        flag.forEach {
            addFlags(it)
        }
    }.build()
}

/**
 * Add fragment to the activity
 *
 * @param containerViewId optional identifier of the container this fragment is
 * to be placed in.  If 0, it will not be placed in a container.
 * @param fragment instance of the fragment
 * @param isAddToBackStack true if fragment transaction should be added to back stack
 */
fun FragmentActivity.addFragment(
    @IdRes containerViewId: Int = 0,
    fragment: Fragment,
    isAddToBackStack: Boolean = false
) {
    supportFragmentManager.beginTransaction().apply {
        add(containerViewId, fragment, fragment::class.java.simpleName)
        if (isAddToBackStack) addToBackStack(fragment::class.java.simpleName)
        supportFragmentManager.executePendingTransactions()
        commitNow()
    }
}

/**
 * Finds a fragment that was identified by the given tag
 *
 * @param T fragment name
 * @return if found return fragment instance
 */
inline fun <reified T> FragmentActivity.findFragment(): T? {
    return supportFragmentManager.findFragmentByTag(T::class.java.simpleName) as? T
}
