package com.example.hacaton.API

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