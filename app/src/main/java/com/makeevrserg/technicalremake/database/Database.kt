package com.makeevrserg.technicalremake.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TimeZoneScheduler::class, FileDatabase::class, PlayList::class], version = 14, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract val databaseDao: DatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: com.makeevrserg.technicalremake.database.Database? = null
        fun getInstance(context: Context): com.makeevrserg.technicalremake.database.Database {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.inMemoryDatabaseBuilder(
                        context.applicationContext,
                        com.makeevrserg.technicalremake.database.Database::class.java
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}