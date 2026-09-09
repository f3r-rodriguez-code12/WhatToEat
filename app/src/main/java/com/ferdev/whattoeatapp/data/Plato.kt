package com.ferdev.whattoeatapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PLATO")
data class Plato(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)