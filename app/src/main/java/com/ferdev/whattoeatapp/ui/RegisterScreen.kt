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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ferdev.whattoeatapp.data.AppDatabase
import com.ferdev.whattoeatapp.data.Schedule
import com.ferdev.whattoeatapp.data.Dish
import com.ferdev.whattoeatapp.data.Restaurant
import com.ferdev.whattoeatapp.data.RestaurantDish
import com.ferdev.whattoeatapp.R
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
                title = { Text(stringResource(R.string.register_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back)
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
                label = { Text(stringResource(R.string.register_name_label)) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = restaurantAddress,
                onValueChange = { restaurantAddress = it },
                label = { Text(stringResource(R.string.register_address_label)) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = restaurantPhone,
                onValueChange = { restaurantPhone = it },
                label = { Text(stringResource(R.string.register_phone_label)) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                singleLine = true
            )

            Text(
                text = stringResource(R.string.register_days_label),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
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
                text = stringResource(R.string.register_hours_label),
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
                    label = stringResource(R.string.register_opens_label),
                    modifier = Modifier.weight(1f)
                )
                TimePickerField(
                    value = endHour,
                    onValueChange = { endHour = it },
                    label = stringResource(R.string.register_closes_label),
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = stringResource(R.string.register_dishes_label),
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

            val errorFieldsText = stringResource(id = R.string.register_error_fields)
            val errorDaysText = stringResource(id = R.string.register_error_days)
            val errorDishesText = stringResource(id = R.string.register_error_dishes)
            val errorTimeFormatText = stringResource(id = R.string.register_error_time_format)
            val errorTimeOrderText = stringResource(id = R.string.register_error_time_order)
            val successText = stringResource(id = R.string.register_success)

            Button(
                onClick = {
                    if (restaurantName.isBlank() || restaurantAddress.isBlank() || restaurantPhone.isBlank() ||
                        startHour.isBlank() || endHour.isBlank()
                    ) {
                        Toast.makeText(context, errorFieldsText, Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (selectedDays.isEmpty()) {
                        Toast.makeText(context, errorDaysText, Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (selectedDishes.isEmpty()) {
                        Toast.makeText(context, errorDishesText, Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val startMinutes = hoursToMinutes(startHour)
                    val endMinutes = hoursToMinutes(endHour)

                    if (startMinutes == -1 || endMinutes == -1) {
                        Toast.makeText(context, errorTimeFormatText, Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (startMinutes >= endMinutes) {
                        Toast.makeText(context, errorTimeOrderText, Toast.LENGTH_SHORT).show()
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
                                Toast.makeText(context, successText, Toast.LENGTH_LONG).show()

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
                Text(stringResource(R.string.register_save_button))
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