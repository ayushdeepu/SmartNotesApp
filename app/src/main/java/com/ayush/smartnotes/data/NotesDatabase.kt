package com.ayush.smartnotes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database for the app. Holds exactly one table: [Note].
 *
 * The singleton pattern prevents several database instances from being
 * opened at the same time, which would waste memory and cause errors.
 */
@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NotesDatabase? = null

        fun get(context: Context): NotesDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NotesDatabase::class.java,
                    "smart_notes.db"
                ).build().also { INSTANCE = it }
            }
    }
}
