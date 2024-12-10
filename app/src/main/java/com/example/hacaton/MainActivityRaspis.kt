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
        //val academicYearManager = AcademicYearManager(this)
        val currentYear = LocalDate.now().year
        startDateM = LocalDate.of(currentYear, 9, 1)
        endDateM = LocalDate.of(currentYear, 12, 31)
        /*lifecycleScope.launch {
            val (startDate, endDate) = academicYearManager.getAcademicYear()
            startDateM = startDate
            endDateM = endDate
        }*/
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

