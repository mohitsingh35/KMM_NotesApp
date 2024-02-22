package com.ncs.notesapp.data.note

import com.ncs.notesapp.NoteDatabase
import com.ncs.notesapp.domain.note.Note
import com.ncs.notesapp.domain.note.NoteDataSource
import com.ncs.notesapp.domain.time.DateTimeUtil

class SqlDelightNoteDataSource(db:NoteDatabase) : NoteDataSource {

    private val queries=db.noteQueries

    override suspend fun insertNote(note: Note) {
        queries.insertNote(note.id,note.title,note.content,note.colorHex,DateTimeUtil.toEpochMillis(note.created)
        )
    }

    override suspend fun getNotebyId(id: Long): Note? {
        return queries
            .getNoteById(id)
            .executeAsOneOrNull()
            ?.toNote()
    }

    override suspend fun getAllNotes(): List<Note> {
        return queries.getAllNotes().executeAsList().map { it.toNote() }
    }

    override suspend fun deleteNoteById(id: Long) {
        queries.deleteNoteById(id)
    }
}