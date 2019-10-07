package com.vegst.memorio

import android.animation.Animator
import android.animation.ObjectAnimator
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View

class ExtendedActionBarDrawerToggle(val activity: AppCompatActivity, val mDrawerLayout: DrawerLayout,
                                    val toolbar: Toolbar?, val openDrawerContentDescRes: Int,
                                    val closeDrawerContentDescRes: Int,
                                    val fragmentManager: FragmentManager
):
    ActionBarDrawerToggle(activity, mDrawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes),
    FragmentManager.OnBackStackChangedListener, View.OnClickListener {


    init {
        fragmentManager.addOnBackStackChangedListener(this)
        toolbar?.setNavigationOnClickListener(this)
    }

    private fun isRoot(): Boolean {
        return fragmentManager.backStackEntryCount == 0
    }

    private fun toggle() {
        if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onClick(p0: View?) {
        if (isRoot()) {
            toggle()
        } else {
            activity.onBackPressed()
        }
    }

    override fun onBackStackChanged() {
        ObjectAnimator.ofFloat(drawerArrowDrawable, "progress", if (isRoot()) 0f else 1f).apply {
            duration = 500
            start()
        }
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        if (isRoot()) {
            super.onDrawerSlide(drawerView, slideOffset)
        }
    }

    override fun onDrawerClosed(drawerView: View) {
        if (isRoot()) {
            super.onDrawerClosed(drawerView)
        }
    }

    override fun onDrawerOpened(drawerView: View) {
        if (isRoot()) {
            super.onDrawerOpened(drawerView)
        }
    }
}