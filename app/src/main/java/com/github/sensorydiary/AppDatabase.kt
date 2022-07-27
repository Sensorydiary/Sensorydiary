package com.github.sensorydiary

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Person::class, Day::class, Stressor::class, DayStressor::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dayDao(): DayDao
    abstract fun dayStressorDao(): DayStressorDao
    abstract fun personDao(): PersonDao
    abstract fun stressorDao(): StressorDao

    companion object {
        private const val DB_NAME = "app_database.db"

        @Volatile
        private var instance: AppDatabase? = null
        suspend fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = buildDatabase(context)
                    instance!!.prepopulateDatabase()
            }
            return instance as AppDatabase
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DB_NAME)
                .build()
    }

    private suspend fun prepopulateDatabase() {
        if (instance!!.stressorDao().countStressors() == 0) {
            val sight = Stressor(1, "Sight")
            instance!!.stressorDao().addStressor(sight)
            val smell = Stressor(2, "Smell")
            instance!!.stressorDao().addStressor(smell)
            val sound = Stressor(3, "Sound")
            instance!!.stressorDao().addStressor(sound)
            val taste = Stressor(4, "Taste")
            instance!!.stressorDao().addStressor(taste)
            val touch = Stressor(5, "Touch")
            instance!!.stressorDao().addStressor(touch)
        }
    }
}