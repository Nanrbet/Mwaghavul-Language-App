package com.mwaghavul.mwaghavullexicon

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.mwaghavul.mwaghavullexicon.databinding.ActivityMainBinding
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

lateinit var dbHelper: DBHelper

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var reviewManager: ReviewManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install modern Splash Screen FIRST
        installSplashScreen()

        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle window insets for a professional edge-to-edge look
        ViewCompat.setOnApplyWindowInsetsListener(binding.drawerLayout) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // Apply top padding to toolbar so it sits below status bar
            binding.toolbar.setPadding(0, systemBars.top, 0, 0)
            
            // Bottom navigation bar and system navigation area
            binding.buttomAppBar.setPadding(0, 0, 0, systemBars.bottom)

            insets
        }

        reviewManager = ReviewManagerFactory.create(this)

        // Initialize components that require context here
        MobileAds.initialize(this) { }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true) // Enable title for fragment names
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
                    openFragment(SearchFragment(), "search", true)
                }
                R.id.nav_bookmark -> {
                    openFragment(BookmarkFragment(), "bookmark", true)
                    // Change the icon to filled star
                    item.setIcon(R.drawable.filled_star_24)
                }
                R.id.nav_history -> {
                    openFragment(HistoryFragment(), "history", true)
                }
            }
            true
        }
        fragmentManager = supportFragmentManager
        
        // Load default fragment if not already restored
        if (savedInstanceState == null) {
            openFragment(HomeFragment(), "home", true)
            binding.buttomNavigation.selectedItemId = R.id.nav_home
        }
        
        bottomNavigationView = findViewById(R.id.buttom_navigation)

        // Register the custom OnBackPressedCallback
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    val fragmentManager = supportFragmentManager
                    if (fragmentManager.backStackEntryCount > 1) {
                        fragmentManager.popBackStack()
                        val currentFragment = fragmentManager.fragments.lastOrNull()
                        if (currentFragment != null) {
                            updateBottomNavigation(currentFragment)
                        }
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
                showRateApp()
            }
            R.id.nav_share -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
                if (currentFragment is DetailFragment) {
                    val selectedWord = currentFragment.getSelectedWord()
                    shareWord(selectedWord)
                } else {
                    Toast.makeText(this, "Go to search and click a word", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_help -> {
                openFragment(HelpFragment(), "Help page", false)
            }
            R.id.nav_about -> {
                openFragment(AboutFragment(), "About Us", false)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val activeFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)

        when (activeFragment) {
            is SearchFragment -> supportActionBar?.title = "Mwaghavul Lexicon"
            is BookmarkFragment -> supportActionBar?.title = "Bookmark"
            is HistoryFragment -> supportActionBar?.title = "History"
            is HomeFragment -> supportActionBar?.title = "Home"
            is HelpFragment -> supportActionBar?.title = "Help page"
            is AboutFragment -> supportActionBar?.title = "About Us"
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

        // After the fragment transaction, invalidate the options menu
        invalidateOptionsMenu()
    }

    private fun shareWord(word: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, word)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, null))
    }

    private fun showRateApp() {
        val uri = "market://details?id=$packageName".toUri()
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(myAppLinkToMarket)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "Impossible to find an application for the market", Toast.LENGTH_LONG).show()
        }
    }
}
