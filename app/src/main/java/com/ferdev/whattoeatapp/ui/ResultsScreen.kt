package com.ferdev.whattoeatapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferdev.whattoeatapp.data.AppDatabase
import com.ferdev.whattoeatapp.data.Restaurant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class SearchResult(
    val restaurant: Restaurant,
    val schedulesText: String,
    val dishesText: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    database: AppDatabase,
    day: Int?,
    fromTime: String,
    toTime: String,
    dishIds: List<Int>,
    onBack: () -> Unit
) {
    var results by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(day, fromTime, toTime, dishIds) {
        isLoading = true
        results = performSearch(database, day, fromTime, toTime, dishIds)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Results (${results.size})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                    )
                }
                results.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "😔",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No restaurants found",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try different filters!",
                            fontSize = 14.sp
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(results) { result ->
                            ResultCard(result)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultCard(result: SearchResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = result.restaurant.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "📍 ${result.restaurant.address}", fontSize = 14.sp)
            Text(text = "📞 ${result.restaurant.phone}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "🕐 ${result.schedulesText}", fontSize = 14.sp)
            Text(text = "🍽️ ${result.dishesText}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

// ====================
//     FILTER LOGIC
// ====================
suspend fun performSearch(
    database: AppDatabase,
    day: Int?,
    fromTime: String,
    toTime: String,
    dishIds: List<Int>
): List<SearchResult> {
    return withContext(Dispatchers.IO) {
        val results = mutableListOf<SearchResult>()
        val allRestaurants = database.restaurantDao().getAll()

        for (rest in allRestaurants) {
            // Filter by DAY
            if (day != null) {
                val schedules = database.scheduleDao().getByRestaurant(rest.id)
                val isOpenThatDay = schedules.any { it.day_id == day }
                if (!isOpenThatDay) continue
            }

            // Filter by TIME RANGE
            val hasTimeFilter = fromTime.isNotBlank() || toTime.isNotBlank()

            if (hasTimeFilter && day != null) {
                val schedules = database.scheduleDao().getByRestaurant(rest.id)
                val daySchedule = schedules.find { it.day_id == day } ?: continue

                val fromMin = if (fromTime.isNotBlank()) timeToMinutes(fromTime) else null
                val toMin = if (toTime.isNotBlank()) timeToMinutes(toTime) else null

                if (fromMin == -1) continue
                if (toMin == -1) continue

                val passes = when {
                    fromMin != null && toMin != null -> {
                        daySchedule.start <= toMin && daySchedule.end >= fromMin
                    }
                    fromMin != null -> {
                        daySchedule.end >= fromMin
                    }
                    toMin != null -> {
                        daySchedule.start <= toMin
                    }
                    else -> true
                }

                if (!passes) continue
            }

            // Filter by DISHES
            if (dishIds.isNotEmpty()) {
                val relations = database.restaurantDishDao().getByRestaurant(rest.id)
                val restaurantDishIds = relations.map { it.dish_id }
                val hasAnyDish = dishIds.any { it in restaurantDishIds }
                if (!hasAnyDish) continue
            }

            val schedules = database.scheduleDao().getByRestaurant(rest.id)
            val schedulesText = schedules.joinToString(", ") { s ->
                val dayName = getDayName(s.day_id)
                "$dayName: ${minutesToTime(s.start)}-${minutesToTime(s.end)}"
            }

            val relations = database.restaurantDishDao().getByRestaurant(rest.id)
            val allDishes = database.dishDao().getAll()
            val dishesText = relations.mapNotNull { rel ->
                allDishes.find { it.id == rel.dish_id }?.name
            }.joinToString(", ")

            results.add(
                SearchResult(
                    restaurant = rest,
                    schedulesText = schedulesText,
                    dishesText = dishesText
                )
            )
        }

        results
    }
}

private fun timeToMinutes(timeStr: String): Int {
    return try {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = format.parse(timeStr) ?: return -1

        val calendar = Calendar.getInstance().apply { time = date }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        hour * 60 + minute
    } catch (e: Exception) {
        -1
    }
}

private fun minutesToTime(min: Int): String {
    val h = min / 60
    val m = min % 60
    return String.format(Locale.getDefault(), "%02d:%02d", h, m)
}

private fun getDayName(id: Int): String = when (id) {
    1 -> "Mon"; 2 -> "Tue"; 3 -> "Wed"; 4 -> "Thu"
    5 -> "Fri"; 6 -> "Sat"; 7 -> "Sun"
    else -> "?"
}