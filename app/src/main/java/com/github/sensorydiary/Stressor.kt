package com.github.sensorydiary

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stressor")
data class Stressor(
    @PrimaryKey
    val stressorId: Int,
    val stressorName: String
)