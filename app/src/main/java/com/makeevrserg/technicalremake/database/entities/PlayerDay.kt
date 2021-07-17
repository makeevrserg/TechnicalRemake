package com.makeevrserg.technicalremake.database.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class PlayerDay(
    @PrimaryKey(autoGenerate = false)
    val day: String,
    @Ignore
    val timeZones: List<PlayerTimezone> = mutableListOf()
){
    constructor(day:String):this(day, mutableListOf())
}