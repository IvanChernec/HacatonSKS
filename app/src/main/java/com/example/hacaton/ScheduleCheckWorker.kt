package com.example.hacaton

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.hacaton.API.ApiClient
import com.example.hacaton.API.ScheduleDto
import com.example.hacaton.db.AppDatabase
import com.example.hacaton.db.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScheduleCheckWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        try {
            val database = AppDatabase.getInstance(applicationContext)
            val prefs = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val isTeacher = prefs.getInt("isTeacher", -1)
            val selectedItem = prefs.getString("selectedItem", null)

            if (isTeacher == -1 || selectedItem == null) {
                return Result.success()
            }

            // Получаем текущее расписание
            val currentSchedule = if (isTeacher == 0) {
                val group = database.groupDao().getGroupByName(selectedItem)
                database.scheduleDao().getScheduleForGroup(group.id).map { scheduleWithDetails ->
                    Schedule(
                        id = scheduleWithDetails.id,
                        subjectId = scheduleWithDetails.subjectId,
                        teacherId = scheduleWithDetails.teacherId,
                        groupId = scheduleWithDetails.groupId,
                        room = scheduleWithDetails.room,
                        startTime = scheduleWithDetails.startTime,
                        endTime = scheduleWithDetails.endTime,
                        day = scheduleWithDetails.day,
                        week = scheduleWithDetails.week
                    )
                }
            } else {
                val teacher = database.teacherDao().getTeacherByName(selectedItem)
                database.scheduleDao().getScheduleForTeacher(teacher.id).map { scheduleWithDetails ->
                    Schedule(
                        id = scheduleWithDetails.id,
                        subjectId = scheduleWithDetails.subjectId,
                        teacherId = scheduleWithDetails.teacherId,
                        groupId = scheduleWithDetails.groupId,
                        room = scheduleWithDetails.room,
                        startTime = scheduleWithDetails.startTime,
                        endTime = scheduleWithDetails.endTime,
                        day = scheduleWithDetails.day,
                        week = scheduleWithDetails.week
                    )
                }
            }

            // Получаем новое расписание с API
            val response = if (isTeacher == 0) {
                val group = database.groupDao().getGroupByName(selectedItem)
                ApiClient.api.getScheduleForGroup(group.name)
            } else {
                val teacher = database.teacherDao().getTeacherByName(selectedItem)
                ApiClient.api.getScheduleForTeacher(teacher.name)
            }

            val scheduleEntities = response.map { dto ->
                Schedule(
                    id = dto.id.toInt(),
                    subjectId = dto.subjectId.toInt(),
                    teacherId = dto.teacherId.toInt(),
                    groupId = dto.groupId.toInt(),
                    room = dto.room,
                    startTime = dto.startTime,
                    endTime = dto.endTime,
                    day = dto.day,
                    week = dto.week
                )
            }

// Сравниваем расписания и обновляем если есть изменения
            if (hasScheduleChanged(currentSchedule, scheduleEntities)) {
                database.scheduleDao().insertAll(scheduleEntities)
                showScheduleChangeNotification()
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    private fun hasScheduleChanged(
        currentSchedule: List<Schedule>,
        newSchedule: List<Schedule>
    ): Boolean {
        if (currentSchedule.size != newSchedule.size) return true

        return currentSchedule.zip(newSchedule).any { (current, new) ->
            current.subjectId != new.subjectId ||
                    current.teacherId != new.teacherId ||
                    current.groupId != new.groupId ||
                    current.room != new.room ||
                    current.startTime != new.startTime ||
                    current.endTime != new.endTime ||
                    current.day != new.day ||
                    current.week != new.week
        }
    }

    private fun showScheduleChangeNotification() {
        val channelId = "schedule_changes"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                channelId,
                "Изменения в расписании",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления об изменениях в расписании"
                enableLights(true)
                enableVibration(true)
                notificationManager.createNotificationChannel(this)
            }
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Изменения в расписании")
            .setContentText("Обнаружены изменения в вашем расписании")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}