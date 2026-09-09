package com.ferdev.whattoeatapp.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [
        Dia::class,
        Plato::class,
        Restaurante::class,
        Horario::class,
        RestaurantePlato::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diaDao(): DiaDao
    abstract fun platoDao(): PlatoDao
    abstract fun restauranteDao(): RestauranteDao
    abstract fun horarioDao(): HorarioDao
    abstract fun restaurantePlatoDao(): RestaurantePlatoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "restaurant_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}