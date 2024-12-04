package com.example.hacaton

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hacaton.API.AcademicYearManager
import com.example.hacaton.db.AppDatabase
import com.example.hacaton.db.Group
import com.example.hacaton.db.Schedule
import com.example.hacaton.db.Subject
import com.example.hacaton.db.Teacher
import com.example.hacaton.fragments.AdditionalFragment
import com.example.hacaton.fragments.ScheduleFragment
import com.example.hacaton.mappers.ScheduleWithDetails
import com.example.hacaton.ui.theme.HacatonTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class MainActivityRaspis : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var startDateM: LocalDate
    private lateinit var endDateM: LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getInstance(this)
        val academicYearManager = AcademicYearManager(this)

        lifecycleScope.launch {
            val (startDate, endDate) = academicYearManager.getAcademicYear()
            startDateM = startDate
            endDateM = endDate
        }
        lifecycleScope.launch(Dispatchers.IO) {
            populateScheduleIfEmpty()
        }
        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isTeacher = intent.getIntExtra("isTeacher", 0)
        val selectedItem = intent.getStringExtra("selectedItem") ?: ""
        val theme = sharedPreferences.getString("theme", "Системная")

        setContent {
            if (theme == "Системная") {
                HacatonTheme() {
                    RaspisScreen(isTeacher, selectedItem, database, startDateM, endDateM)
                }
            }else if (theme == "Темная"){
                HacatonTheme(darkTheme = true) {
                    RaspisScreen(isTeacher, selectedItem, database, startDateM, endDateM)
                }
            }else{
                HacatonTheme(darkTheme = false) {
                    RaspisScreen(isTeacher, selectedItem, database, startDateM, endDateM)
                }
            }

        }
    }

    private suspend fun populateScheduleIfEmpty() {
        val scheduleCount = database.scheduleDao().getScheduleCount()

        if (scheduleCount == 0) {
            // Добавляем тестовые данные для групп
            val group1 = Group(name = "ИП-212")
            val group2 = Group(name = "РВ-231")
            database.groupDao().insertGroup(group1)
            database.groupDao().insertGroup(group2)

            // Добавляем тестовые данные для преподавателей
            val teacher1 = Teacher(name = "Иванов И.И.")
            val teacher2 = Teacher(name = "Петров П.П.")
            database.teacherDao().insertTeacher(teacher1)
            database.teacherDao().insertTeacher(teacher2)

            // Добавляем тестовые данные для предметов
            val subject1 = Subject(name = "Математика")
            val subject2 = Subject(name = "Физика")
            database.subjectDao().insertSubject(subject1)
            database.subjectDao().insertSubject(subject2)

            // Добавляем тестовые данные расписания
            val scheduleItems = listOf(
                Schedule(
                    groupId = 1,
                    teacherId = 1,
                    subjectId = 1,
                    day = 1,
                    week = 1,
                    room = "101",
                    startTime = "09:00",
                    endTime = "10:30"
                ),
                Schedule(
                    groupId = 1,
                    teacherId = 1,
                    subjectId = 1,
                    day = 2,
                    week = 13,
                    room = "101",
                    startTime = "09:00",
                    endTime = "10:30"
                ),
                Schedule(
                    groupId = 1,
                    teacherId = 1,
                    subjectId = 1,
                    day = 3,
                    week = 13,
                    room = "101",
                    startTime = "09:00",
                    endTime = "10:30"
                ),
                Schedule(
                    groupId = 2,
                    teacherId = 2,
                    subjectId = 2,
                    day = 1,
                    week = 1,
                    room = "202",
                    startTime = "11:00",
                    endTime = "12:30"
                )
            )
            scheduleItems.forEach { database.scheduleDao().insertSchedule(it) }
        }
    }
}

@Composable
fun RaspisScreen(isTeacher: Int, selectedItem: String, database: AppDatabase, startDate: LocalDate, endDate: LocalDate) {
    var scheduleItems by remember { mutableStateOf<List<ScheduleWithDetails>>(emptyList()) }
    val navController = rememberNavController()
    var selectedNavItem by remember { mutableStateOf(0) }
    val items = listOf("Расписание", "Дополнительно")

    // Загрузка данных из базы данных
    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.IO) {
            scheduleItems = if (isTeacher == 0) {
                val group = database.groupDao().getGroupByName(selectedItem)
                database.scheduleDao().getScheduleForGroup(group.id)
            } else {
                val teacher = database.teacherDao().getTeacherByName(selectedItem)
                database.scheduleDao().getScheduleForTeacher(teacher.id)
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(
                                    id = when(item) {
                                        "Расписание" -> R.drawable.ic_calendar
                                        "Дополнительно" -> R.drawable.ic_info
                                        else -> R.drawable.ic_calendar
                                    }
                                ),
                                contentDescription = item
                            )
                        },
                        label = { Text(item) },
                        selected = selectedNavItem == index,
                        onClick = {
                            selectedNavItem = index
                            navController.navigate(item)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = "Расписание", Modifier.padding(innerPadding)) {
            composable("Расписание") {
                ScheduleFragment(isTeacher, selectedItem, navController, database, startDate, endDate)
            }
            composable("Дополнительно") {
                AdditionalFragment(navController)
            }
        }
    }
}

