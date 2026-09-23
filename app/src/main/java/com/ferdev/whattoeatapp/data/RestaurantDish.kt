package com.ferdev.whattoeatapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "RESTAURANT_PLATO",
    foreignKeys = [
        ForeignKey(
            entity = Restaurant::class,
            parentColumns = ["id"],
            childColumns = ["restaurant_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Dish::class,
            parentColumns = ["id"],
            childColumns = ["plato_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RestaurantDish(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val restaurant_id: Int,
    val dish_id: Int
)
