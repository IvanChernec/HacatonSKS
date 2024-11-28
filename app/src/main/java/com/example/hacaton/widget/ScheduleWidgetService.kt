package com.example.hacaton.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.hacaton.R
import com.example.hacaton.db.AppDatabase
import com.example.hacaton.fragments.getCurrentWeekAndDay
import com.example.hacaton.mappers.toScheduleItem
import com.example.hacaton.model.ScheduleItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ScheduleWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ScheduleWidgetFactory(applicationContext)
    }
}

class ScheduleWidgetFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private var scheduleItems: List<ScheduleItem> = emptyList()

    override fun onDataSetChanged() {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isTeacher = prefs.getInt("isTeacher", -1)
        val selectedItem = prefs.getString("selectedItem", null)

        val database = AppDatabase.getInstance(context)
        val (currentWeek, currentDay) = getCurrentWeekAndDay()

        scheduleItems = runBlocking {
            withContext(Dispatchers.IO) {
                try {
                    Log.d("WidgetFactory", "Loading data for day: $currentDay, week: $currentWeek")
                    if (isTeacher == 0) {
                        val group = database.groupDao().getGroupByName(selectedItem!!)
                        database.scheduleDao().getScheduleForGroup(group.id)
                    } else {
                        val teacher = database.teacherDao().getTeacherByName(selectedItem!!)
                        database.scheduleDao().getScheduleForTeacher(teacher.id)
                    }.filter { it.day == currentDay && it.week == currentWeek }
                        .map { schedule ->
                            ScheduleItem(
                                id = schedule.id,
                                subjectName = database.subjectDao().getSubjectById(schedule.subjectId).name,
                                teacherName = database.teacherDao().getTeacherById(schedule.teacherId).name,
                                room = schedule.room,
                                startTime = schedule.startTime,
                                endTime = schedule.endTime,
                                day = schedule.day,
                                week = schedule.week,
                                groupId = schedule.groupId,
                                teacherId = schedule.teacherId,
                                subjectId = schedule.subjectId,
                                groupName = database.groupDao().getGroupById(schedule.groupId).name
                            )
                        }.also {
                            Log.d("WidgetFactory", "Loaded ${it.size} items")
                        }
                } catch (e: Exception) {
                    Log.e("WidgetFactory", "Error loading data", e)
                    emptyList()
                }
            }
        }
    }

    override fun getViewAt(position: Int): RemoteViews {
        val item = scheduleItems[position]
        return RemoteViews(context.packageName, R.layout.widget_schedule_item).apply {
            setTextViewText(R.id.time_text, "${item.startTime} - ${item.endTime}")
            setTextViewText(R.id.subject_text, item.subjectName)
            setTextViewText(R.id.teacher_text, item.teacherName)
            setTextViewText(R.id.room_text, "Ауд. ${item.room}")
        }
    }

    override fun getCount(): Int = scheduleItems.size
    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true
    override fun onCreate() {}
    override fun onDestroy() {}
}