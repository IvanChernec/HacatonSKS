package com.example.hacaton.model

import com.example.hacaton.db.Schedule

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
    val groupName: String
)

// Функции для получения тестовых данных
fun getStudentScheduleItems(selectedGroup: String): List<ScheduleItem> {
    return listOf(
        ScheduleItem(
            id = 1,
            groupId = 1,
            teacherId = 1,
            subjectId = 1,
            day = 1,
            week = 1,
            room = "Ауд. 101",
            startTime = "09:00",
            endTime = "10:30",
            subjectName = "Математика",
            teacherName = "Иванов И.И.",
            groupName = selectedGroup
        ),
        ScheduleItem(
            id = 2,
            groupId = 1,
            teacherId = 2,
            subjectId = 2,
            day = 1,
            week = 1,
            room = "Ауд. 202",
            startTime = "11:00",
            endTime = "12:30",
            subjectName = "Физика",
            teacherName = "Петров П.П.",
            groupName = selectedGroup
        )
    )
}

fun getTeacherScheduleItems(selectedTeacher: String): List<ScheduleItem> {
    return listOf(
        ScheduleItem(
            id = 1,
            groupId = 1,
            teacherId = 1,
            subjectId = 1,
            day = 1,
            week = 1,
            room = "Ауд. 101",
            startTime = "09:00",
            endTime = "10:30",
            subjectName = "Математика",
            teacherName = selectedTeacher,
            groupName = "ИП-212"
        ),
        ScheduleItem(
            id = 2,
            groupId = 2,
            teacherId = 1,
            subjectId = 2,
            day = 1,
            week = 1,
            room = "Ауд. 202",
            startTime = "11:00",
            endTime = "12:30",
            subjectName = "Физика",
            teacherName = selectedTeacher,
            groupName = "РВ-231"
        )
    )
}