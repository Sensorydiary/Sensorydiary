package com.github.sensorydiary

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addPerson(person: Person)

    @Query("SELECT COUNT(personId) FROM person WHERE personId = :id")
    suspend fun checkIfPersonIdExists(id: Int): Int

    @Query("SELECT COUNT(personName) FROM person WHERE personName = :name")
    suspend fun checkIfPersonNameExists(name: String): Int

    @Query("DELETE FROM person WHERE personName = :name")
    suspend fun deletePerson(name: String)

    @Query("UPDATE person SET personName = :newName WHERE personId = :id")
    suspend fun editPersonName(id: Int, newName: String)

    @Query("SELECT personId FROM person WHERE personName = :name")
    suspend fun getPersonId(name: String): Int

    @Query("SELECT personName FROM person")
    suspend fun returnAllPersons(): List<String>
}