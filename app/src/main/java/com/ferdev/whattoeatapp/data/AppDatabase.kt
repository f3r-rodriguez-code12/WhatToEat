package com.ferdev.whattoeatapp.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.ferdev.whattoeatapp.data.dao.DayDao
import com.ferdev.whattoeatapp.data.dao.HorarioDao
import com.ferdev.whattoeatapp.data.dao.PlatoDao
import com.ferdev.whattoeatapp.data.dao.RestauranteDao
import com.ferdev.whattoeatapp.data.dao.RestaurantePlatoDao

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
    abstract fun dayDao(): DayDao
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