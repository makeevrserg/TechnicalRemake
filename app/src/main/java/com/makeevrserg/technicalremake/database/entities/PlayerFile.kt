package com.makeevrserg.technicalremake.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
data class PlayerFile(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name="file_id")
    val id: Long,
    val file_name: String,
    val name: String,
    val size: Int,
    val md5_file: String,
    val duration: Long,
    val order: Int,
    var isBroken: Boolean
)