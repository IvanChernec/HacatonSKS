package com.example.hacaton.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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



@Composable
fun ScheduleFragment(isTeacher: Int, selectedItem: String, navController: NavController, database: AppDatabase) {
    var scheduleItems by remember { mutableStateOf<List<ScheduleItem>>(emptyList()) }
    var expandedItem by remember { mutableStateOf<ScheduleItem?>(null) }
    var selectedDay by remember { mutableStateOf(1) }

    LaunchedEffect(key1 = selectedDay) {
        withContext(Dispatchers.IO) {
            if (isTeacher == 0) {
                val group = database.groupDao().getGroupByName(selectedItem)
                scheduleItems = database.scheduleDao().getScheduleForGroup(group.id)
                    .filter { it.day == selectedDay }
                    .map { it.toScheduleItem() }
            } else {
                val teacher = database.teacherDao().getTeacherByName(selectedItem)
                scheduleItems = database.scheduleDao().getScheduleForTeacher(teacher.id)
                    .filter { it.day == selectedDay }
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
        Header(selectedItem)
        SquareButtonsBlock(
            selectedDay = selectedDay,
            onDaySelected = { day -> selectedDay = day }
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (isTeacher == 0) {
            StudentScheduleList(scheduleItems, expandedItem, { item -> expandedItem = item })
        } else {
            TeacherScheduleList(scheduleItems, expandedItem, { item -> expandedItem = item })
        }
    }
}

@Composable
fun Header(selectedItem: String) {
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
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = { /* Действие для кнопки "Неделя" */ }) {
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
                        fontSize = 16.sp,  // увеличили размер текста
                        fontWeight = if (selectedDay == index + 1)
                            FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
