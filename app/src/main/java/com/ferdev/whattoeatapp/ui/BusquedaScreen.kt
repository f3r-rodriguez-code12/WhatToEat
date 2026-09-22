package com.ferdev.whattoeatapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ferdev.whattoeatapp.data.AppDatabase
import com.ferdev.whattoeatapp.data.Plato
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BusquedaScreen(
    database: AppDatabase,
    onBack: () -> Unit
) {
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var desdeTime by remember { mutableStateOf("") }
    var hastaTime by remember { mutableStateOf("") }
    val selectedDishes = remember { mutableStateListOf<Plato>() }
    val availableDishes = remember { mutableStateListOf<Plato>() }

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

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}