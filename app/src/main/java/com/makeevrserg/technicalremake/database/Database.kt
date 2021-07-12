package com.makeevrserg.technicalremake.database

import android.content.Context
import androidx.room.*
import androidx.room.Database
import com.makeevrserg.technicalremake.scheduler.JsonParseClasses

@Database(entities = [JsonParseClasses.ProfileFile::class, JsonParseClasses.Profile::class, JsonParseClasses.AdvancedDay::class], version = 14, exportSchema = false)
@TypeConverters(Converter::class)
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