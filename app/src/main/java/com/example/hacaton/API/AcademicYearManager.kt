package com.example.hacaton.API

import android.content.Context
import java.time.LocalDate

class AcademicYearManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    suspend fun getAcademicYear(): Pair<LocalDate, LocalDate> {
        return try {
            val response = ApiClient.api.getAcademicYear()
            if (response.success && response.data != null) {
                parseAcademicYear(response.data.academicYear)
            } else {
                getDefaultAcademicYear()
            }
        } catch (e: Exception) {
            getDefaultAcademicYear()
        }
    }

    private fun parseAcademicYear(academicYear: String): Pair<LocalDate, LocalDate> {
        try {
            val parts = academicYear.split(" ")
            val startParts = parts[0].removePrefix("start:").split(":")
            val endParts = parts[1].removePrefix("end:").split(":")

            val startDate = LocalDate.of(
                startParts[2].toInt(),
                startParts[1].toInt(),
                startParts[0].toInt()
            )
            val endDate = LocalDate.of(
                endParts[2].toInt(),
                endParts[1].toInt(),
                endParts[0].toInt()
            )

            return Pair(startDate, endDate)
        } catch (e: Exception) {
            return getDefaultAcademicYear()
        }
    }

    private fun getDefaultAcademicYear(): Pair<LocalDate, LocalDate> {
        val currentYear = LocalDate.now().year
        return Pair(
            LocalDate.of(currentYear, 9, 1),
            LocalDate.of(currentYear, 12, 31)
        )
    }
}