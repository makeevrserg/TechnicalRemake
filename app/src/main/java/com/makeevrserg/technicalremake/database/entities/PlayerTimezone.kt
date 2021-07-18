package com.makeevrserg.technicalremake.database.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class PlayerTimezone(
    @PrimaryKey(autoGenerate = true)
    val id: Long,//todo
    val from: String,
    val to: String,
    var day: String,
    @Ignore
    val playlists: List<PlayerPlaylistProportion> = mutableListOf(),

    ) {
    constructor(id: Long, from: String, to: String,day:String) : this(id, from, to,day, mutableListOf())
}