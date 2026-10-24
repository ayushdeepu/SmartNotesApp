package com.ayush.smartnotes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data-access object for the [Note] entity. Room generates the actual
 * SQLite implementation at compile time.
 */
@Dao
interface NoteDao {

    /** All notes, pinned first, then most recently edited first. */
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<Note>>

    /** Case-insensitive search over both the title and the content. */
    @Query(
        "SELECT * FROM notes WHERE title LIKE '%' || :query || '%' " +
            "OR content LIKE '%' || :query || '%' " +
            "ORDER BY isPinned DESC, updatedAt DESC"
    )
    fun searchNotes(query: String): Flow<List<Note>>

    /** Fetches a single note by id (null when the id does not exist). */
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?

    /** Inserts a new note (id = 0) or updates an existing note by primary key. */
    @Upsert
    suspend fun upsert(note: Note): Long

    /** Permanently removes a note. */
    @Delete
    suspend fun delete(note: Note)
}
