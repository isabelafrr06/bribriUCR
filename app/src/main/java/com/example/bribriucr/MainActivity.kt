package com.example.bribriucr

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bribriucr.databinding.ActivityMainBinding
import com.example.bribriucr.db.DbHelper
import com.example.bribriucr.ui.dashboard.DashboardFragment
import com.example.bribriucr.ui.home.HomeFragment
import com.example.bribriucr.ui.home.HomeModel
import com.example.bribriucr.ui.profile.FragmentProfile
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    // Binding for accessing views in the layout
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dbHelper = DbHelper(this)
        // Inflate the layout and set it as the content view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val homeModel = HomeModel(dbHelper)
        replaceFragment(HomeFragment(homeModel, this))
        // Database operations
        val db: SQLiteDatabase = dbHelper.writableDatabase
        // ---- Navigation setup ----
        val navView: BottomNavigationView = binding.navView
        navView.post {
            navView.setOnItemSelectedListener { it ->
                when (it.itemId) {
                    R.id.navigation_home -> replaceFragment(HomeFragment(homeModel, this))
                    R.id.navigation_profile -> replaceFragment(FragmentProfile(dbHelper))
                    R.id.navigation_dashboard -> replaceFragment(DashboardFragment(dbHelper))
                    else -> {

                    }
                }
                true
            }
        }
    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main,fragment)
        fragmentTransaction.commit()

    }
}

