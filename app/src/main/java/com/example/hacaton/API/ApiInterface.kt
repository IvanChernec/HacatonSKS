package com.example.hacaton.API

import retrofit2.http.GET
import retrofit2.http.Query

interface ScheduleApi {
    @GET("initial-data")
    suspend fun getInitialData(): ApiResponse<InitialData>

    @GET("getScheduleGroup")
    suspend fun getScheduleForGroup(
        @Query("group") groupId: String
    ): ApiResponse<ScheduleData>

    @GET("schedule")
    suspend fun getScheduleForTeacher(
        @Query("teacher") teacherId: String
    ): ApiResponse<ScheduleData>

    @GET("getAllGroup")
    suspend fun getGroups(): ApiResponse<List<GroupDto>>

    @GET("getAllTeachers")
    suspend fun getTeachers(): ApiResponse<List<TeacherDto>>

    @GET("getAllSubjects")
    suspend fun getSubjects(): ApiResponse<List<SubjectDto>>

    @GET("academic-year")
    suspend fun getAcademicYear(): ApiResponse<AcademicYearDto>
}