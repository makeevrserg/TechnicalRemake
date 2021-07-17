package com.makeevrserg.technicalremake.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class PlayerPlaylistProportion(
    @PrimaryKey(autoGenerate = true)
    val id:Long,//todo
    val playlist_id: Long,
    val proportion: Int
)