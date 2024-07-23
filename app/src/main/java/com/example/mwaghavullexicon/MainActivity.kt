package com.example.mwaghavullexicon

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.mwaghavullexicon.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

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
            when(item.itemId){
                R.id.nav_home -> openFragment(HomeFragment(), true)
                R.id.nav_search -> openFragment(SearchFragment(), true)
                R.id.nav_bookmark -> {openFragment(BookmarkFragment(), true)
                        // Change the icon to filled star
                        item.setIcon(R.drawable.filled_star_24)}
                R.id.nav_history -> openFragment(HistoryFragment(), true)
            }
            true
        }
        fragmentManager = supportFragmentManager
        openFragment(HomeFragment(), true)
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
                    }else if (fragmentManager.backStackEntryCount == 1) {
                        fragmentManager.popBackStack()
                    }else {
                        finish()
                    }
                }
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_rate -> { Toast.makeText(this, "To Rating page",Toast.LENGTH_SHORT).show()
                // Handle rate action
                //TODO
            }
            R.id.nav_share -> { Toast.makeText(this, "To Share page",Toast.LENGTH_SHORT).show()
                // Handle share action
                // TODO
            }
            R.id.nav_help -> { Toast.makeText(this, "To Help page",Toast.LENGTH_SHORT).show()
                // Handle help action
                //TODO
            }
            R.id.nav_about -> {openFragment(AboutFragment(), false)}
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // To close drawer if back button is pressed
//    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
//    override fun onBackPressed() {
//        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
//            binding.drawerLayout.closeDrawer(GravityCompat.START)
//        }else {
//            super.onBackPressedDispatcher.onBackPressed()
//        }
//    }
    private fun updateBottomNavigation(fragment: Fragment) {
        when (fragment) {
            is HomeFragment -> bottomNavigationView.selectedItemId = R.id.nav_home
            is SearchFragment -> bottomNavigationView.selectedItemId = R.id.nav_search
            is BookmarkFragment -> bottomNavigationView.selectedItemId = R.id.nav_bookmark
            is HistoryFragment -> bottomNavigationView.selectedItemId = R.id.nav_history
        }
    }
    // To not keep repeating the code to change the fragments on the frame layout of the main activity
    private fun openFragment(fragment: Fragment, isTop: Boolean){
        val fragmentTransaction: FragmentTransaction= fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment_container, fragment)
            if (!isTop)
             fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}
