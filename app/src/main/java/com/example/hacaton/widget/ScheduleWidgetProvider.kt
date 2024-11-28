package com.example.hacaton.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.example.hacaton.R

class ScheduleWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_schedule)

            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val isTeacher = prefs.getInt("isTeacher", -1)
            val selectedItem = prefs.getString("selectedItem", null)

            if (isTeacher == -1 || selectedItem.isNullOrEmpty()) {
                views.setViewVisibility(R.id.widget_title, View.GONE)
                views.setViewVisibility(R.id.widget_message, View.VISIBLE)
                views.setViewVisibility(R.id.widget_list, View.GONE)
                views.setTextViewText(R.id.widget_message, "Войдите в приложение и выберите роль")
            } else {
                // Устанавливаем заголовок
                views.setViewVisibility(R.id.widget_title, View.VISIBLE)
                views.setTextViewText(R.id.widget_title, selectedItem)

                views.setViewVisibility(R.id.widget_message, View.GONE)
                views.setViewVisibility(R.id.widget_list, View.VISIBLE)

                val intent = Intent(context, ScheduleWidgetService::class.java)
                views.setRemoteAdapter(R.id.widget_list, intent)

                // Добавляем обработчик для кнопки обновления
                val refreshIntent = Intent(context, ScheduleWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
                }
                val refreshPendingIntent = PendingIntent.getBroadcast(
                    context,
                    appWidgetId,
                    refreshIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_refresh, refreshPendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list)
        }
    }
}