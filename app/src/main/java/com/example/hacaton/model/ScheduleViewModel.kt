package com.example.hacaton.model

import android.content.Context
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate
import java.util.*
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hacaton.db.AppDatabase
import com.example.hacaton.db.Note
import com.example.hacaton.notification.NoteNotificationReceiver
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val noteDao = database.noteDao()
    private val scheduleDao = database.scheduleDao()

    fun addNote(scheduleId: Int, text: String, hasReminder: Boolean) {
        viewModelScope.launch {
            val note = Note(
                scheduleId = scheduleId,
                text = text,
                timestamp = System.currentTimeMillis(),
                needReminder = hasReminder
            )
            noteDao.insert(note)

            if (hasReminder) {
                val schedule = scheduleDao.getScheduleById(scheduleId)
                schedule?.let {
                    scheduleNoteReminder(
                        text,
                        it.subjectId,
                        it.day,
                        it.week
                    )
                }
            }
        }
    }
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteDao.delete(note)
        }
    }

    private suspend fun scheduleNoteReminder(
        noteText: String,
        subjectId: Int,
        currentDay: Int,
        currentWeek: Int
    ) {
        try {
            // Получаем следующую пару
            val nextSchedule = scheduleDao.getNextSimilarSchedule(
                subjectId,
                currentDay,
                currentWeek
            )

            nextSchedule?.let { schedule ->
                val startDate = LocalDate.of(2024, 9, 1)
                val firstMonday = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))

                val targetDate = firstMonday
                    .plusWeeks(schedule.week.toLong() - 1)
                    .plusDays((schedule.day - 1).toLong())

                // Устанавливаем уведомление на день раньше в 17:00
                val notificationDate = targetDate.minusDays(1)

                val calendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, notificationDate.year)
                    set(Calendar.MONTH, notificationDate.monthValue - 1)
                    set(Calendar.DAY_OF_MONTH, notificationDate.dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, 17)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }

                // Проверяем, что дата уведомления не в прошлом
                if (calendar.timeInMillis <= System.currentTimeMillis()) {
                    Toast.makeText(
                        getApplication(),
                        "Невозможно установить напоминание на прошедшую дату",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                Log.d("ScheduleViewModel", "Scheduled time: ${calendar.time}")


                val intent = Intent(getApplication(), NoteNotificationReceiver::class.java).apply {
                    putExtra("note_text", noteText)
                    flags = Intent.FLAG_RECEIVER_FOREGROUND
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    getApplication(),
                    noteText.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val alarmManager =
                    getApplication<Application>().getSystemService(Context.ALARM_SERVICE) as AlarmManager

                viewModelScope.launch(Dispatchers.Main) {
                    try {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )

                        Toast.makeText(
                            getApplication(),
                            "Напоминание установлено",
                            Toast.LENGTH_SHORT
                        ).show()

                        Log.d("ScheduleViewModel", "Reminder scheduled successfully")
                    } catch (e: Exception) {
                        Log.e("ScheduleViewModel", "Error scheduling alarm", e)
                        Toast.makeText(
                            getApplication(),
                            "Ошибка при установке напоминания: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ScheduleViewModel", "Error in scheduleNoteReminder", e)
            viewModelScope.launch(Dispatchers.Main) {
                Toast.makeText(
                    getApplication(),
                    "Ошибка при настройке напоминания: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    fun transferLastNote(currentItem: ScheduleItem) {
        viewModelScope.launch {
            val nextSchedule = scheduleDao.getNextSimilarSchedule(
                currentItem.subjectId,
                currentItem.day,
                currentItem.week
            )

            if (nextSchedule != null) {
                val lastNote = noteDao.getLastNoteForSchedule(currentItem.id)
                lastNote?.let { note ->
                    noteDao.insert(note.copy(id = 0, scheduleId = nextSchedule.id))
                    if (note.needReminder) {
                        scheduleNoteReminder(
                            note.text,
                            currentItem.subjectId,
                            currentItem.day,
                            currentItem.week
                        )
                    }
                }
            } else {
                Toast.makeText(
                    getApplication(),
                    "Это последняя пара",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}