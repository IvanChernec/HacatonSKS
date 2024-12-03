package com.example.hacaton

import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.hacaton.API.ApiClient
import com.example.hacaton.API.ApiState
import com.example.hacaton.db.AppDatabase
import com.example.hacaton.db.Group
import com.example.hacaton.db.Schedule
import com.example.hacaton.db.Teacher
import com.example.hacaton.model.LoadingScreen
import com.example.hacaton.model.MainViewModel
import com.example.hacaton.ui.theme.HacatonTheme
import com.example.hacaton.widget.ScheduleWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: AppDatabase
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        database = AppDatabase.getInstance(this)
        val theme = sharedPreferences.getString("theme", "Системная")

        setupScheduleCheck(this)
        lifecycleScope.launch(Dispatchers.IO) {
            populateDatabaseIfEmpty()
        }

        setContent {
            when (theme) {
                "Системная" -> HacatonTheme { MainContent() }
                "Темная" -> HacatonTheme(darkTheme = true) { MainContent() }
                else -> HacatonTheme(darkTheme = false) { MainContent() }
            }
        }
    }

    @Composable
    private fun MainContent() {
        val apiState by viewModel.apiState.collectAsState()

        when (apiState) {
            is ApiState.Loading -> LoadingScreen()
            else -> MainScreen(
                database = database,
                onRoleSelected = { isTeacher, selectedItem ->
                    viewModel.loadScheduleData(isTeacher, selectedItem)
                }
            )
        }
    }


    private suspend fun populateDatabaseIfEmpty() {
        val groupCount = database.groupDao().getGroupCount()
        val teacherCount = database.teacherDao().getTeacherCount()

        if (groupCount == 0 && teacherCount == 0) {
            // Добавляем группы и преподавателей в базу данных, если они отсутствуют.
            val groups = listOf(
                Group(name = "ИП-212"),
                Group(name = "РВ-231"),
                Group(name = "Р-223")
            )
            groups.forEach { database.groupDao().insertGroup(it) }

            val teachers = listOf(
                Teacher(name = "Иванов И.И."),
                Teacher(name = "Петров П.П."),
                Teacher(name = "Сидоров С.С.")
            )
            teachers.forEach { database.teacherDao().insertTeacher(it) }
        }
    }
}

@Composable
fun MainScreen(database: AppDatabase, onRoleSelected: (Int, String) -> Unit) {
    val context = LocalContext.current
    var showStudentDialog by remember { mutableStateOf(false) }
    var showTeacherDialog by remember { mutableStateOf(false) }

    var groups by remember { mutableStateOf<List<Group>>(emptyList()) }
    var teachers by remember { mutableStateOf<List<Teacher>>(emptyList()) }

    // Загрузка данных из базы данных в фоновом потоке.
    LaunchedEffect(key1 = Unit) {
        launch(Dispatchers.IO) {
            groups = database.groupDao().getAllGroups()
            teachers = database.teacherDao().getAllTeachers()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Выберите роль",
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Пожалуйста, выберите одну из опций ниже",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(50.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RoleButton("Студент", R.drawable.ic_student, onClick = { showStudentDialog = true })
            RoleButton("Преподаватель", R.drawable.ic_teacher, onClick = { showTeacherDialog = true })
        }
    }

    if (showStudentDialog) {
        SearchDialog(
            title = "Выберите группу",
            items = groups.map { it.name }, // Данные из базы данных.
            onDismiss = { showStudentDialog = false },
            onItemSelected = { selectedGroup ->
                navigateToRaspis(context, 0, selectedGroup)
                showStudentDialog = false
            }
        )
    }

    if (showTeacherDialog) {
        SearchDialog(
            title = "Выберите преподавателя",
            items = teachers.map { it.name }, // Данные из базы данных.
            onDismiss = { showTeacherDialog = false },
            onItemSelected = { selectedTeacher ->
                navigateToRaspis(context, 1, selectedTeacher)
                showTeacherDialog = false
            }
        )
    }
}

@Composable
fun RoleButton(role: String, iconRes: Int, onClick: () -> Unit) {
    Card(
        modifier= Modifier.size(150.dp).clickable(onClick=onClick),
        shape= RoundedCornerShape(16.dp),
        elevation= CardDefaults.cardElevation(defaultElevation=8.dp),
        colors= CardDefaults.cardColors(containerColor= MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            modifier= Modifier.padding(16.dp),
            horizontalAlignment= Alignment.CenterHorizontally,
            verticalArrangement= Arrangement.Center
        ) {
            Image(
                painter= painterResource(id= iconRes), // Иконка для кнопки.
                contentDescription= "$role Icon",
                modifier= Modifier.size(64.dp)
            )
            Spacer(modifier= Modifier.height(8.dp))
            Text(text= role, fontSize = 15.sp, color= Color.White)
        }
    }
}

@Composable
fun SearchDialog(
    title: String,
    items: List<String>,
    onDismiss: () -> Unit,
    onItemSelected: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") } // Состояние для поискового запроса

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column {
                // Поле для ввода поискового запроса
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Поиск") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Фильтрация списка на основе поискового запроса
                LazyColumn {
                    items(items.filter { it.contains(searchQuery, ignoreCase = true) }) { item ->
                        Text(
                            text = item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onItemSelected(item) }
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена",
                    color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    )
}

private fun navigateToRaspis(context: Context, isTeacher: Int, selectedItem: String) {
    // Сохраняем данные в SharedPreferences
    context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        .edit()
        .putInt("isTeacher", isTeacher)
        .putString("selectedItem", selectedItem)
        .apply()

    // Обновляем виджет
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val componentName = ComponentName(context, ScheduleWidgetProvider::class.java)
    val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

    val widgetIntent = Intent(context, ScheduleWidgetProvider::class.java).apply {
        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
    }
    context.sendBroadcast(widgetIntent)

    // Загружаем данные с API
    (context as ComponentActivity).lifecycleScope.launch {
        try {
            val database = AppDatabase.getInstance(context)
            withTimeout(25000) {
                val response = if (isTeacher == 0) {
                    ApiClient.api.getScheduleForGroup(selectedItem)
                } else {
                    ApiClient.api.getScheduleForTeacher(selectedItem)
                }

                if (response.success) {
                    withContext(Dispatchers.IO) {
                        response.data?.schedules?.let {
                            database.scheduleDao().insertAll(it.map { dto ->
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
            }
        } catch (e: Exception) {
            Log.e("API Error", "Error loading schedule data", e)
        } finally {
            // Переходим к расписанию
            val intent = Intent(context, MainActivityRaspis::class.java).apply {
                putExtra("isTeacher", isTeacher)
                putExtra("selectedItem", selectedItem)
            }
            context.startActivity(intent)
        }
    }
}
private fun setupScheduleCheck(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val scheduleCheckRequest = PeriodicWorkRequestBuilder<ScheduleCheckWorker>(
        60, TimeUnit.MINUTES,  // Проверка каждые 60 минут
        5, TimeUnit.MINUTES    // Гибкий интервал
    )
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "schedule_check",
            ExistingPeriodicWorkPolicy.KEEP,
            scheduleCheckRequest
        )
}