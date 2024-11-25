package com.example.hacaton.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.IO) {
            if (isTeacher == 0) {
                val group = database.groupDao().getGroupByName(selectedItem)
                val scheduleWithDetails = database.scheduleDao().getScheduleForGroup(group.id)
                scheduleItems = scheduleWithDetails.map { it.toScheduleItem() }
            } else {
                val teacher = database.teacherDao().getTeacherByName(selectedItem)
                val scheduleWithDetails = database.scheduleDao().getScheduleForTeacher(teacher.id)
                scheduleItems = scheduleWithDetails.map { it.toScheduleItem() }
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
        SquareButtonsBlock()
        Spacer(modifier = Modifier.height(16.dp))
        if (isTeacher == 0) {
            StudentScheduleList(scheduleItems, expandedItem, { item -> expandedItem = item })
        } else {
            TeacherScheduleList(scheduleItems, expandedItem, { item -> expandedItem = item })
        }
        if (expandedItem != null) {
            ExpandedDetails(expandedItem!!)
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
fun SquareButtonsBlock() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEAEAEA), shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(modifier = Modifier.size(100.dp), onClick = { /* Действие */ }) {
                Text("Кнопка 1")
            }
            Button(modifier = Modifier.size(100.dp), onClick = { /* Действие */ }) {
                Text("Кнопка 2")
            }
            Button(modifier = Modifier.size(100.dp), onClick = { /* Действие */ }) {
                Text("Кнопка 3")
            }
        }
    }
}

