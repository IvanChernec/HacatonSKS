package com.example.hacaton.mappers

import com.example.hacaton.db.Note
import com.example.hacaton.model.ScheduleItem

data class ScheduleWithDetails(
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

fun ScheduleWithDetails.toScheduleItem(): ScheduleItem {
    return ScheduleItem(
        id = id,
        groupId = groupId,
        teacherId = teacherId,
        subjectId = subjectId,
        day = day,
        week = week,
        room = room,
        startTime = startTime,
        endTime = endTime,
        subjectName = subjectName,
        teacherName = teacherName,
        groupName = groupName,
        notes = notes
    )
}