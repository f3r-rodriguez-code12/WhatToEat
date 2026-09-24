package com.ferdev.whattoeatapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RESTAURANT")
data class Restaurant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val address: String,
    val phone: String
)