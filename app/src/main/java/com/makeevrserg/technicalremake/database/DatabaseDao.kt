package com.makeevrserg.technicalremake.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.makeevrserg.technicalremake.database.entities.*
import com.makeevrserg.technicalremake.database.entities.relation.DayAndTimeZones
import com.makeevrserg.technicalremake.database.entities.relation.PlaylistWithFiles
import com.makeevrserg.technicalremake.database.entities.relation.TimeZoneAndPlaylistProportion
import com.makeevrserg.technicalremake.database.entities.relation.crossrefs.FilePlaylistCrossRef


@Dao
interface DatabaseDao {


    //Profile
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProfile(fileDatabase: PlayerProfile)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProfileList(fileDatabase: List<PlayerProfile>)
    @Update
    suspend fun updateProfile(profile:PlayerProfile)

    //PlayerPlaylist
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerPlaylist(playerPlaylists:List<PlayerPlaylist>)
    //Files
    @Update
    suspend fun fileUpdate(fileDatabase: PlayerFile)
    @Query("SELECT * FROM files WHERE file_id=:key")
    suspend fun getFile(key: Long): PlayerFile?
    @Query("SELECT * FROM files ")
    suspend fun getAllFiles(): List<PlayerFile>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayerFile(files:List<PlayerFile>)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayerFile(files:PlayerFile)

    //PlayerDay
    @Insert
    suspend fun insertPlayerDay(day:List<PlayerDay>)
    //PlayerTimeZone
    @Insert
    suspend fun insertPlayerTimeZones(timezones:List<PlayerTimezone>)
    //PlayerPlaylistProportion
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayerPlaylistProportion(props:List<PlayerPlaylistProportion>)
    //Profile
    @Insert
    fun insertProfileList(profile: PlayerProfile)
    @Query("SELECT * FROM profile")
    suspend fun getProfile(): PlayerProfile


    //DayAndTimeZone
    @Transaction
    @Query("SELECT * FROM PlayerDay WHERE day=:day")
    suspend fun getDayAndTimezones(day:String):DayAndTimeZones

    //FilePlaylistCrossRef
    @Insert
    suspend fun insertFilePlaylistCrossRefs(list:List<FilePlaylistCrossRef>)

    //TimeZoneAndPlaylistProportion
//    @Transaction
//    @Query("SELECT * FROM playertimezone WHERE day=:day")
//    suspend fun getTimeZoneAndPlaylistProportion(day:String): LiveData<List<TimeZoneAndPlaylistProportion>>

    @Transaction
    @Query("SELECT * FROM playertimezone")
    suspend fun getAllTimeZoneAndPlaylistProportion(): List<TimeZoneAndPlaylistProportion>

    @Query("SELECT * FROM playerplaylist WHERE playlist_id=:playlist_id")
    suspend fun getFilesOfPlaylist(playlist_id:Long):PlaylistWithFiles
//    @Transaction
//    @Query("SELECT * FROM profile WHERE name=:profileName")
//    suspend fun getProfileAndPlaylists(profileName:String):List<ProfileAndPlaylist>


}