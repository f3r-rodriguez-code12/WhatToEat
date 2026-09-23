package com.ferdev.whattoeatapp.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ferdev.whattoeatapp.data.AppDatabase
import com.ferdev.whattoeatapp.data.Schedule
import com.ferdev.whattoeatapp.data.Dish
import com.ferdev.whattoeatapp.data.Restaurant
import com.ferdev.whattoeatapp.data.RestaurantDish
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(
    database: AppDatabase,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var restaurantName by remember { mutableStateOf("") }
    var restaurantAddress by remember { mutableStateOf("") }
    var restaurantPhone by remember { mutableStateOf("") }
    var startHour by remember { mutableStateOf("") }
    var endHour by remember { mutableStateOf("") }

    val selectedDays = remember { mutableStateListOf<Int>() }

    val availableDishes = remember { mutableStateListOf<Dish>() }
    val selectedDishes = remember { mutableStateListOf<Dish>() }

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
                title = { Text("Register Restaurant") },
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = restaurantName,
                onValueChange = { restaurantName = it },
                label = { Text("Restaurant name") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = restaurantAddress,
                onValueChange = { restaurantAddress = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = restaurantPhone,
                onValueChange = { restaurantPhone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                singleLine = true
            )

            Text(
                text = "Operating days:",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            val days = listOf(
                1 to "Mon", 2 to "Tue", 3 to "Wed", 4 to "Thu",
                5 to "Fri", 6 to "Sat", 7 to "Sun"
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                days.forEach { (id, label) ->
                    FilterChip(
                        selected = selectedDays.contains(id),
                        onClick = {
                            if (selectedDays.contains(id)) {
                                selectedDays.remove(id)
                            } else {
                                selectedDays.add(id)
                            }
                        },
                        label = { Text(label) }
                    )
                }
            }

            Text(
                text = "Operating hours (HH:MM format):",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimePickerField(
                    value = startHour,
                    onValueChange = { startHour = it },
                    label = "Opening",
                    modifier = Modifier.weight(1f)
                )
                TimePickerField(
                    value = endHour,
                    onValueChange = { endHour = it },
                    label = "Closing",
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "Served dishes:",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
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
                    if (restaurantName.isBlank() || restaurantAddress.isBlank() || restaurantPhone.isBlank() ||
                        startHour.isBlank() || endHour.isBlank()
                    ) {
                        Toast.makeText(context, "Complete all the fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (selectedDays.isEmpty()) {
                        Toast.makeText(context, "Select at least one day", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (selectedDishes.isEmpty()) {
                        Toast.makeText(context, "Select at least one dish", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val startMinutes = hoursToMinutes(startHour)
                    val endMinutes = hoursToMinutes(endHour)

                    if (startMinutes == -1 || endMinutes == -1) {
                        Toast.makeText(context, "Invalid time format (use HH:MM)", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (startMinutes >= endMinutes) {
                        Toast.makeText(context, "Opening time must be earlier than closing time", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    scope.launch(Dispatchers.IO) {
                        try {
                            val restaurantId = database.restaurantDao().insert(
                                Restaurant(name = restaurantName, address = restaurantAddress, phone = restaurantPhone)
                            ).toInt()

                            selectedDays.forEach { dayId ->
                                database.scheduleDao().insert(
                                    Schedule(day_id = dayId, start = startMinutes, end = endMinutes, restaurant_id = restaurantId)
                                )
                            }

                            selectedDishes.forEach { dish ->
                                database.restaurantDishDao().insert(
                                    RestaurantDish(restaurant_id = restaurantId, dish_id = dish.id)
                                )
                            }

                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Restaurant saved successfully!", Toast.LENGTH_LONG).show()

                                restaurantName = ""
                                restaurantAddress = ""
                                restaurantPhone = ""
                                startHour = ""
                                endHour = ""
                                selectedDays.clear()
                                selectedDishes.clear()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Restaurant")
            }
        }
    }
}

private fun hoursToMinutes(hourStr: String): Int {
    return try {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = format.parse(hourStr) ?: return -1

        val calendar = Calendar.getInstance().apply { time = date }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        hour * 60 + minute
    } catch (e: Exception) {
        -1
    }
}