package com.ferdev.whattoeatapp.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.ferdev.whattoeatapp.data.dao.DayDao
import com.ferdev.whattoeatapp.data.dao.ScheduleDao
import com.ferdev.whattoeatapp.data.dao.DishDao
import com.ferdev.whattoeatapp.data.dao.RestaurantDao
import com.ferdev.whattoeatapp.data.dao.RestaurantDishDao

@Database(
    entities = [
        Day::class,
        Dish::class,
        Restaurant::class,
        Schedule::class,
        RestaurantDish::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dayDao(): DayDao
    abstract fun dishDao(): DishDao
    abstract fun restaurantDao(): RestaurantDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun restaurantDishDao(): RestaurantDishDao

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