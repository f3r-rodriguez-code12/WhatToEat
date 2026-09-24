package com.ferdev.whattoeatapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ferdev.whattoeatapp.data.Day

@Dao
interface DayDao {
    @Insert
    suspend fun insert(day: Day)

    @Insert
    suspend fun insertAll(days: List<Day>)

    @Query("SELECT * FROM DAY")
    suspend fun getAll(): List<Day>

    @Query("SELECT COUNT(*) FROM DAY")
    suspend fun getCount(): Int
}