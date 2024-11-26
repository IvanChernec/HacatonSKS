package com.example.hacaton.fragments

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hacaton.db.AppDatabase
import com.example.hacaton.db.Schedule
import com.example.hacaton.model.ScheduleItem
import com.example.hacaton.model.getStudentScheduleItems
import com.example.hacaton.mappers.toScheduleItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar


@Composable
fun ScheduleFragment(isTeacher: Int, selectedItem: String, navController: NavController, database: AppDatabase) {
    var scheduleItems by remember { mutableStateOf<List<ScheduleItem>>(emptyList()) }
    var expandedItem by remember { mutableStateOf<ScheduleItem?>(null) }
    var showWeekPicker by remember { mutableStateOf(false) }
    val (currentWeek, currentDay) = remember { getCurrentWeekAndDay() }
    var selectedDay by remember { mutableStateOf(currentDay) }
    var selectedWeek by remember { mutableStateOf(currentWeek) }
    val totalWeeks = remember {
        calculateWeeks(
            LocalDate.of(2024, 9, 1),
            LocalDate.of(2024, 12, 30)
        )
    }


    if (showWeekPicker) {
        WeekPickerDialog(
            weeks = totalWeeks,
            currentWeek = selectedWeek,
            onWeekSelected = { week -> selectedWeek = week },
            onDismiss = { showWeekPicker = false }
        )
    }

    Column {
        Header(
            selectedItem = selectedItem,
            onWeekClick = { showWeekPicker = true }
        )
        SquareButtonsBlock(
            selectedDay = selectedDay,
            onDaySelected = { day -> selectedDay = day }
        )
        WeekDayInfo(
            dayOfWeek = getDayOfWeekName(selectedDay),
            weekNumber = selectedWeek
        )




        LaunchedEffect(key1 = selectedDay, key2 = selectedWeek) {
            withContext(Dispatchers.IO) {
                if (isTeacher == 0) {
                    val group = database.groupDao().getGroupByName(selectedItem)
                    scheduleItems = database.scheduleDao().getScheduleForGroup(group.id)
                        .filter { it.day == selectedDay && it.week == selectedWeek }
                        .map { it.toScheduleItem() }
                } else {
                    val teacher = database.teacherDao().getTeacherByName(selectedItem)
                    scheduleItems = database.scheduleDao().getScheduleForTeacher(teacher.id)
                        .filter { it.day == selectedDay && it.week == selectedWeek }
                        .map { it.toScheduleItem() }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            if (isTeacher == 0) {
                StudentScheduleList(scheduleItems, expandedItem, { item -> expandedItem = item })
            } else {
                TeacherScheduleList(scheduleItems, expandedItem, { item -> expandedItem = item })
            }
        }
    }
}
    fun getDayOfWeekName(day: Int): String {
        return when (day) {
            1 -> "Понедельник"
            2 -> "Вторник"
            3 -> "Среда"
            4 -> "Четверг"
            5 -> "Пятница"
            6 -> "Суббота"
            7 -> "Воскресенье"
            else -> ""
        }
    }

@Composable
fun Header(
    selectedItem: String,
    onWeekClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = selectedItem,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = onWeekClick) {
            Text(text = "Неделя")
        }
    }
}


@Composable
    fun SquareButtonsBlock(
        selectedDay: Int,
        onDaySelected: (Int) -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)  // добавили отступ сверху
                .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val days = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
                days.forEachIndexed { index, day ->
                    Button(
                        onClick = { onDaySelected(index + 1) },
                        modifier = Modifier.size(70.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedDay == index + 1)
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(
                            day,
                            color = if (selectedDay == index + 1)
                                MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = if (selectedDay == index + 1)
                                FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun WeekPickerDialog(
        weeks: Int,
        currentWeek: Int,
        onWeekSelected: (Int) -> Unit,
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Выберите неделю") },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(weeks) { index ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (index + 1 == currentWeek)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    onWeekSelected(index + 1)
                                    onDismiss()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = if (index + 1 == currentWeek)
                                    MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = { }
        )
    }

    @Composable
    fun WeekDayInfo(dayOfWeek: String, weekNumber: Int) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dayOfWeek,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Неделя $weekNumber",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }

fun calculateWeeks(startDate: LocalDate, endDate: LocalDate): Int {
    return ChronoUnit.WEEKS.between(startDate, endDate).toInt() + 1
}

fun getCurrentWeekAndDay(): Pair<Int, Int> {
    val calendar = Calendar.getInstance()

    // Получаем текущий день недели (1-7, где 1 это понедельник)
    val currentDay = when(calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY -> 7
        else -> calendar.get(Calendar.DAY_OF_WEEK) - 1
    }

    // Получаем номер недели от начала учебного года
    val startDate = Calendar.getInstance().apply {
        set(Calendar.YEAR, if (calendar.get(Calendar.MONTH) < Calendar.SEPTEMBER)
            calendar.get(Calendar.YEAR) - 1 else calendar.get(Calendar.YEAR))
        set(Calendar.MONTH, Calendar.SEPTEMBER)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val weeksBetween = ((calendar.timeInMillis - startDate.timeInMillis) /
            (7 * 24 * 60 * 60 * 1000)).toInt() + 1

    return Pair(weeksBetween, currentDay)
}

