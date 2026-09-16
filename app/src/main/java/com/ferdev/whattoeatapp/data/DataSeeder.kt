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
        }
    }
}