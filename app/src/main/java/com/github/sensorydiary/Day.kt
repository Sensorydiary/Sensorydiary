package com.github.sensorydiary

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "day",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["personId"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["personId"])]
)
data class Day(
    @PrimaryKey(autoGenerate = true)
    val dayId: Int?,
    val personId: Int,
    val date: String,
    val stressScore: Int,
    var notes: String
)