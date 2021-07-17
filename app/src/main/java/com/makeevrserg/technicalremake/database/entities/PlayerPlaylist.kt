package com.makeevrserg.technicalremake.database.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.makeevrserg.technicalremake.database.entities.PlayerFile

@Entity
data class PlayerPlaylist(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val name: String,
    val duration: Long,
    val random: Boolean,
    @Ignore
    val files: MutableList<PlayerFile> = mutableListOf()
){
    constructor(id:Long,name:String,duration:Long,random: Boolean):this(id,name, duration, random,
        mutableListOf())
}