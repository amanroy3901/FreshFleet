package com.example.freshfleet.presentation.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.freshfleet.R
import com.example.freshfleet.data.local.AppDatabase
import com.example.freshfleet.data.repository.ShopCartRepository
import com.example.freshfleet.databinding.ActivityMainBinding
import com.example.freshfleet.presentation.ui.fragments.CartFragment
import com.example.freshfleet.presentation.ui.fragments.FavFragment
import com.example.freshfleet.presentation.ui.fragments.HomeFragment
import com.example.freshfleet.presentation.viewmodel.CartViewModelFactory
import com.example.freshfleet.presentation.viewmodel.ShopCartViewModel
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding
    lateinit var toolbar: Toolbar
    lateinit var viewModel: ShopCartViewModel
    private lateinit var database: AppDatabase
    private lateinit var cartRepository: ShopCartRepository
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    var size: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navigationView = binding.navigationView
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item clicks here
            when (menuItem.itemId) {
                R.id.nav_fav -> {
                    supportFragmentManager.beginTransaction().replace(
                        binding.fragmentContainer.id, FavFragment()
                    ).addToBackStack(null).commit()

                    true
                }
                R.id.nav_cart -> {
                    supportFragmentManager.beginTransaction().replace(
                        binding.fragmentContainer.id, CartFragment()
                    ).addToBackStack(null).commit()
                    true
                }
            }
            // Close the drawer after handling the click
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        database = AppDatabase.getDatabase(this)
        cartRepository = ShopCartRepository(database)
        viewModel = ViewModelProvider(
            this,
            CartViewModelFactory(cartRepository, null, database, null)
        ).get(ShopCartViewModel::class.java)

        viewModel.cartItems.observe(this) { cartItems ->
            size = cartItems.size
            invalidateOptionsMenu()
        }

        supportFragmentManager.beginTransaction().replace(
            binding.fragmentContainer.id, HomeFragment()
        ).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.drawer_menu, menu)
        val menuItem = menu.findItem(R.id.cartIcon)
        val actionView = menuItem.actionView!!

        val textView: TextView = actionView.findViewById(R.id.cart_badge_text_view)

        actionView.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(
                binding.fragmentContainer.id, CartFragment()
            ).addToBackStack(null).commit()
        }
        textView.text = size.toString()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.favIcon -> {
                supportFragmentManager.beginTransaction().replace(
                    binding.fragmentContainer.id, FavFragment()
                ).addToBackStack(null).commit()

                true
            }

            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}