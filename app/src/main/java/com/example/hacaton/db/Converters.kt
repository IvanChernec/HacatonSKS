package com.example.hacaton.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromNotesList(notes: List<Note>?): String? {
        return Gson().toJson(notes)
    }

    @TypeConverter
    fun toNotesList(notesString: String?): List<Note>? {
        if (notesString == null) return null
        val type = object : TypeToken<List<Note>>() {}.type
        return Gson().fromJson(notesString, type)
    }
}