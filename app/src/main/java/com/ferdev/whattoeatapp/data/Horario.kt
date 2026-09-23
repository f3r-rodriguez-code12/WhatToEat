package com.ferdev.whattoeatapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "HORARIO",
    foreignKeys = [
        ForeignKey(
            entity = Restaurant::class,
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
    val day_id: Int,
    val start: Int,  // Hora en minutos (ej. 600 = 10:00am)
    val end: Int,    // Hora en minutos (ej. 1080 = 6:00pm)
    val restaurant_id: Int
)

/*
    Minutes examples:
        00:00 = 0           00:30 = 30          01:00 = 60          01:30 = 90          02:00 = 120
        02:30 = 150         03:00 = 180         03:30 = 210         04:00 = 240         04:30 = 270
        05:00 = 300         05:30 = 330         06:00 = 360         06:30 = 390         07:00 = 420
        07:30 = 450         08:00 = 480         08:30 = 510         09:00 = 540         09:30 = 570
        10:00 = 600         10:30 = 630         11:00 = 660         11:30 = 690         12:00 = 720
        12:30 = 750         13:00 = 780         13:30 = 810         14:00 = 840         14:30 = 870
        15:00 = 900         15:30 = 930         16:00 = 960         16:30 = 990         17:00 = 1020
        17:30 = 1050        18:00 = 1080        18:30 = 1110        19:00 = 1140        19:30 = 1170
        20:00 = 1200        20:30 = 1230        21:00 = 1260        21:30 = 1290        22:00 = 1320
        22:30 = 1350        23:00 = 1380        23:30 = 1410
 */
