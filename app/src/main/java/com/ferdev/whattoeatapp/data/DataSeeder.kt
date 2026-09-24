package com.ferdev.whattoeatapp.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataSeeder(private val database: AppDatabase) {
    fun seedData() {
        CoroutineScope(Dispatchers.IO).launch {
            val days = listOf(
                Day(1, "Mon"),
                Day(2, "Tue"),
                Day(3, "Wed"),
                Day(4, "Thu"),
                Day(5, "Fri"),
                Day(6, "Sat"),
                Day(7, "Sun")
            )
            database.dayDao().insertAll(days)

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

            val horariosFirstRests = listOf(
                Schedule(day_id = 2, start = 660, end = 1290, restaurant_id = idFirstRest),
                Schedule(day_id = 3, start = 660, end = 1290, restaurant_id = idFirstRest),
                Schedule(day_id = 4, start = 660, end = 1290, restaurant_id = idFirstRest),
                Schedule(day_id = 5, start = 660, end = 1290, restaurant_id = idFirstRest),
                Schedule(day_id = 6, start = 660, end = 1290, restaurant_id = idFirstRest),
                Schedule(day_id = 7, start = 660, end = 1290, restaurant_id = idFirstRest)
            )
            database.scheduleDao().insertAll(horariosFirstRests)

            val horariosSecondRests = listOf(
                Schedule(day_id = 1, start = 600, end = 1200, restaurant_id = idSecondRest),
                Schedule(day_id = 5, start = 720, end = 1200, restaurant_id = idSecondRest),
                Schedule(day_id = 6, start = 660, end = 1200, restaurant_id = idSecondRest),
                Schedule(day_id = 7, start = 600, end = 1200, restaurant_id = idSecondRest)
            )
            database.scheduleDao().insertAll(horariosSecondRests)

            val firstRestRelationship = listOf(
                RestaurantDish(restaurant_id = idFirstRest, dish_id = 2),
                RestaurantDish(restaurant_id = idFirstRest, dish_id = 4),
                RestaurantDish(restaurant_id = idFirstRest, dish_id = 6)
            )
            database.restaurantDishDao().insertAll(firstRestRelationship)

            val secondRestRelationship = listOf(
                RestaurantDish(restaurant_id = idSecondRest, dish_id = 1),
                RestaurantDish(restaurant_id = idSecondRest, dish_id = 3),
                RestaurantDish(restaurant_id = idSecondRest, dish_id = 5),
                RestaurantDish(restaurant_id = idSecondRest, dish_id = 7)
            )
            database.restaurantDishDao().insertAll(secondRestRelationship)
        }
    }
}