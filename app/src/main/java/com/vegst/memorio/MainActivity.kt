package com.vegst.memorio

import android.animation.Animator
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.vegst.memorio.ui.mainactivity.PlaceholderFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL
import android.animation.ObjectAnimator
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.support.v4.app.FragmentManager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.view.View
import kotlinx.android.synthetic.main.activity_main.toolbar
import android.widget.Toast




class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        nav_view.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            showConcept("YRzeLiFRU7JbNeEUI9zb")
        }

        val user = mAuth.currentUser
        if (user != null) {
            nav_view.getHeaderView(0).displayName.text = user.displayName
            nav_view.getHeaderView(0).email.text = user.email
            if (user.photoUrl != null) {
                doAsync {
                    val newurl = URL(user.photoUrl.toString())
                    val icon: Bitmap? = BitmapFactory.decodeStream(newurl.openConnection().getInputStream())
                    uiThread {
                        if (icon != null) {
                            photo.setImageBitmap(icon)
                        }
                    }
                }
            }
        }

        // Toolbar
        setSupportActionBar(toolbar)
        collapsingToolbarLayout.isTitleEnabled = false
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        super.setSupportActionBar(toolbar)

        val toggle = ExtendedActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close, supportFragmentManager
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }



    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(mAuthListener)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_placeholder -> {
                showConcept("YRzeLiFRU7JbNeEUI9zb")
            }
            R.id.nav_logout -> {
                mAuth.signOut()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun showConcept(id: String, backstack: Boolean = false) {
        val fragment = ConceptFragment.newInstance(id)
        fragment.setOnConceptClickListener {
            if (it != null) {
                showConcept(it.id, true)
            }
        }
        if (backstack) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }
        else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }
}