package com.ferdev.whattoeatapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ferdev.whattoeatapp.data.RestaurantDish

@Dao
interface RestaurantDishDao {
    @Insert
    suspend fun insert(restaurantDish: RestaurantDish)

    @Insert
    suspend fun insertAll(relationships: List<RestaurantDish>)

    @Query("SELECT * FROM RESTAURANT_DISH WHERE restaurant_id = :restaurantId")
    suspend fun getByRestaurant(restaurantId: Int): List<RestaurantDish>
}