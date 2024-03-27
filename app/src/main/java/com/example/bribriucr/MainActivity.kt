package com.example.bribriucr

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.bribriucr.databinding.ActivityMainBinding
import com.example.bribriucr.db.DbHelper
import com.example.bribriucr.ui.dashboard.DashboardFragment
import com.example.bribriucr.ui.home.HomeFragment
import com.example.bribriucr.ui.home.HomeModel
import com.example.bribriucr.ui.profile.FragmentProfile
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale
/**
 * The main activity class for the application. This class handles the overall application flow,
 * navigation, and fragment management.
 */
class MainActivity : AppCompatActivity() {

    // Binding for accessing views in the layout
    private lateinit var binding: ActivityMainBinding
    /**
     * onCreate function is called when the activity is first created.
     *
     * @param savedInstanceState Bundle containing the data of the previous instance (if any).
     */
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

        // Navigation setup
        val navView: BottomNavigationView = binding.navView
        navView.post {
            navView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.navigation_home -> replaceFragment(HomeFragment(homeModel, this))
                    R.id.navigation_profile -> replaceFragment(FragmentProfile(dbHelper))
                    R.id.navigation_dashboard -> replaceFragment(DashboardFragment(dbHelper))
                    else -> {
                        // Handle other navigation items
                    }
                }
                true
            }
        }
    }
    /**
     * Function to display a dialog for language selection and update the application locale.
     */
    private fun changeLanguage() {
        val listLang = arrayOf("Español", "Bribri")
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Escoger Idioma")
        mBuilder.setSingleChoiceItems(listLang, -1) { dialog, which ->
            if (which == 0) {
                setLocale("es")
            } else if (which == 1) {
                setLocale("")
            }

            // Send broadcast to notify fragments about language change
            val intent = Intent("language_changed")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

            dialog.dismiss()
        }
        val mDialog = mBuilder.create()
        mDialog.show()
    }
    /**
     * Function to set the application locale based on the provided language code.
     *
     * @param lang The language code (e.g., "es" for Spanish).
     */
    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        // Log.d("MainActivity", "Locale changed to: $locale")
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", lang)
        editor.apply()

        val intent = Intent(this@MainActivity, MainActivity::class.java)  // Create an intent for MainActivity
        finish()  // Finish the current activity
        startActivity(intent)  // Start a new instance of MainActivity
    }
    /**
     * Function to replace the fragment container with a new fragment.
     *
     * @param fragment The fragment to be displayed.
     */
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment)
        fragmentTransaction.commit()
    }

    // Handle menu inflation and selection
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.navigation_language -> {
                changeLanguage()
                return true
            }
            R.id.navigation_help -> {
                showHelp()
                return true
            }
            R.id.navigation_close -> {
                closeApp()
                return true
            }
            // ... other menu items
        }
        return super.onOptionsItemSelected(menuItem)
    }
    /**
     * Function to terminate the activity, effectively closing the application.
     */
    private fun closeApp() {
        finish()
    }
    /**
     * Function to display a simple help dialog with application information.
     */
    private fun showHelp() {
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Ayuda")
        mBuilder.setMessage("Seleccione alguno de los botones con los distintos temas")
        mBuilder.setPositiveButton("Aceptar", null)
        val dialog = mBuilder.create()
        dialog.show()
    }
}

