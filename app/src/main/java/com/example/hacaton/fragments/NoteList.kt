package com.example.hacaton.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.hacaton.db.Note
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.DismissValue
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberDismissState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotesList(
    notes: List<Note>,
    onNoteAdded: (text: String, hasReminder: Boolean) -> Unit,
    onTransferNote: () -> Unit,
    onNoteDeleted: (Note) -> Unit
) {
    var noteText by remember { mutableStateOf("") }
    var needReminder by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Заметки:",
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 120.dp)
        ) {
            items(notes) { note ->
                val dismissState = rememberDismissState()

                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart),
                    dismissThresholds = { FractionalThreshold(0.5f) },
                    background = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Удалить",
                                tint = Color.White
                            )
                        }
                    }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    ) {
                        Text(
                            text = note.text,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                LaunchedEffect(dismissState.currentValue) {
                    if (dismissState.currentValue != DismissValue.Default) {
                        onNoteDeleted(note)
                    }
                }
            }
        }


        TextField(
            value = noteText,
            onValueChange = { noteText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Введите текст", color = Color.Gray) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Напомнить",
                color = Color.Gray,
                modifier = Modifier.padding(end = 8.dp)
            )
            Switch(
                checked = needReminder,
                onCheckedChange = { needReminder = it }
            )
        }

        Button(
            onClick = onTransferNote,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A1B2E)
            )
        ) {
            Text("Перенести")
        }

        Button(
            onClick = {
                if (noteText.isNotEmpty()) {
                    onNoteAdded(noteText, needReminder)
                    noteText = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A1B2E)
            )
        ) {
            Text("Сохранить")
        }
    }
}