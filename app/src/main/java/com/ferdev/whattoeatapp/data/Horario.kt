package com.ferdev.whattoeatapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "HORARIO",
    foreignKeys = [
        ForeignKey(
            entity = Restaurante::class,
            parentColumns = ["id"],
            childColumns = ["restaurant_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Dia::class,
            parentColumns = ["id"],
            childColumns = ["dia_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Horario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dia_id: Int,
    val start: Int,  // Hora en minutos (ej. 600 = 10:00am)
    val end: Int,    // Hora en minutos (ej. 1080 = 6:00pm)
    val restaurant_id: Int
)
