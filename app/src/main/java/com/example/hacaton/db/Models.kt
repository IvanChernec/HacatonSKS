package com.example.hacaton.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(tableName = "schedule")
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val groupId: Int,
    val teacherId: Int,
    val subjectId: Int,
    val day: Int,
    val week: Int,
    val room: String,
    val startTime: String,
    val endTime: String
)