package com.example.hacaton.fragments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.unit.dp
import com.example.hacaton.db.Note
import com.example.hacaton.model.ScheduleItem

@Composable
fun StudentScheduleList(
    scheduleItems: List<ScheduleItem>,
    expandedItem: ScheduleItem?,
    scheduleNotes: List<Note>,
    onItemClicked: (ScheduleItem) -> Unit,
    onNoteAdded: (text: String, hasReminder: Boolean) -> Unit,
    onTransferNote: () -> Unit,
    onNoteDeleted: (Note) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(scheduleItems) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClicked(item) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "${item.startTime} - ${item.endTime}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.subjectName,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = item.teacherName,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "Ауд. ${item.room}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    AnimatedVisibility(
                        visible = item == expandedItem,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        NotesList(
                            notes = scheduleNotes,
                            onNoteAdded = onNoteAdded,
                            onTransferNote = onTransferNote,
                            onNoteDeleted = onNoteDeleted
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun TeacherScheduleList(
    scheduleItems: List<ScheduleItem>,
    expandedItem: ScheduleItem?,
    scheduleNotes: List<Note>,
    onItemClicked: (ScheduleItem) -> Unit,
    onNoteAdded: (text: String, hasReminder: Boolean) -> Unit,
    onTransferNote: () -> Unit,
    onNoteDeleted: (Note) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(scheduleItems) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClicked(item) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "${item.startTime} - ${item.endTime}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.groupName,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = item.subjectName,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "Ауд. ${item.room}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    AnimatedVisibility(
                        visible = item == expandedItem,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        NotesList(
                            notes = scheduleNotes,
                            onNoteAdded = onNoteAdded,
                            onTransferNote = onTransferNote,
                            onNoteDeleted = onNoteDeleted
                        )
                    }
                }
            }
        }
    }
}



