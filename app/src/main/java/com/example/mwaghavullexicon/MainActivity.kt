package com.example.mwaghavullexicon

import Word
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.mwaghavullexicon.databinding.ActivityMainBinding
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize components that require context here
        MobileAds.initialize(this) { }

        setSupportActionBar(binding.toolbar)
        dbHelper = DBHelper(this,null)

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navDrawer.setNavigationItemSelectedListener(this)
        binding.buttomNavigation.background = null
        binding.buttomNavigation.setOnItemSelectedListener { item ->
            // Reset star icon to unfilled if another item is selected
            if (binding.buttomNavigation.selectedItemId != R.id.nav_bookmark) {
                binding.buttomNavigation.menu.findItem(R.id.nav_bookmark).setIcon(R.drawable.round_star_outline_24)
            }
            when (item.itemId) {
                R.id.nav_home -> {
                    openFragment(HomeFragment(), "home", true)
                }
                R.id.nav_search -> {
                    openFragment(SearchFragment(dbHelper), "search", true)
                }
                R.id.nav_bookmark -> {
                    openFragment(BookmarkFragment(dbHelper), "bookmark", true)
                    // Change the icon to filled star
                    item.setIcon(R.drawable.filled_star_24)
                }
                R.id.nav_history -> {
                    openFragment(HistoryFragment(dbHelper), "history", true)
                }
            }
            true
        }
        fragmentManager = supportFragmentManager
        openFragment(HomeFragment(), "home", true)
        bottomNavigationView = findViewById(R.id.buttom_navigation)
        // Load the default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.nav_home
        }
        // Register the custom OnBackPressedCallback
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    val fragmentManager = supportFragmentManager
                    if (fragmentManager.backStackEntryCount > 1) {
                        fragmentManager.popBackStack()
                        val currentFragment = fragmentManager.fragments.last()
                        updateBottomNavigation(currentFragment)
                    } else if (fragmentManager.backStackEntryCount == 1) {
                        fragmentManager.popBackStack()
                    } else {
                        finish()
                    }
                }
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_rate -> {
                Toast.makeText(this, "To Rating page", Toast.LENGTH_SHORT).show()
                // Handle rate action
                // TODO
            }
            R.id.nav_share -> {
                Toast.makeText(this, "To Share page", Toast.LENGTH_SHORT).show()
                // Handle share action
                // TODO
            }
            R.id.nav_help -> {
                Toast.makeText(this, "To Help page", Toast.LENGTH_SHORT).show()
                // Handle help action
                // TODO
            }
            R.id.nav_about -> {
                openFragment(AboutFragment(), "about", false)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val activeFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)

        when (activeFragment) {
            is SearchFragment -> binding.toolbar.title = "Mwaghavul Lexicon"
            is BookmarkFragment -> binding.toolbar.title = "Bookmark"
            is HistoryFragment -> binding.toolbar.title = "History"
            is HomeFragment -> binding.toolbar.title = "Home"
        }
        return true
    }

    private fun updateBottomNavigation(fragment: Fragment) {
        when (fragment) {
            is HomeFragment -> bottomNavigationView.selectedItemId = R.id.nav_home
            is SearchFragment -> bottomNavigationView.selectedItemId = R.id.nav_search
            is BookmarkFragment -> bottomNavigationView.selectedItemId = R.id.nav_bookmark
            is HistoryFragment -> bottomNavigationView.selectedItemId = R.id.nav_history
        }
    }

    private fun openFragment(fragment: Fragment, tag: String, isTop: Boolean) {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment_container, fragment, tag)
        if (!isTop) fragmentTransaction.addToBackStack(tag)
        fragmentTransaction.commit()
    }
}
