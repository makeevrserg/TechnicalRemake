package com.makeevrserg.technicalremake.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file_table")
data class FileDatabase(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "file_id")
    var id: Long,

    @ColumnInfo(name = "file_path")
    val file_name: String,

    @ColumnInfo(name = "file_name")
    val name: String,

    @ColumnInfo(name = "file_md5")
    val md5_file: String,

    @ColumnInfo(name = "is_broken")
    var isBroken: Boolean=true,

    )
