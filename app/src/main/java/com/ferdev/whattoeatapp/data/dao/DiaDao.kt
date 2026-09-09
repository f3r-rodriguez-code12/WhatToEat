package com.ferdev.whattoeatapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ferdev.whattoeatapp.data.Dia

@Dao
interface DiaDao {
    @Insert
    suspend fun insert(dia: Dia)

    @Query("SELECT * FROM DIA")
    suspend fun getAll(): List<Dia>
}