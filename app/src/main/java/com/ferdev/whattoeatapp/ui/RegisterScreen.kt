package com.ferdev.whattoeatapp.ui

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
import com.ferdev.whattoeatapp.data.Horario
import com.ferdev.whattoeatapp.data.Plato
import com.ferdev.whattoeatapp.data.Restaurante
import com.ferdev.whattoeatapp.data.RestaurantePlato
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
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
    var restaurantDirection by remember { mutableStateOf("") }
    var restaurantPhone by remember { mutableStateOf("") }
    var startHour by remember { mutableStateOf("") }
    var endHour by remember { mutableStateOf("") }

    val selectedDays = remember { mutableStateListOf<Int>() }

    val availableDishes = remember { mutableStateListOf<Plato>() }
    val selectedDishes = remember { mutableStateListOf<Plato>() }

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
                label = { Text("Nombre del restaurante") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = restaurantDirection,
                onValueChange = { restaurantDirection = it },
                label = { Text("Dirección") },
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
                text = "Días de atención:",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            val dias = listOf(
                1 to "Mon", 2 to "Tue", 3 to "Wed", 4 to "Thu",
                5 to "Fri", 6 to "Sat", 7 to "Sun"
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                dias.forEach { (id, label) ->
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
                text = "Horario de atención (formato HH:MM):",
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
                    label = "Apertura",
                    modifier = Modifier.weight(1f)
                )
                TimePickerField(
                    value = endHour,
                    onValueChange = { endHour = it },
                    label = "Cierre",
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "Platos que sirve:",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                availableDishes.forEach { plato ->
                    FilterChip(
                        selected = selectedDishes.contains(plato),
                        onClick = {
                            if (selectedDishes.contains(plato)) {
                                selectedDishes.remove(plato)
                            } else {
                                selectedDishes.add(plato)
                            }
                        },
                        label = { Text(plato.name) }
                    )
                }
            }

            Button(
                onClick = {
                    if (restaurantName.isBlank() || restaurantDirection.isBlank() || restaurantPhone.isBlank() ||
                        startHour.isBlank() || endHour.isBlank()
                    ) {
                        android.widget.Toast.makeText(context, "Complete all the fields", android.widget.Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (selectedDays.isEmpty()) {
                        android.widget.Toast.makeText(context, "Select at least one day", android.widget.Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (selectedDishes.isEmpty()) {
                        android.widget.Toast.makeText(context, "Select at least one plato", android.widget.Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val startMinutes = hoursToMinutes(startHour)
                    val endMinutes = hoursToMinutes(endHour)

                    if (startMinutes == -1 || endMinutes == -1) {
                        android.widget.Toast.makeText(context, "Formato de hora inválido (usa HH:MM)", android.widget.Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (startMinutes >= endMinutes) {
                        android.widget.Toast.makeText(context, "La hora de 'apertura' debe ser menor a la de 'cierre'", android.widget.Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    scope.launch(Dispatchers.IO) {
                        try {
                            val restaurantId = database.restauranteDao().insert(
                                Restaurante(name = restaurantName, address = restaurantDirection, phone = restaurantPhone)
                            ).toInt()

                            selectedDays.forEach { diaId ->
                                database.horarioDao().insert(
                                    Horario(dia_id = diaId, start = startMinutes, end = endMinutes, restaurant_id = restaurantId)
                                )
                            }

                            selectedDishes.forEach { plato ->
                                database.restaurantePlatoDao().insert(
                                    RestaurantePlato(restaurant_id = restaurantId, plato_id = plato.id)
                                )
                            }

                            withContext(Dispatchers.Main) {
                                android.widget.Toast.makeText(context, "¡Restaurante guardado!", android.widget.Toast.LENGTH_LONG).show()

                                restaurantName = ""
                                restaurantDirection = ""
                                restaurantPhone = ""
                                startHour = ""
                                endHour = ""
                                selectedDays.clear()
                                selectedDishes.clear()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                android.widget.Toast.makeText(context, "Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Restaurante")
            }
        }
    }
}

private fun hoursToMinutes(horaStr: String): Int {
    return try {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = format.parse(horaStr) ?: return -1
        date.hours * 60 + date.minutes
    } catch (e: Exception) {
        -1
    }
}