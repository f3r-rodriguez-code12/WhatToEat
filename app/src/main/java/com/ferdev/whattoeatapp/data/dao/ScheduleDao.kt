package com.ferdev.whattoeatapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ferdev.whattoeatapp.data.Schedule

@Dao
interface ScheduleDao {
    @Insert
    suspend fun insert(schedule: Schedule)

    @Insert
    suspend fun insertAll(schedules: List<Schedule>)

    @Query("SELECT * FROM HORARIO WHERE restaurant_id = :restaurantId")
    suspend fun getByRestaurant(restaurantId: Int): List<Schedule>
}