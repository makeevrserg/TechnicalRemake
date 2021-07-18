package com.makeevrserg.technicalremake.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(primaryKeys = ["day","playlist_id"])
data class PlayerPlaylistProportion(
    var day:String,
    val playlist_id: Long,
    val proportion: Int
)