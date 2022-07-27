package com.github.sensorydiary

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "dayStressor",
    foreignKeys = [
        ForeignKey(
            entity = Day::class,
            parentColumns = ["dayId"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Stressor::class,
            parentColumns = ["stressorId"],
            childColumns = ["stressorId"]
        )
    ],
    primaryKeys = ["dayId", "stressorId"],
    indices = [Index(value = ["stressorId"])]
)
data class DayStressor(
    val dayId: Int,
    val stressorId: Int
)