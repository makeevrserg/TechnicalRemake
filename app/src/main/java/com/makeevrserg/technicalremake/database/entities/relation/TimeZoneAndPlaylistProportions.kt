package com.makeevrserg.technicalremake.database.entities.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.makeevrserg.technicalremake.database.entities.PlayerPlaylistProportion
import com.makeevrserg.technicalremake.database.entities.PlayerTimezone

data class TimeZoneAndPlaylistProportions(
    @Embedded
    val timeZone:PlayerTimezone,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val playlistProportions:List<PlayerPlaylistProportion>

)