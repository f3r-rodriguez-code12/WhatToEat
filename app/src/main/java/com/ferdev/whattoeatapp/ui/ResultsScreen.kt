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
import com.ferdev.whattoeatapp.data.Restaurante
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

data class SearchResult(
    val restaurante: Restaurante,
    val horariosText: String,
    val platosText: String
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
    var resultados by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(day, fromTime, toTime, dishIds) {
        isLoading = true
        resultados = performSearch(database, day, fromTime, toTime, dishIds)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultados (${resultados.size})") },
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
                resultados.isEmpty() -> {
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
                            text = "No restaurantes found",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try different filtros!",
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
                        items(resultados) { result ->
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
                text = result.restaurante.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "📍 ${result.restaurante.address}", fontSize = 14.sp)
            Text(text = "📞 ${result.restaurante.phone}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "🕐 ${result.horariosText}", fontSize = 14.sp)
            Text(text = "🍽️ ${result.platosText}", fontSize = 14.sp)
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
    desdeTime: String,
    hastaTime: String,
    platosIds: List<Int>
): List<SearchResult> {
    return withContext(Dispatchers.IO) {
        val results = mutableListOf<SearchResult>()
        val allRestaurants = database.restauranteDao().getAll()

        for (rest in allRestaurants) {
            // Filter by DIA
            if (day != null) {
                val schedules = database.horarioDao().getByRestaurant(rest.id)
                val isOpenThatDay = schedules.any { it.dia_id == day }
                if (!isOpenThatDay) continue
            }

            // Filter by TIME RANGE
            val hasTimeFilter = desdeTime.isNotBlank() || hastaTime.isNotBlank()

            if (hasTimeFilter && day != null) {
                val schedules = database.horarioDao().getByRestaurant(rest.id)
                val daySchedule = schedules.find { it.dia_id == day } ?: continue

                val fromMin = if (desdeTime.isNotBlank()) timeToMinutes(desdeTime) else null
                val toMin = if (hastaTime.isNotBlank()) timeToMinutes(hastaTime) else null

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

            // Filter by PLATOS
            if (platosIds.isNotEmpty()) {
                val relations = database.restaurantePlatoDao().getByRestaurant(rest.id)
                val restaurantDishIds = relations.map { it.plato_id }
                val hasAnyDish = platosIds.any { it in restaurantDishIds }
                if (!hasAnyDish) continue
            }

            val schedules = database.horarioDao().getByRestaurant(rest.id)
            val horariosText = schedules.joinToString(", ") { s ->
                val dayName = getDayName(s.dia_id)
                "${dayName}: ${minutesToTime(s.start)}-${minutesToTime(s.end)}"
            }

            val relations = database.restaurantePlatoDao().getByRestaurant(rest.id)
            val allDishes = database.dishDao().getAll()
            val platosText = relations.mapNotNull { rel ->
                allDishes.find { it.id == rel.plato_id }?.name
            }.joinToString(", ")

            results.add(
                SearchResult(
                    restaurante = rest,
                    horariosText = horariosText,
                    platosText = platosText
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
        date.hours * 60 + date.minutes
    } catch (e: Exception) {
        -1
    }
}

private fun minutesToTime(min: Int): String {
    val h = min / 60
    val m = min % 60
    return String.format("%02d:%02d", h, m)
}

private fun getDayName(id: Int): String = when (id) {
    1 -> "Mon"; 2 -> "Tue"; 3 -> "Wed"; 4 -> "Thu"
    5 -> "Fri"; 6 -> "Sat"; 7 -> "Sun"
    else -> "?"
}