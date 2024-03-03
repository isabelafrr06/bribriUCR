package com.example.bribriucr

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bribriucr.databinding.ActivityMainBinding
import com.example.bribriucr.db.DbHelper
import com.example.bribriucr.ui.TopicCard
import com.example.bribriucr.ui.TopicCardAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Database
        val dbHelper = DbHelper(this)
        val db: SQLiteDatabase = dbHelper.getWritableDatabase()

        // Hacer las operaciones que queramos sobre la base de datos
        //
        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        //prueba card
        val rvCard = findViewById<RecyclerView>(R.id.recyclerList)
        rvCard.layoutManager = LinearLayoutManager(this)

        val listaEquipos = ArrayList<TopicCard>()

        listaEquipos.add(TopicCard("Comidas", "Photo", "0%"))
        listaEquipos.add(TopicCard("Animales", "Photo", "15%"))
        listaEquipos.add(TopicCard("Utensilios", "Photo", "20%"))

        val adaptadorCarta = TopicCardAdapter(listaEquipos)
        rvCard.adapter = adaptadorCarta
    }
}