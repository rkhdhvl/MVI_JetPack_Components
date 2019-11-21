package com.practice.myapplication.mvi.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.practice.myapplication.R
import com.practice.myapplication.mvi.ui.BaseActivity
import com.practice.myapplication.mvi.ui.auth.AuthActivity
import com.practice.myapplication.mvi.ui.main.account.ChangePasswordFragment
import com.practice.myapplication.mvi.ui.main.account.UpdateAccountFragment
import com.practice.myapplication.mvi.ui.main.blog.UpdateBlogFragment
import com.practice.myapplication.mvi.ui.main.blog.ViewBlogFragment
import com.practice.myapplication.mvi.util.BottomNavController
import com.practice.myapplication.mvi.util.BottomNavController.*
import com.practice.myapplication.mvi.util.setUpNavigation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), NavGraphProvider,
    OnNavigationGraphChanged,
    OnNavigationReselectedListener {

    private lateinit var bottomNavigationView: BottomNavigationView
    private val bottomNavController : BottomNavController by lazy {
        BottomNavController(
            this,
            R.id.main_nav_host_fragment,
            R.id.nav_blog,
            this,
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpActionBar()
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        // extension function that was created on the bottomNavigationView
        bottomNavigationView.setUpNavigation(bottomNavController,this)
        // if its the first time the activity is getting selected, then pass nothing to onNavigationItemSelected()
        if(savedInstanceState == null){
          bottomNavController.onNavigationItemSelected()
        }

        // to be commented
        /*tool_bar.setOnClickListener {
            sessionManager.logout()
        }*/

        subscribeObservers()
    }

    fun subscribeObservers() {
        sessionManager.cachedToken.observe(this, Observer { authToken ->
            Log.d(TAG, "MainActivity, subscribeObservers: ViewState: ${authToken}")
            if (authToken == null || authToken.account_pk == -1 || authToken.token == null) {
                navAuthActivity()
                finish()
            }
        })
    }

    private fun navAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(bool: Boolean) {
        if (bool) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.GONE
        }
    }


    // here get the different case for Navigation Id
    override fun getNavGraphId(itemId: Int) = when(itemId)
    {
        R.id.nav_blog ->{
               R.navigation.nav_blog
        }

        R.id.nav_account ->{
              R.navigation.nav_account
        }

        R.id.nav_create_blog ->{
             R.navigation.nav_create_blog
        }

        else -> {
            R.navigation.nav_blog
        }

    }

    override fun onGraphChange() {
       // TODO("What needs to happen when the graph changes")
    }

    private fun setUpActionBar()
    {
        setSupportActionBar(tool_bar)
    }

    // what happens when a selected item gets reselected
    override fun onReselectNavItem(navController: NavController, fragment: Fragment) = when(fragment)
    {
        is ViewBlogFragment -> {
            navController.navigate(R.id.action_viewBlogFragment_to_home)
        }

        is UpdateBlogFragment -> {
            navController.navigate(R.id.action_updateBlogFragment_to_home)
        }

        is UpdateAccountFragment -> {
            navController.navigate(R.id.action_updateAccountFragment_to_home)
        }

        is ChangePasswordFragment -> {
            navController.navigate(R.id.action_changePasswordFragment_to_home)
        }

        else -> {
            // do nothing
        }

    }



    override fun onBackPressed() = bottomNavController.onBackPressed()

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun expandAppBar() {
        findViewById<AppBarLayout>(R.id.app_bar).setExpanded(true)
    }
}
