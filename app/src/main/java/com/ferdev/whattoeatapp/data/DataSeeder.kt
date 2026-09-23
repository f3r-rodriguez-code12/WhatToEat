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

            val dishes = listOf(
                Dish(name = "Chicharron"),
                Dish(name = "Charque"),
                Dish(name = "Escabeche"),
                Dish(name = "Planchita"),
                Dish(name = "Huminta"),
                Dish(name = "Pique"),
                Dish(name = "Enrrollado"),
                Dish(name = "Sushi"),
                Dish(name = "Pescado a la parrilla")
            )
            database.dishDao().insertAll(dishes)

            val firstRestaurant = Restaurant(
                name = "Planchitas Originales (El Prado)",
                address = "Av. José Ballivian entre C. La Paz y Oruro",
                phone = "78310109"
            )
            val idFirstRest = database.restaurantDao().insert(firstRestaurant).toInt()

            val secondRestaurant = Restaurant(
                name = "Doña Pola",
                address = "Av. America esq. Av. Gualberto Villarroel",
                phone = "62604567"
            )
            val idSecondRest = database.restaurantDao().insert(secondRestaurant).toInt()

            val horariosFirstRest = listOf(
                Horario(day_id = 2, start = 660, end = 1290, restaurant_id = idFirstRest),
                Horario(day_id = 3, start = 660, end = 1290, restaurant_id = idFirstRest),
                Horario(day_id = 4, start = 660, end = 1290, restaurant_id = idFirstRest),
                Horario(day_id = 5, start = 660, end = 1290, restaurant_id = idFirstRest),
                Horario(day_id = 6, start = 660, end = 1290, restaurant_id = idFirstRest),
                Horario(day_id = 7, start = 660, end = 1290, restaurant_id = idFirstRest)
            )
            database.scheduleDao().insertAll(horariosFirstRest)

            val horariosSecondRest = listOf(
                Horario(day_id = 1, start = 600, end = 1200, restaurant_id = idSecondRest),
                Horario(day_id = 5, start = 720, end = 1200, restaurant_id = idSecondRest),
                Horario(day_id = 6, start = 660, end = 1200, restaurant_id = idSecondRest),
                Horario(day_id = 7, start = 600, end = 1200, restaurant_id = idSecondRest)
            )
            database.scheduleDao().insertAll(horariosSecondRest)

            val firstRestRelationship = listOf(
                RestaurantePlato(restaurant_id = idFirstRest, dish_id = 2),
                RestaurantePlato(restaurant_id = idFirstRest, dish_id = 4),
                RestaurantePlato(restaurant_id = idFirstRest, dish_id = 6)
            )
            database.restaurantDishDao().insertAll(firstRestRelationship)

            val secondRestRelationship = listOf(
                RestaurantePlato(restaurant_id = idSecondRest, dish_id = 1),
                RestaurantePlato(restaurant_id = idSecondRest, dish_id = 3),
                RestaurantePlato(restaurant_id = idSecondRest, dish_id = 5),
                RestaurantePlato(restaurant_id = idSecondRest, dish_id = 7)
            )
            database.restaurantDishDao().insertAll(secondRestRelationship)
        }
    }
}