package com.makeevrserg.technicalremake.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.makeevrserg.technicalremake.scheduler.JsonParseClasses
import kotlinx.coroutines.DisposableHandle

@Dao
interface DatabaseDao {

    //TimeZone
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun timeZoneInsert(timeZoneScheduler: TimeZoneScheduler)
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun timeZoneInsert(timeZoneScheduler: List<TimeZoneScheduler>)
//
//    @Update
//    suspend fun timeZoneUpdate(timeZoneScheduler: TimeZoneScheduler)

//    @Query("UPDATE timezones set is_broken=:isBroken WHERE playlist_id=:id")
//    suspend fun updateTimezoneBrokentByPlaylistId(isBroken: Boolean, id: Long)
//
//    @Query("SELECT timeZoneId,`from`,`to`,day,playlist_name,playlist_id,sum(proportion) as proportion,is_broken,show_day FROM timezones WHERE :day=day AND :time BETWEEN `from` AND `to` AND proportion IS NOT NULL GROUP BY playlist_id ORDER BY proportion DESC")
//    fun getTimezonesByTime(day: String, time: String): List<TimeZoneScheduler>
//
//    @Query("SELECT * FROM timezones ORDER BY timeZoneId ASC")
//    fun getTimeZones(): LiveData<List<TimeZoneScheduler>>


    //PlayList
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun playlistInsert(playList: PlayList)
//
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun playlistInsert(playList: List<PlayList>)
//
//    @Update
//    suspend fun playlistUpdate(playlistDatabase: PlayList)

//    @Query("UPDATE playlist_table set is_broken=:isBroken WHERE id_music=:id")
//    suspend fun updateBrokenPlaylisByMusicID(isBroken: Boolean, id: Long)
//
//    @Query("SELECT playlist_id FROM playlist_table WHERE id_music=:id")
//    suspend fun getPlaylistsByMusicId(id: Long): List<Long>
//
//    @Query("SELECT DISTINCT playlist_name FROM playlist_table WHERE playlist_id=:id")
//    suspend fun getPlaylistNameByPlaylistId(id: Long): String
//
//
//    @Query("SELECT * FROM playlist_table ORDER BY playlist_id ASC")
//    suspend fun getPlaylists(): List<PlayList>


    //Files
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun fileInsert(fileDatabase: JsonParseClasses.ProfileFile)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun fileInsert(fileDatabase: List<JsonParseClasses.ProfileFile>)

    @Update
    suspend fun fileUpdate(fileDatabase: JsonParseClasses.ProfileFile)

    @Query("SELECT * FROM files WHERE id=:key")
    suspend fun getFile(key: Long): JsonParseClasses.ProfileFile?


    @Query("SELECT * FROM files ")
    suspend fun getFiles(): List<JsonParseClasses.ProfileFile>

    @Query("SELECT file_name from files WHERE id=:key and isBroken=0")
    suspend fun getFileName(key: Long): String


    //Profile
    @Insert
    fun insertProfile(profile: JsonParseClasses.Profile)


    @Query("SELECT * FROM profile")
    suspend fun getProfile():JsonParseClasses.Profile

    //Timezones
    @Query("SELECT * FROM timings")
    fun getTimeZones(): LiveData<List<JsonParseClasses.AdvancedDay>>



    @Insert
    fun insertProfile(days: JsonParseClasses.AdvancedDay)



    @Insert
    fun insertTimezone(days: List<JsonParseClasses.AdvancedDay>)

    @Update
    fun timeZoneUpdate(day:JsonParseClasses.AdvancedDay)
//    @Query("SELECT id_music FROM playlist_table WHERE playlist_id=:key AND is_broken=0")
//    suspend fun getMusicIdsByPlaylistId(key: Long): Array<Long>
//
//
//    @Query("SELECT * FROM file_table ")
//    fun getFiles(): List<FileDatabase>

}