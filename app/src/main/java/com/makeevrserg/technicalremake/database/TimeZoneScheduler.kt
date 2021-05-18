package com.makeevrserg.technicalremake.database

import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.room.*

@Entity(tableName = "timezones")
data class TimeZoneScheduler(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val timeZoneId: Long = 0L,
    @ColumnInfo(name = "from")
    val from: String,
    @ColumnInfo(name = "to")
    val to: String,
    @ColumnInfo(name = "day")
    val day: String,

    @ColumnInfo(name = "playlist_name")
    val playlistName: String,

    @ColumnInfo(name = "playlist_id")
    val playlistId: Long?,
    @ColumnInfo(name = "proportion")
    var proportion: Int?,

    @ColumnInfo(name = "is_broken")
    var isBroken: Boolean = false
)