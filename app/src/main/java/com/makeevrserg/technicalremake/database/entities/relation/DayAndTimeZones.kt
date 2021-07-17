package com.makeevrserg.technicalremake.database.entities.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.makeevrserg.technicalremake.database.entities.PlayerDay
import com.makeevrserg.technicalremake.database.entities.PlayerTimezone

data class DayAndTimeZones(
    @Embedded
    val day: PlayerDay,
    @Relation(
        parentColumn = "day",
        entityColumn = "id"
    )
    val timezones: List<PlayerTimezone>
)

