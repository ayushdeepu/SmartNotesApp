package com.ayush.smartnotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single note stored in the local Room database.
 *
 * @property id         auto-generated primary key (0 means "new note, not saved yet")
 * @property title      short heading of the note
 * @property content    main body text of the note
 * @property colorIndex index into the app's note colour palette (used as a label)
 * @property isPinned   pinned notes always appear at the top of the list
 * @property createdAt  creation timestamp in milliseconds
 * @property updatedAt  last-edit timestamp in milliseconds (used for sorting)
 */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val colorIndex: Int = 0,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
