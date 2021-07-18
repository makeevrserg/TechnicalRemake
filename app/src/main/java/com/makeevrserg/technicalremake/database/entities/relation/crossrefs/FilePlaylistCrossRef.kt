package com.makeevrserg.technicalremake.database.entities.relation.crossrefs

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["playlist_id","file_id"])
class FilePlaylistCrossRef(
    @ColumnInfo(name = "playlist_id")
    val playlistId:Long,
    @ColumnInfo(name = "file_id")
    val fileId:Long
)