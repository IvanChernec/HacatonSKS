package com.example.hacaton.API

import com.example.hacaton.db.AppDatabase
import com.example.hacaton.db.Group
import com.example.hacaton.db.Schedule
import com.example.hacaton.db.Subject
import com.example.hacaton.db.Teacher
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// API Response models
data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: String?
)

data class InitialData(
    val groups: List<GroupDto>,
    val teachers: List<TeacherDto>,
    val subjects: List<SubjectDto>
)

data class ScheduleData(
    val schedules: List<ScheduleDto>
)
data class AcademicYearDto(
    val academicYear: String // формат "start:DD:MM:YYYY end:DD:MM:YYYY"
)

// DTO (Data Transfer Objects)
data class GroupDto(
    val id: Int,
    val name: String
)

data class TeacherDto(
    val id: Int,
    val name: String
)

data class SubjectDto(
    val id: Int,
    val name: String
)

data class ScheduleDto(
    val id: Int,
    val subjectId: Int,
    val teacherId: Int,
    val groupId: Int,
    val room: String,
    val startTime: String,
    val endTime: String,
    val day: Int,
    val week: Int
)
// DTO классы для десериализации JSON
data class GroupResponse(
    val id: String,
    val name: String
)

data class TeacherResponse(
    val id: String,
    val name: String
)

data class SubjectResponse(
    val id: String,
    val name: String
)

data class ScheduleResponse(
    val id: String,
    val subjectId: String,
    val teacherId: String,
    val groupId: String,
    val room: String,
    val startTime: String,
    val endTime: String,
    val day: Int,
    val week: Int
)

suspend fun saveGroupsFromJson(response: List<GroupResponse>, database: AppDatabase) {
    withContext(Dispatchers.IO) {
        val entities = response.map {
            Group(
                id = it.id.toInt(),
                name = it.name
            )
        }
        database.groupDao().insertAll(entities)
    }
}
suspend fun saveTeachersFromJson(response: List<TeacherResponse>, database: AppDatabase) {
    withContext(Dispatchers.IO) {
        val entities = response.map {
            Teacher(
                id = it.id.toInt(),
                name = it.name
            )
        }
        database.teacherDao().insertAll(entities)
    }
}
suspend fun saveSubjectsFromJson(response: List<SubjectResponse>, database: AppDatabase) {
    withContext(Dispatchers.IO) {
        val entities = response.map {
            Subject(
                id = it.id.toInt(),
                name = it.name
            )
        }
        database.subjectDao().insertAll(entities)
    }
}


suspend fun saveScheduleFromJson(response: List<ScheduleResponse>, database: AppDatabase) {
    withContext(Dispatchers.IO) {
        val entities = response.map {
            Schedule(
                id = it.id.toInt(),
                subjectId = it.subjectId.toInt(),
                teacherId = it.teacherId.toInt(),
                groupId = it.groupId.toInt(),
                room = it.room,
                startTime = it.startTime,
                endTime = it.endTime,
                day = it.day,
                week = it.week
            )
        }
        database.scheduleDao().insertAll(entities)
    }
}