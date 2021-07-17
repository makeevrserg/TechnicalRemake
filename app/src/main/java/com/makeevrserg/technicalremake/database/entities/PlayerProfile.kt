package com.makeevrserg.technicalremake.database.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.makeevrserg.technicalremake.database.entities.ProfileSchedule

@Entity(tableName = "profile")
data class PlayerProfile(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    @Ignore
    val schedule: ProfileSchedule? = null
) {
    constructor(id: Int, name: String) : this(id, name, null)
}