package com.ferdev.whattoeatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ferdev.whattoeatapp.data.AppDatabase
import com.ferdev.whattoeatapp.data.DataSeeder
import com.ferdev.whattoeatapp.ui.BusquedaScreen
import com.ferdev.whattoeatapp.ui.HomeScreen
import com.ferdev.whattoeatapp.ui.RegistroScreen
import com.ferdev.whattoeatapp.ui.ResultsScreen
import com.ferdev.whattoeatapp.ui.theme.WhatToEatAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = AppDatabase.getInstance(this)

        lifecycleScope.launch {
            val dayCount = withContext(Dispatchers.IO) {
                database.diaDao().getCount()
            }

            android.util.Log.d("SEEDER", "Day count in DB: $dayCount")

            if (dayCount == 0) {
                android.util.Log.d("SEEDER", "DB is empty. Running seeder...")
                DataSeeder(database).seedData()
            } else {
                android.util.Log.d("SEEDER", "DB already has data. Skipping seeder.")
            }
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
                            onBack = { navController.popBackStack() },
                            onSearchResults = { dia, desdeTime, hastaTime, platosIds ->
                                val route = "results?day=${dia ?: -1}" +
                                        "&from=${desdeTime}" +
                                        "&to=${hastaTime}" +
                                        "&dishes=${platosIds.joinToString(",")}"
                                navController.navigate(route)
                            }
                        )
                    }

                    composable(
                        route = "results?day={dia}&from={from}&to={to}&dishes={platos}",
                        arguments = listOf(
                            navArgument("dia") { type = NavType.IntType; defaultValue = -1 },
                            navArgument("from") { type = NavType.StringType; defaultValue = "" },
                            navArgument("to") { type = NavType.StringType; defaultValue = "" },
                            navArgument("platos") { type = NavType.StringType; defaultValue = "" }
                        )
                    ) { backStackEntry ->
                        val day = backStackEntry.arguments?.getInt("dia")?.takeIf { it != -1 }
                        val from = backStackEntry.arguments?.getString("from") ?: ""
                        val to = backStackEntry.arguments?.getString("to") ?: ""
                        val platoIdsStr = backStackEntry.arguments?.getString("platos") ?: ""
                        val platoIds = if (platoIdsStr.isBlank()) emptyList()
                        else platoIdsStr.split(",").mapNotNull { it.toIntOrNull() }

                        ResultsScreen(
                            database = database,
                            day = day,
                            desdeTime = from,
                            hastaTime = to,
                            platosIds = platoIds,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}