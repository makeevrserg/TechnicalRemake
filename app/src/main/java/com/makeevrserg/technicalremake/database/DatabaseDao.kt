package com.makeevrserg.technicalremake.database

import androidx.room.*
import com.makeevrserg.technicalremake.database.entities.*


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
    @Query("SELECT * FROM files WHERE id=:key")
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
    @Insert
    suspend fun insertPlayerPlaylistProportion(props:List<PlayerPlaylistProportion>)
    //Profile
    @Insert
    fun insertProfileList(profile: PlayerProfile)
    @Query("SELECT * FROM profile")
    suspend fun getProfile(): PlayerProfile

//    @Transaction
//    @Query("SELECT * FROM profile WHERE name=:profileName")
//    suspend fun getProfileAndPlaylists(profileName:String):List<ProfileAndPlaylist>


}