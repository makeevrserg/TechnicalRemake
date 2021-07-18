package com.makeevrserg.technicalremake.database.entities.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.makeevrserg.technicalremake.database.entities.PlayerPlaylist
import com.makeevrserg.technicalremake.database.entities.PlayerProfile

data class ProfileAndPlaylist(
    @Embedded
    val playerProfile: PlayerProfile,
    @Relation(
        parentColumn = "name",
        entityColumn = "playlist_id"
    )
    val playlists: List<PlayerPlaylist>
)
