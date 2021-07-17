package com.makeevrserg.technicalremake.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
data class PlayerFile(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val file_name: String,
    val name: String,
    val size: Int,
    val md5_file: String,
    val duration: Long,
    val order: Int,
    var isBroken: Boolean
)