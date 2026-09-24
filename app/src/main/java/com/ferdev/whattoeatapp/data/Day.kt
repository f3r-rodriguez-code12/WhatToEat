package com.ferdev.whattoeatapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DIA")
data class Day(
    @PrimaryKey
    val id: Int,
    val name: String
)