package com.ferdev.whattoeatapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ferdev.whattoeatapp.data.Dish

@Dao
interface DishDao {
    @Insert
    suspend fun insert(dish: Dish)

    @Insert
    suspend fun insertAll(dishes: List<Dish>)

    @Query("SELECT * FROM PLATO")
    suspend fun getAll(): List<Dish>
}