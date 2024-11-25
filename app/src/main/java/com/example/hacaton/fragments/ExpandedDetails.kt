package com.example.hacaton.fragments

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.hacaton.model.ScheduleItem

@Composable
fun ExpandedDetails(item: ScheduleItem) {
    val alpha by animateFloatAsState(targetValue = if (item == null) 0f else 1f, animationSpec = tween(300))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFEAEAEA), shape = RoundedCornerShape(8.dp))
            .alpha(alpha),
        contentAlignment = Alignment.TopStart
    ) {
        Column {
            Text("Детали айтема:")
            TextField(
                value = "",
                onValueChange = { /* Обработка изменения текста */ },
                label = { Text("Введите текст") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { /* Действие при нажатии на кнопку */ }, enabled = true) {
                Text("Сохранить")
            }
        }
    }
}