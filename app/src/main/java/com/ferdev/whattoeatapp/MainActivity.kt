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
import com.ferdev.whattoeatapp.ui.HomeScreen
import com.ferdev.whattoeatapp.ui.RegisterScreen
import com.ferdev.whattoeatapp.ui.ResultsScreen
import com.ferdev.whattoeatapp.ui.SearchScreen
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
                database.dayDao().getCount()
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
                        RegisterScreen(
                            database = database,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("search") {
                        SearchScreen(
                            database = database,
                            onBack = { navController.popBackStack() },
                            onSearchResults = { day, fromTime, toTime, dishIds ->
                                val route = "results?day=${day ?: -1}" +
                                        "&from=${fromTime}" +
                                        "&to=${toTime}" +
                                        "&dishes=${dishIds.joinToString(",")}"
                                navController.navigate(route)
                            }
                        )
                    }

                    composable(
                        route = "results?day={day}&from={from}&to={to}&dishes={dishes}",
                        arguments = listOf(
                            navArgument("day") { type = NavType.IntType; defaultValue = -1 },
                            navArgument("from") { type = NavType.StringType; defaultValue = "" },
                            navArgument("to") { type = NavType.StringType; defaultValue = "" },
                            navArgument("dishes") { type = NavType.StringType; defaultValue = "" }
                        )
                    ) { backStackEntry ->
                        val day = backStackEntry.arguments?.getInt("day")?.takeIf { it != -1 }
                        val from = backStackEntry.arguments?.getString("from") ?: ""
                        val to = backStackEntry.arguments?.getString("to") ?: ""
                        val dishIdsStr = backStackEntry.arguments?.getString("dishes") ?: ""
                        val dishIds = if (dishIdsStr.isBlank()) emptyList()
                        else dishIdsStr.split(",").mapNotNull { it.toIntOrNull() }

                        ResultsScreen(
                            database = database,
                            day = day,
                            fromTime = from,
                            toTime = to,
                            dishIds = dishIds,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}