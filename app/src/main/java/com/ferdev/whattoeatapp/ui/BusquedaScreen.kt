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
import kotlinx.coroutines.launch

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
                        }
                    }
                }
            }
        }
    }
}