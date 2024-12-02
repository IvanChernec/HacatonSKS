package com.example.hacaton.API

import retrofit2.http.GET
import retrofit2.http.Query

interface ScheduleApi {
    @GET("initial-data")
    suspend fun getInitialData(): ApiResponse<InitialData>

    @GET("schedule")
    suspend fun getScheduleForGroup(
        @Query("groupId") groupId: Int
    ): ApiResponse<ScheduleData>

    @GET("schedule")
    suspend fun getScheduleForTeacher(
        @Query("teacherId") teacherId: Int
    ): ApiResponse<ScheduleData>
}