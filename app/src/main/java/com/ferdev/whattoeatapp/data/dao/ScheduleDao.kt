package com.ferdev.whattoeatapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ferdev.whattoeatapp.data.Horario

@Dao
interface ScheduleDao {
    @Insert
    suspend fun insert(horario: Horario)

    @Insert
    suspend fun insertAll(horarios: List<Horario>)

    @Query("SELECT * FROM HORARIO WHERE restaurant_id = :restaurantId")
    suspend fun getByRestaurant(restaurantId: Int): List<Horario>
}