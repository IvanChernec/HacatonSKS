package com.example.hacaton.API

import retrofit2.http.GET
import retrofit2.http.Query

interface ScheduleApi {
    @GET("initial-data")
    suspend fun getInitialData(): ApiResponse<InitialData>

    @GET("getScheduleGroup")
    suspend fun getScheduleForGroup(
        @Query("group") groupId: String
    ): List<ScheduleResponse>

    @GET("getScheduleTeachers")
    suspend fun getScheduleForTeacher(
        @Query("teacher") teacherId: String
    ): List<ScheduleResponse>

    @GET("getAllGroup")
    suspend fun getGroups(): List<GroupResponse>  // Изменено с ApiResponse<List<GroupDto>> на List<GroupResponse>

    @GET("getAllTeachers")
    suspend fun getTeachers(): List<TeacherResponse>

    @GET("getAllSubjects")
    suspend fun getSubjects(): List<SubjectResponse>

    @GET("academic-year")
    suspend fun getAcademicYear(): ApiResponse<AcademicYearDto>
}