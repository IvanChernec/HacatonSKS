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
    private val database = AppDatabase.getInstance(getApplication())

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                withTimeout(25000) {
                    // Загружаем группы
                    val groupsResponse = ApiClient.api.getGroups()
                    if (groupsResponse.success) {
                        withContext(Dispatchers.IO) {
                            database.groupDao().insertAll(
                                groupsResponse.data?.map { dto ->
                                    Group(id = dto.id, name = dto.name)
                                } ?: emptyList()
                            )
                        }
                    }

                    // Загружаем преподавателей
                    val teachersResponse = ApiClient.api.getTeachers()
                    if (teachersResponse.success) {
                        withContext(Dispatchers.IO) {
                            database.teacherDao().insertAll(
                                teachersResponse.data?.map { dto ->
                                    Teacher(id = dto.id, name = dto.name)
                                } ?: emptyList()
                            )
                        }
                    }

                    // Загружаем предметы
                    val subjectsResponse = ApiClient.api.getSubjects()
                    if (subjectsResponse.success) {
                        withContext(Dispatchers.IO) {
                            database.subjectDao().insertAll(
                                subjectsResponse.data?.map { dto ->
                                    Subject(id = dto.id, name = dto.name)
                                } ?: emptyList()
                            )
                        }
                    }

                    _apiState.value = ApiState.Success
                }
            } catch (e: Exception) {
                _apiState.value = ApiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun switchToOfflineMode() {
        _apiState.value = ApiState.Error("Switched to offline mode")
    }
}