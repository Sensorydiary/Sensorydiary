package com.github.sensorydiary

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StressorDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addStressor(stressor: Stressor)
    
    @Query("SELECT COUNT(stressorName) FROM stressor")
    suspend fun countStressors(): Int
}