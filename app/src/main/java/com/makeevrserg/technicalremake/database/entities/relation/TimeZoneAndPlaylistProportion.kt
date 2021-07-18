package com.makeevrserg.technicalremake.database.entities.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.makeevrserg.technicalremake.database.entities.PlayerPlaylistProportion
import com.makeevrserg.technicalremake.database.entities.PlayerTimezone

class TimeZoneAndPlaylistProportion(
    @Embedded val timeZone:PlayerTimezone,
    @Relation(
        parentColumn = "day",
        entityColumn = "day"
    )
    val playlistProp:PlayerPlaylistProportion
)