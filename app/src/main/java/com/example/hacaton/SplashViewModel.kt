package com.example.hacaton

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.hacaton.API.ApiClient
import com.example.hacaton.API.ApiState
import com.example.hacaton.API.InitialData
import com.example.hacaton.API.ScheduleData
import com.example.hacaton.db.AppDatabase
import com.example.hacaton.db.Group
import com.example.hacaton.db.Schedule
import com.example.hacaton.db.Subject
import com.example.hacaton.db.Teacher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private val _apiState = MutableStateFlow<ApiState>(ApiState.Loading)
    val apiState: StateFlow<ApiState> = _apiState
    private val database = AppDatabase.getInstance(application)

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                withTimeout(25000) {
                    val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val isTeacher = prefs.getInt("isTeacher", -1)
                    val selectedItem = prefs.getString("selectedItem", null)

                    if (isTeacher == -1 || selectedItem == null) {
                        val response = ApiClient.api.getInitialData()
                        if (response.success) {
                            saveInitialData(response.data)
                            _apiState.value = ApiState.Success
                        } else {
                            _apiState.value = ApiState.Error(response.error ?: "Unknown error")
                        }
                    } else {
                        // Остальной код без изменений
                    }
                }
            } catch (e: Exception) {
                _apiState.value = ApiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private suspend fun saveInitialData(data: InitialData?) {
        data?.let {
            withContext(Dispatchers.IO) {
                database.groupDao().insertAll(it.groups.map { dto ->
                    Group(id = dto.id, name = dto.name)
                })
                database.teacherDao().insertAll(it.teachers.map { dto ->
                    Teacher(id = dto.id, name = dto.name)
                })
                database.subjectDao().insertAll(it.subjects.map { dto ->
                    Subject(id = dto.id, name = dto.name)
                })
            }
        }
    }

    private suspend fun saveScheduleData(data: ScheduleData?) {
        data?.let {
            withContext(Dispatchers.IO) {
                database.scheduleDao().insertAll(it.schedules.map { dto ->
                    Schedule(
                        id = dto.id,
                        subjectId = dto.subjectId,
                        teacherId = dto.teacherId,
                        groupId = dto.groupId,
                        room = dto.room,
                        startTime = dto.startTime,
                        endTime = dto.endTime,
                        day = dto.day,
                        week = dto.week
                    )
                })
            }
        }
    }

    fun switchToOfflineMode() {
        _apiState.value = ApiState.Error("Switched to offline mode")
    }
}