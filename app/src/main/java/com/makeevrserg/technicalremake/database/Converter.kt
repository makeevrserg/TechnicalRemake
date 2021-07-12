package com.makeevrserg.technicalremake.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.makeevrserg.technicalremake.scheduler.JsonParseClasses

class Converter {
    @TypeConverter
    fun fromProfileSchedule(obj:JsonParseClasses.ProfileSchedule):String{
        return Gson().toJson(obj)
    }
    @TypeConverter
    fun toProfileSchedule(str:String):JsonParseClasses.ProfileSchedule{
        return Gson().fromJson(str,JsonParseClasses.ProfileSchedule::class.java)
    }
}