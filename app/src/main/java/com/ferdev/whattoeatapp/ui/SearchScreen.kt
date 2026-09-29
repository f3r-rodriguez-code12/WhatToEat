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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferdev.whattoeatapp.data.AppDatabase
import com.ferdev.whattoeatapp.data.Dish
import com.ferdev.whattoeatapp.R
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
                title = { Text(stringResource(R.string.search_title)) },
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
                text = stringResource(R.string.search_day_label),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val days = listOf(
                1 to stringResource(R.string.day_mon),
                2 to stringResource(R.string.day_tue),
                3 to stringResource(R.string.day_wed),
                4 to stringResource(R.string.day_thu),
                5 to stringResource(R.string.day_fri),
                6 to stringResource(R.string.day_sat),
                7 to stringResource(R.string.day_sun)
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
                text = stringResource(R.string.search_time_label),
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
                    label = stringResource(R.string.search_from_label),
                    modifier = Modifier.weight(1f)
                )
                TimePickerField(
                    value = toTime,
                    onValueChange = { toTime = it },
                    label = stringResource(R.string.search_to_label),
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = stringResource(R.string.search_dishes_label),
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
                Text(stringResource(R.string.search_button), fontSize = 18.sp)
            }
        }
    }
}