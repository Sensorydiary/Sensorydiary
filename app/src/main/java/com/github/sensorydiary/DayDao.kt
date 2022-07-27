package com.github.sensorydiary

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DayDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addDay(day: Day)

    @Query("SELECT COUNT(date) FROM day WHERE date == :date AND personId == :personId")
    suspend fun checkIfDateExists(personId: Int, date: String): Int

    @Query("DELETE FROM day WHERE dayId == :dayId")
    suspend fun deleteDay(dayId: Int)

    @Query("SELECT * FROM day WHERE personId == :personId ORDER BY date DESC")
    suspend fun getAllDays(personId: Int): List<Day>

    @Query("SELECT ROUND(AVG(stressScore), 1) FROM day JOIN dayStressor ON dayStressor.dayId == day.dayId WHERE day.personId == :personId AND dayStressor.stressorId == :stressorId")
    suspend fun getAverageStressScore(personId: Int, stressorId: Int): String?

    @Query("SELECT dayId FROM day WHERE date == :date AND personId == :personId")
    suspend fun getDayId(personId: Int, date: String): Int

    @Query("SELECT notes FROM day WHERE date == :date AND personId == :personId")
    suspend fun getNotes(personId: Int, date: String): String

    @Query("SELECT stressScore FROM day WHERE dayId == :dayId")
    suspend fun getStressScore(dayId: Int): Int

    @Query("UPDATE day SET date = :date, stressScore = :stressScore, notes = :notes WHERE dayId == :dayId")
    suspend fun updateDay(dayId: Int, date: String, stressScore: Int, notes: String)
}