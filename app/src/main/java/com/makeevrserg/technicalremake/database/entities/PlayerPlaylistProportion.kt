package com.makeevrserg.technicalremake.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class PlayerPlaylistProportion(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    var day:String,
    var from:String,
    var to:String,
    var playlistName:String,
    var showDay:Boolean = false,
    val playlist_id: Long,
    val proportion: Int
)