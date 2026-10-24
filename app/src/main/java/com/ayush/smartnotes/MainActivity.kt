package com.ayush.smartnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.ayush.smartnotes.ui.NotesViewModel
import com.ayush.smartnotes.ui.screens.NoteEditorScreen
import com.ayush.smartnotes.ui.screens.NotesListScreen
import com.ayush.smartnotes.ui.theme.SmartNotesTheme

/**
 * Single-activity host of the app. Two screens (list and editor) are
 * switched with a simple nullable state, which keeps the code easy to
 * read for a college project.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: NotesViewModel by viewModels { NotesViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartNotesTheme {
                // null -> list screen, -1 -> new note, any other id -> existing note
                var editingNoteId by rememberSaveable { mutableStateOf<Int?>(null) }

                val currentId = editingNoteId
                if (currentId == null) {
                    NotesListScreen(
                        viewModel = viewModel,
                        onNoteClick = { editingNoteId = it },
                        onAddClick = { editingNoteId = -1 }
                    )
                } else {
                    NoteEditorScreen(
                        viewModel = viewModel,
                        noteId = currentId,
                        onBack = { editingNoteId = null }
                    )
                }
            }
        }
    }
}
