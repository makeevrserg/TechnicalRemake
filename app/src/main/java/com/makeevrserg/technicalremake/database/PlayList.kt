package com.makeevrserg.technicalremake.database

import androidx.room.*
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


@Entity(tableName = "playlist_table")
data class PlayList(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "playlist_id")
    var playlistId: Long,

    @ColumnInfo(name = "playlist_name")
    val playlistName: String,

    @ColumnInfo(name = "id_music")
    var fileID: Long,

    @ColumnInfo(name = "is_broken")
    var isBroken: Boolean = false
)
