package com.ferdev.whattoeatapp.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataSeeder(private val database: AppDatabase) {
    fun seedData() {
        CoroutineScope(Dispatchers.IO).launch {
            val dias = listOf(
                Dia(1, "Mon"),
                Dia(2, "Tue"),
                Dia(3, "Wed"),
                Dia(4, "Thu"),
                Dia(5, "Fri"),
                Dia(6, "Sat"),
                Dia(7, "Sun")
            )
            database.dayDao().insertAll(dias)

            val platos = listOf(
                Plato(name = "Chicharron"),
                Plato(name = "Charque"),
                Plato(name = "Escabeche"),
                Plato(name = "Planchita"),
                Plato(name = "Huminta"),
                Plato(name = "Pique"),
                Plato(name = "Enrrollado"),
                Plato(name = "Sushi"),
                Plato(name = "Pescado a la parrilla")
            )
            database.platoDao().insertAll(platos)

            val firstRestaurant = Restaurante(
                name = "Planchitas Originales (El Prado)",
                address = "Av. José Ballivian entre C. La Paz y Oruro",
                phone = "78310109"
            )
            val idFirstRest = database.restauranteDao().insert(firstRestaurant).toInt()

            val secondRestaurant = Restaurante(
                name = "Doña Pola",
                address = "Av. America esq. Av. Gualberto Villarroel",
                phone = "62604567"
            )
            val idSecondRest = database.restauranteDao().insert(secondRestaurant).toInt()

            val horariosFirstRest = listOf(
                Horario(dia_id = 2, start = 660, end = 1290, restaurant_id = idFirstRest),
                Horario(dia_id = 3, start = 660, end = 1290, restaurant_id = idFirstRest),
                Horario(dia_id = 4, start = 660, end = 1290, restaurant_id = idFirstRest),
                Horario(dia_id = 5, start = 660, end = 1290, restaurant_id = idFirstRest),
                Horario(dia_id = 6, start = 660, end = 1290, restaurant_id = idFirstRest),
                Horario(dia_id = 7, start = 660, end = 1290, restaurant_id = idFirstRest)
            )
            database.horarioDao().insertAll(horariosFirstRest)

            val horariosSecondRest = listOf(
                Horario(dia_id = 1, start = 600, end = 1200, restaurant_id = idSecondRest),
                Horario(dia_id = 5, start = 720, end = 1200, restaurant_id = idSecondRest),
                Horario(dia_id = 6, start = 660, end = 1200, restaurant_id = idSecondRest),
                Horario(dia_id = 7, start = 600, end = 1200, restaurant_id = idSecondRest)
            )
            database.horarioDao().insertAll(horariosSecondRest)

            val firstRestRelationship = listOf(
                RestaurantePlato(restaurant_id = idFirstRest, plato_id = 2),
                RestaurantePlato(restaurant_id = idFirstRest, plato_id = 4),
                RestaurantePlato(restaurant_id = idFirstRest, plato_id = 6)
            )
            database.restaurantePlatoDao().insertAll(firstRestRelationship)

            val secondRestRelationship = listOf(
                RestaurantePlato(restaurant_id = idSecondRest, plato_id = 1),
                RestaurantePlato(restaurant_id = idSecondRest, plato_id = 3),
                RestaurantePlato(restaurant_id = idSecondRest, plato_id = 5),
                RestaurantePlato(restaurant_id = idSecondRest, plato_id = 7)
            )
            database.restaurantePlatoDao().insertAll(secondRestRelationship)
        }
    }
}