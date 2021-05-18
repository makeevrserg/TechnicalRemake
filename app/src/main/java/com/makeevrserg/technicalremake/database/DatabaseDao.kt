package com.makeevrserg.technicalremake.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface DatabaseDao {

    //TimeZone
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun timeZoneInsert(timeZoneScheduler: TimeZoneScheduler)

    @Update
    suspend fun timeZoneUpdate(timeZoneScheduler: TimeZoneScheduler)

    @Query("UPDATE timezones set is_broken=:isBroken WHERE playlist_id=:id")
    suspend fun updateTimezoneBrokentByPlaylistId(isBroken: Boolean, id: Long)

    @Query("SELECT timeZoneId,`from`,`to`,day,playlist_name,playlist_id,sum(proportion) as proportion,is_broken FROM timezones WHERE :day=day AND :time BETWEEN `from` AND `to` AND proportion IS NOT NULL GROUP BY playlist_id ORDER BY proportion DESC")
    fun getTimezonesByTime(day: String, time: String): List<TimeZoneScheduler>

    @Query("SELECT * FROM timezones ORDER BY timeZoneId ASC")
    fun getTimeZones(): LiveData<List<TimeZoneScheduler>>


    @Query("DELETE FROM timezones")
    suspend fun clearTimeZone()



    //PlayList
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun playlistInsert(playList: PlayList)

    @Update
    suspend fun playlistUpdate(playlistDatabase: PlayList)

    @Query("UPDATE playlist_table set is_broken=:isBroken WHERE id_music=:id")
    suspend fun updatePlaylistBrokentByMusicId(isBroken: Boolean, id: Long)

    @Query("SELECT playlist_id FROM playlist_table WHERE id_music=:id")
    suspend fun getPlaylistsByMusicId(id: Long): List<Long>

    @Query("SELECT DISTINCT playlist_name FROM playlist_table WHERE playlist_id=:id")
    suspend fun getPlaylistNameByPlaylistId(id: Long): String


    @Query("SELECT * FROM playlist_table ORDER BY playlist_id ASC")
    suspend fun getPlaylists(): List<PlayList>

    @Query("DELETE FROM playlist_table")
    suspend fun clearPlayLists()



    //Files
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun fileInsert(fileDatabase: FileDatabase)

    @Update
    suspend fun fileUpdate(fileDatabase: FileDatabase)

    @Query("SELECT * FROM file_table WHERE file_id=:key")
    suspend fun getFile(key: Long): FileDatabase?

    @Query("SELECT file_name from file_table WHERE file_id=:key and is_broken=0")
    suspend fun getFileName(key: Long): String


    @Query("SELECT id_music FROM playlist_table WHERE playlist_id=:key AND is_broken=0")
    suspend fun getMusicIdsByPlaylistId(key: Long): Array<Long>


    @Query("SELECT * FROM file_table ")
    fun getFiles(): List<FileDatabase>


    @Query("DELETE FROM file_table")
    suspend fun clearFiles()

}