package com.ferdev.whattoeatapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferdev.whattoeatapp.data.AppDatabase
import com.ferdev.whattoeatapp.data.Plato
import com.ferdev.whattoeatapp.data.Restaurante
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

data class SearchResult(
    val restaurante: Restaurante,
    val horariosText: String,
    val platosText: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BusquedaScreen(
    database: AppDatabase,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var desdeTime by remember { mutableStateOf("") }
    var hastaTime by remember { mutableStateOf("") }
    val selectedDishes = remember { mutableStateListOf<Plato>() }
    val availableDishes = remember { mutableStateListOf<Plato>() }

    var resultados by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    var searchDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val platos = database.platoDao().getAll()
            withContext(Dispatchers.Main) {
                availableDishes.clear()
                availableDishes.addAll(platos)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Restaurants") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Which dia?",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val dias = listOf(
                    1 to "Mon", 2 to "Tue", 3 to "Wed", 4 to "Thu",
                    5 to "Fri", 6 to "Sat", 7 to "Sun"
                )

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    dias.forEach { (id, label) ->
                        FilterChip(
                            selected = selectedDay == id,
                            onClick = {
                                selectedDay = if (selectedDay == id) null else id
                            },
                            label = { Text(label) }
                        )
                    }
                }

                Text(
                    text = "What hora? (optional)",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = desdeTime,
                        onValueChange = { desdeTime = it },
                        label = { Text("Desde") },
                        placeholder = { Text("11:00") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = hastaTime,
                        onValueChange = { hastaTime = it },
                        label = { Text("Hasta") },
                        placeholder = { Text("16:00") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Text(
                    text = "What do you quieres to comer? (optional)",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    availableDishes.forEach { dish ->
                        FilterChip(
                            selected = selectedDishes.contains(dish),
                            onClick = {
                                if (selectedDishes.contains(dish)) {
                                    selectedDishes.remove(dish)
                                } else {
                                    selectedDishes.add(dish)
                                }
                            },
                            label = { Text(dish.name) }
                        )
                    }
                }

                Button(
                    onClick = {
                        scope.launch {
                            resultados = performSearch(
                                database = database,
                                day = selectedDay,
                                desdeTime = desdeTime,
                                hastaTime = hastaTime,
                                platos = selectedDishes.toList()
                            )
                            searchDone = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SEARCH!", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (searchDone) {
                Text(
                    text = "Results (${resultados.size}):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (resultados.isEmpty()) {
                    Text(
                        text = "😔 No restaurantes found con esos filters!",
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
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
    platos: List<Plato>
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
            if (desdeTime.isNotBlank() && hastaTime.isNotBlank() && day != null) {
                val fromMin = timeToMinutes(desdeTime)
                val toMin = timeToMinutes(hastaTime)
                if (fromMin == -1 || toMin == -1) continue

                val schedules = database.horarioDao().getByRestaurant(rest.id)
                val daySchedule = schedules.find { it.dia_id == day } ?: continue

                val overlaps = daySchedule.start <= toMin && daySchedule.end >= fromMin
                if (!overlaps) continue
            }

            // Filter by PLATOS
            if (platos.isNotEmpty()) {
                val relations = database.restaurantePlatoDao().getByRestaurant(rest.id)
                val restaurantDishIds = relations.map { it.plato_id }
                val searchedDishIds = platos.map { it.id }
                val hasAnyDish = searchedDishIds.any { it in restaurantDishIds }
                if (!hasAnyDish) continue
            }

            val schedules = database.horarioDao().getByRestaurant(rest.id)
            val horariosText = schedules.joinToString(", ") { s ->
                val dayName = getDayName(s.dia_id)
                "${dayName}: ${minutesToTime(s.start)}-${minutesToTime(s.end)}"
            }

            val relations = database.restaurantePlatoDao().getByRestaurant(rest.id)
            val allDishes = database.platoDao().getAll()
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