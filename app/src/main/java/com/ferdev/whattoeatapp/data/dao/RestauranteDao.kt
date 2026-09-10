package com.ferdev.whattoeatapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ferdev.whattoeatapp.data.Restaurante

@Dao
interface RestauranteDao {
    @Insert
    suspend fun insert(restaurante: Restaurante): Long

    @Query("SELECT * FROM RESTAURANT")
    suspend fun getAll(): List<Restaurante>
}