package com.ferdev.whattoeatapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ferdev.whattoeatapp.data.RestaurantePlato

@Dao
interface RestaurantDishDao {
    @Insert
    suspend fun insert(restaurantePlato: RestaurantePlato)

    @Insert
    suspend fun insertAll(relationships: List<RestaurantePlato>)

    @Query("SELECT * FROM RESTAURANT_PLATO WHERE restaurant_id = :restaurantId")
    suspend fun getByRestaurant(restaurantId: Int): List<RestaurantePlato>
}