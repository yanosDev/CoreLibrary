package de.yanos.chat.domain.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.yanos.chat.data.MessageState

object TypeConverters {
    @TypeConverter
    fun fromStringToArray(value: String): List<String> {
        return Gson().fromJson(value, object : TypeToken<ArrayList<String>>() {}.type)
    }

    @TypeConverter
    fun fromArrayList(list: List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStringToMap(value: String): Map<String, MessageState> {
        return Gson().fromJson(value, object : TypeToken<Map<String, MessageState>>() {}.type)
    }

    @TypeConverter
    fun fromMap(list: Map<String, MessageState>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStringToMapWithList(value: String): Map<String, List<String>> {
        return Gson().fromJson(value, object : TypeToken<Map<String, List<String>>>() {}.type)
    }

    @TypeConverter
    fun fromMapWithList(list: Map<String, List<String>>): String {
        val gson = Gson()
        return gson.toJson(list)
    }


}