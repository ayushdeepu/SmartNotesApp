package com.ayush.smartnotes.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayush.smartnotes.data.Note
import com.ayush.smartnotes.ui.NotesViewModel
import com.ayush.smartnotes.ui.theme.noteColors
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

/**
 * Autosaving editor. A mutex serializes writes and saves finish before navigation,
 * preventing duplicate new notes and cancelled writes on Back.
 */
@Composable
fun NoteEditorScreen(
    viewModel: NotesViewModel,
    noteId: Int,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val saveMutex = remember { Mutex() }
    var currentId by rememberSaveable(noteId) { mutableIntStateOf(noteId) }
    var title by rememberSaveable(noteId) { mutableStateOf("") }
    var content by rememberSaveable(noteId) { mutableStateOf("") }
    var colorIndex by rememberSaveable(noteId) { mutableIntStateOf(0) }
    var pinned by rememberSaveable(noteId) { mutableStateOf(false) }
    var createdAt by rememberSaveable(noteId) { mutableLongStateOf(System.currentTimeMillis()) }
    var loaded by rememberSaveable(noteId) { mutableStateOf(false) }
    var edited by rememberSaveable(noteId) { mutableStateOf(false) }
    var showColors by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf(false) }
    var exiting by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        if (!loaded) {
            if (noteId != -1) {
                viewModel.loadNote(noteId)?.let { n ->
                    currentId = n.id
                    title = n.title
                    content = n.content
                    colorIndex = n.colorIndex
                    pinned = n.isPinned
                    createdAt = n.createdAt
                }
            }
            loaded = true
        }
    }

    suspend fun persistChanges() {
        // Finish an already-started database write even if a debounce job is cancelled.
        withContext(NonCancellable) {
            saveMutex.lock()
            try {
                if (!loaded || !edited || deleting) return@withContext
                val draft = Note(
                    id = if (currentId == -1) 0 else currentId,
                    title = title,
                    content = content,
                    colorIndex = colorIndex,
                    isPinned = pinned,
                    createdAt = createdAt,
                    updatedAt = System.currentTimeMillis()
                )
                val savedId = viewModel.upsert(draft)
                if (currentId == -1 && savedId > 0) currentId = savedId.toInt()
                if (title == draft.title && content == draft.content &&
                    colorIndex == draft.colorIndex && pinned == draft.isPinned
                ) edited = false
            } finally {
                saveMutex.unlock()
            }
        }
    }

    fun saveAndBack() {
        if (exiting || deleting) return
        exiting = true
        scope.launch {
            persistChanges()
            onBack()
        }
    }

    // The Android system Back gesture follows the same safe-save path as the arrow.
    BackHandler(enabled = !showDeleteConfirm) { saveAndBack() }

    LaunchedEffect(loaded, edited, title, content, colorIndex, pinned) {
        if (loaded && edited && !deleting) {
            delay(700)
            persistChanges()
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete note?") },
            text = { Text("This note will be permanently deleted.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    deleting = true
                    scope.launch {
                        withContext(NonCancellable) {
                            saveMutex.lock()
                            try {
                                if (currentId != -1) viewModel.delete(Note(id = currentId))
                            } finally {
                                saveMutex.unlock()
                            }
                        }
                        onBack()
                    }
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(containerColor = noteColors[colorIndex % noteColors.size]) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { saveAndBack() }, enabled = !exiting) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    if (noteId == -1) "New note" else "Edit note",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { pinned = !pinned; edited = true }, enabled = loaded && !exiting) {
                    Icon(
                        if (pinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = if (pinned) "Unpin" else "Pin"
                    )
                }
                IconButton(onClick = { showColors = !showColors }, enabled = loaded && !exiting) {
                    Icon(Icons.Filled.Palette, contentDescription = "Change colour")
                }
                if (currentId != -1) {
                    IconButton(onClick = { showDeleteConfirm = true }, enabled = loaded && !exiting) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete note")
                    }
                }
            }

            AnimatedVisibility(visible = showColors) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    noteColors.forEachIndexed { index, color ->
                        val selected = index == colorIndex
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(color, CircleShape)
                                .then(
                                    if (selected) Modifier.border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    ) else Modifier
                                )
                                .clickable(enabled = !exiting) {
                                    colorIndex = index
                                    edited = true
                                }
                        )
                    }
                }
            }

            TextField(
                value = title,
                onValueChange = { title = it; edited = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = loaded && !exiting,
                placeholder = { Text("Title", fontSize = 24.sp, color = Color(0xFF8A8A8A)) },
                textStyle = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            TextField(
                value = content,
                onValueChange = { content = it; edited = true },
                modifier = Modifier.fillMaxWidth().weight(1f),
                enabled = loaded && !exiting,
                placeholder = { Text("Start writing your note…", color = Color(0xFF8A8A8A)) },
                textStyle = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}
