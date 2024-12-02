package com.example.hacaton.model

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _currentTheme = mutableStateOf(prefs.getString("theme", "Системная") ?: "Системная")
    val currentTheme: String
        get() = _currentTheme.value

    private val _notificationsEnabled = mutableStateOf(prefs.getBoolean("notifications_enabled", true))
    val notificationsEnabled: Boolean
        get() = _notificationsEnabled.value

    private val _reminderTime = mutableStateOf(prefs.getString("reminder_time", "17:00") ?: "17:00")
    val reminderTime: String
        get() = _reminderTime.value

    fun setTheme(theme: String) {
        _currentTheme.value = theme
        prefs.edit().putString("theme", theme).apply()
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    fun setReminderTime(time: String) {
        _reminderTime.value = time
        prefs.edit().putString("reminder_time", time).apply()
    }
}