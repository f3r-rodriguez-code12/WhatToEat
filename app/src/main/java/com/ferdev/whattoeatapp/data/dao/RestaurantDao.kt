package com.ferdev.whattoeatapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ferdev.whattoeatapp.data.Restaurant

@Dao
interface RestaurantDao {
    @Insert
    suspend fun insert(restaurant: Restaurant): Long

    @Query("SELECT * FROM RESTAURANT")
    suspend fun getAll(): List<Restaurant>
}