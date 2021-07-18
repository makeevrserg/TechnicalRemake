package com.makeevrserg.technicalremake.database.entities.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.makeevrserg.technicalremake.database.entities.PlayerFile
import com.makeevrserg.technicalremake.database.entities.PlayerPlaylist
import com.makeevrserg.technicalremake.database.entities.relation.crossrefs.FilePlaylistCrossRef

class PlaylistWithFiles(
    @Embedded
    val playlist: PlayerPlaylist,
    @Relation(
        parentColumn = "playlist_id",
        entityColumn = "file_id",
        associateBy = Junction(FilePlaylistCrossRef::class)
    )
    private val files: List<PlayerFile>
){
    init {
        playlist.files.addAll(files)
    }
}
