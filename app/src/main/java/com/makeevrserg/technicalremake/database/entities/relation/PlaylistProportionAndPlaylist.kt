package com.makeevrserg.technicalremake.database.entities.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.makeevrserg.technicalremake.database.entities.PlayerPlaylist
import com.makeevrserg.technicalremake.database.entities.PlayerPlaylistProportion

data class PlaylistProportionAndPlaylist (
    @Embedded
    val playlistProportion: PlayerPlaylistProportion,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val playlist: PlayerPlaylist

)