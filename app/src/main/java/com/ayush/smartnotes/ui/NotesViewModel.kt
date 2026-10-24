package com.ayush.smartnotes.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ayush.smartnotes.data.Note
import com.ayush.smartnotes.data.NotesDatabase
import com.ayush.smartnotes.data.NotesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * MVVM ViewModel for both screens. Survives configuration changes
 * (e.g. screen rotation) automatically.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModel(private val repository: NotesRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /**
     * The list shown on the home screen. Reacts automatically to the
     * search query: empty query -> all notes, otherwise -> filtered notes.
     */
    val notes: StateFlow<List<Note>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) repository.allNotes() else repository.search(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Number of stored notes (used in the top bar subtitle). */
    val noteCount: StateFlow<Int> = notes
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    /** Loads a single note for the editor (null for new notes). */
    suspend fun loadNote(id: Int): Note? = repository.getNoteById(id)

    /** Inserts or updates a note; returns the saved row id. */
    suspend fun upsert(note: Note): Long = repository.upsert(note)

    /** Deletes a note permanently. */
    suspend fun delete(note: Note) = repository.delete(note)

    companion object {
        /** Factory so the ViewModel can be created with its repository. */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as Application
                NotesViewModel(NotesRepository(NotesDatabase.get(app).noteDao()))
            }
        }
    }
}
