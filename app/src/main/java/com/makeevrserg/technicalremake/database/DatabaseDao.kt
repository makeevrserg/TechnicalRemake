package com.makeevrserg.technicalremake.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.makeevrserg.technicalremake.scheduler.JsonParseClasses
import com.makeevrserg.technicalremake.scheduler.JsonParseClasses.ProfileFile

@Dao
interface DatabaseDao {


    //Profile
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProfile(fileDatabase: ProfileFile)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProfile(fileDatabase: List<ProfileFile>)
    @Update
    suspend fun updateProfile(profile:JsonParseClasses.Profile)

    //Files
    @Update
    suspend fun fileUpdate(fileDatabase: ProfileFile)
    @Query("SELECT * FROM files WHERE id=:key")
    suspend fun getFile(key: Long): ProfileFile?
    @Query("SELECT * FROM files ")
    suspend fun getFiles(): List<ProfileFile>
    @Query("SELECT file_name from files WHERE id=:key and isBroken=0")
    suspend fun getFileName(key: Long): String

    //Profile
    @Insert
    fun insertProfile(profile: JsonParseClasses.Profile)
    @Query("SELECT * FROM profile")
    suspend fun getProfile(): JsonParseClasses.Profile


    //AdvancedDays
    @Query("SELECT * FROM timings")
    fun getAdvancedDayLiveData(): LiveData<List<JsonParseClasses.AdvancedDay>>
    @Query("UPDATE timings set isBroken=:isBroken WHERE playlistId=:id")
    suspend fun updateBrokenAdvancedDayByPlaylistID(isBroken: Boolean, id: Long)
    @Query("SELECT * FROM timings")
    suspend fun getAdvancedDays(): List<JsonParseClasses.AdvancedDay>
    @Insert
    fun insertAdvancedDay(days: JsonParseClasses.AdvancedDay)
    @Insert
    fun insertAdvancedDay(days: List<JsonParseClasses.AdvancedDay>)
    @Update
    fun updateAdvancedDay(day: JsonParseClasses.AdvancedDay)

}