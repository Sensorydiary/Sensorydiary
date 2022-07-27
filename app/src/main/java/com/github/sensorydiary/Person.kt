package com.github.sensorydiary

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "person",
    indices = [Index(value = ["personName"], unique = true)]
)
data class Person(
    @PrimaryKey(autoGenerate = true)
    val personId: Int?,
    val personName: String
)