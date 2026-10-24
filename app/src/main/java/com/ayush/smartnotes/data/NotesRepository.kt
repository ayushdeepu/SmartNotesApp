package com.ayush.smartnotes.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository layer: the single source of truth for note data.
 * The ViewModel talks only to this class, never to the database directly,
 * which keeps the MVVM separation clean.
 */
class NotesRepository(private val dao: NoteDao) {

    /** All notes as an observable stream. */
    fun allNotes(): Flow<List<Note>> = dao.getAllNotes()

    /** Search results for the given query text. */
    fun search(query: String): Flow<List<Note>> = dao.searchNotes(query)

    /** Loads one note (used by the editor screen). */
    suspend fun getNoteById(id: Int): Note? = dao.getNoteById(id)

    /** Inserts or updates a note and returns its row id. */
    suspend fun upsert(note: Note): Long = dao.upsert(note)

    /** Deletes a note. */
    suspend fun delete(note: Note) = dao.delete(note)
}
