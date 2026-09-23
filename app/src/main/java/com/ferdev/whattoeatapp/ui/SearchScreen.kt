package com.ferdev.whattoeatapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferdev.whattoeatapp.data.AppDatabase
import com.ferdev.whattoeatapp.data.Dish
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    database: AppDatabase,
    onBack: () -> Unit,
    onSearchResults: (day: Int?, fromTime: String, toTime: String, dishIds: List<Int>) -> Unit
) {
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var fromTime by remember { mutableStateOf("") }
    var toTime by remember { mutableStateOf("") }
    val selectedDishes = remember { mutableStateListOf<Dish>() }
    val availableDishes = remember { mutableStateListOf<Dish>() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val dishes = database.dishDao().getAll()
            withContext(Dispatchers.Main) {
                availableDishes.clear()
                availableDishes.addAll(dishes)
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
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Which day?",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val days = listOf(
                1 to "Mon", 2 to "Tue", 3 to "Wed", 4 to "Thu",
                5 to "Fri", 6 to "Sat", 7 to "Sun"
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                days.forEach { (id, label) ->
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
                text = "What time? (optional)",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimePickerField(
                    value = fromTime,
                    onValueChange = { fromTime = it },
                    label = "From",
                    modifier = Modifier.weight(1f)
                )
                TimePickerField(
                    value = toTime,
                    onValueChange = { toTime = it },
                    label = "To",
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "What do you want to eat? (optional)",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
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
                    onSearchResults(
                        selectedDay,
                        fromTime,
                        toTime,
                        selectedDishes.map { it.id }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SEARCH!", fontSize = 18.sp)
            }
        }
    }
}