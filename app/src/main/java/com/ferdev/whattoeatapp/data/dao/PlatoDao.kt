package com.ferdev.whattoeatapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ferdev.whattoeatapp.data.Plato

@Dao
interface PlatoDao {
    @Insert
    suspend fun insert(plato: Plato)

    @Insert
    suspend fun insertAll(platos: List<Plato>)

    @Query("SELECT * FROM PLATO")
    suspend fun getAll(): List<Plato>
}