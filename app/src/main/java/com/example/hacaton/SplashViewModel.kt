package com.example.hacaton

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.hacaton.API.ApiClient
import com.example.hacaton.API.ApiState
import com.example.hacaton.API.InitialData
import com.example.hacaton.API.ScheduleData
import com.example.hacaton.API.saveGroupsFromJson
import com.example.hacaton.API.saveScheduleFromJson
import com.example.hacaton.API.saveSubjectsFromJson
import com.example.hacaton.API.saveTeachersFromJson
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
    private val database = AppDatabase.getInstance(getApplication())

    init {
        viewModelScope.launch {
            loadInitialData()
        }
    }

    private suspend fun loadInitialData() {
        try {
            withTimeout(25000) { // 25 секунд таймаут
                Log.d("SplashViewModel", "Starting data load")

                try {
                    Log.d("SplashViewModel", "Loading groups...")
                    val groupsResponse = ApiClient.api.getGroups()
                    Log.d("SplashViewModel", "Groups loaded: ${groupsResponse.size}")
                    saveGroupsFromJson(groupsResponse, database)

                    Log.d("SplashViewModel", "Loading teachers...")
                    val teachersResponse = ApiClient.api.getTeachers()
                    Log.d("SplashViewModel", "Teachers loaded: ${teachersResponse.size}")
                    saveTeachersFromJson(teachersResponse, database)

                    Log.d("SplashViewModel", "Loading subjects...")
                    val subjectsResponse = ApiClient.api.getSubjects()
                    Log.d("SplashViewModel", "Subjects loaded: ${subjectsResponse.size}")
                    saveSubjectsFromJson(subjectsResponse, database)

                    // Проверяем, был ли уже выбран пользователь
                    val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val isTeacher = prefs.getInt("isTeacher", -1)
                    val selectedItem = prefs.getString("selectedItem", null)

                    if (isTeacher != -1 && selectedItem != null) {
                        Log.d("SplashViewModel", "Loading schedule for existing user")
                        val scheduleResponse = if (isTeacher == 0) {
                            ApiClient.api.getScheduleForGroup(selectedItem)
                        } else {
                            ApiClient.api.getScheduleForTeacher(selectedItem)
                        }
                        saveScheduleFromJson(scheduleResponse, database)
                    }

                    _apiState.value = ApiState.Success

                } catch (e: Exception) {
                    Log.e("SplashViewModel", "Error loading data", e)
                    _apiState.value = ApiState.Error(e.message ?: "Error loading data")
                }
            }
        } catch (e: Exception) {
            // Fallback к локальным данным
        }
    }

    fun switchToOfflineMode() {
        _apiState.value = ApiState.Error("Switched to offline mode")
    }
}