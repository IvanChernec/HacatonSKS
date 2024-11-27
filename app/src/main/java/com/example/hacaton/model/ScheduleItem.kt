package com.example.hacaton.model

import com.example.hacaton.db.Note


data class ScheduleItem(
    val id: Int,
    val groupId: Int,
    val teacherId: Int,
    val subjectId: Int,
    val day: Int,
    val week: Int,
    val room: String,
    val startTime: String,
    val endTime: String,
    val subjectName: String,
    val teacherName: String,
    val groupName: String,
    val notes: List<Note>? = null
)

