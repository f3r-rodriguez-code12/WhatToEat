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
            database.diaDao().insertAll(dias)

            val platos = listOf(
                Plato(name = "Chicharron"),
                Plato(name = "Charque"),
                Plato(name = "Escabeche"),
                Plato(name = "Planchita"),
                Plato(name = "Huminta"),
                Plato(name = "Pique"),
                Plato(name = "Enrrollado")
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
        }
    }
}