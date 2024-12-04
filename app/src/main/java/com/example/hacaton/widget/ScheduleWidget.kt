package com.example.hacaton.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.layout.Box
import androidx.glance.text.TextAlign
import com.example.hacaton.db.AppDatabase
import com.example.hacaton.fragments.getCurrentWeekAndDay
import com.example.hacaton.model.ScheduleItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope

class ScheduleWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isTeacher = prefs.getInt("isTeacher", -1)
        val selectedItem = prefs.getString("selectedItem", null)

        provideContent {
            when {
                isTeacher == -1 || selectedItem.isNullOrEmpty() -> {
                    // Показываем сообщение для неавторизованного пользователя
                    Column(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .background(GlanceTheme.colors.background)
                            .padding(16.dp),
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                    ) {
                        Text(
                            text = "Войдите в приложение и выберите роль",
                            style = TextStyle(
                                color = GlanceTheme.colors.onBackground,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
                else -> {
                    // Показываем расписание
                    Column(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .background(GlanceTheme.colors.background)
                            .padding(8.dp)
                    ) {
                        // Заголовок
                        Text(
                            text = selectedItem,
                            style = TextStyle(
                                color = GlanceTheme.colors.onBackground,
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp
                            ),
                            modifier = GlanceModifier.padding(bottom = 8.dp)
                        )
                        var scheduleItems by remember { mutableStateOf<List<ScheduleItem>>(emptyList()) }
                        // Список предметов
                        LaunchedEffect(Unit) {
                            scheduleItems = loadScheduleItems(context, isTeacher, selectedItem)
                        }
                        LazyColumn {
                            items(scheduleItems) { item ->
                                ScheduleItemCard(item)
                            }
                        }

                        // Кнопка обновления
                        Button(
                            text = "Обновить",
                            onClick = actionRunCallback<RefreshAction>(),
                            modifier = GlanceModifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleItemCard(item: ScheduleItem) {
    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(GlanceTheme.colors.onSecondary)
            .padding(8.dp)
            .cornerRadius(12.dp)
    ) {
        Column {
            Text(text = "",
                style = TextStyle(
                    fontSize = 0.sp
                ))
            Text(
                text = "${item.startTime} - ${item.endTime}",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 14.sp
                )
            )
            Text(
                text = item.subjectName,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 16.sp
                )
            )
            Text(
                text = item.teacherName,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 14.sp
                )
            )
            Text(
                text = "Ауд. ${item.room}",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 14.sp
                )
            )
        }
    }
}

class RefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Обновляем виджет
        ScheduleWidget().update(context, glanceId)
    }
}

private suspend fun loadScheduleItems(
    context: Context,
    isTeacher: Int,
    selectedItem: String
): List<ScheduleItem> {
    val database = AppDatabase.getInstance(context)
    val (currentWeek, currentDay) = getCurrentWeekAndDay()

    return withContext(Dispatchers.IO) {
        try {
            if (isTeacher == 0) {
                val group = database.groupDao().getGroupByName(selectedItem)
                database.scheduleDao().getScheduleForGroup(group.id)
            } else {
                val teacher = database.teacherDao().getTeacherByName(selectedItem)
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
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
