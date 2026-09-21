package com.ferdev.whattoeatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ferdev.whattoeatapp.data.AppDatabase
import com.ferdev.whattoeatapp.data.DataSeeder
import com.ferdev.whattoeatapp.ui.BusquedaScreen
import com.ferdev.whattoeatapp.ui.HomeScreen
import com.ferdev.whattoeatapp.ui.RegistroScreen
import com.ferdev.whattoeatapp.ui.theme.WhatToEatAppTheme

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = AppDatabase.getInstance(this)

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isSeeded = prefs.getBoolean("data_seeded", false)
        if (!isSeeded) {
            DataSeeder(database).seedData()
            prefs.edit().putBoolean("data_seeded", true).apply()
        }

        setContent {
            WhatToEatAppTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(
                            onGoToRegister = { navController.navigate("register") },
                            onGoToSearch = { navController.navigate("search") }
                        )
                    }

                    composable("register") {
                        RegistroScreen(
                            database = database,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("search") {
                        BusquedaScreen(
                            database = database,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}