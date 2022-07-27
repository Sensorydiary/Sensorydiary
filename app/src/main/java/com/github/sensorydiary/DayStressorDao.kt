package com.github.sensorydiary

import androidx.room.*

@Dao
interface DayStressorDao {
    @Query("SELECT dayStressor.dayId, group_concat(stressor.stressorName, ', ') AS stressors FROM dayStressor JOIN day ON dayStressor.dayId = day.dayId JOIN stressor ON dayStressor.stressorId = stressor.stressorId WHERE personId = :personId GROUP BY dayStressor.dayId")
    suspend fun getAllStressors(personId: Int) : List<DayIdStressor>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addDayStressor(stressor: DayStressor)

    @Query("DELETE FROM dayStressor WHERE dayId == :dayId")
    suspend fun deleteOldStressors(dayId: Int)

    @Query("SELECT stressorId FROM dayStressor WHERE dayId == :dayId")
    suspend fun getStressors(dayId: Int): Array<Int>
}