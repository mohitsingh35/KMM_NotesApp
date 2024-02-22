package com.ncs.notesapp.domain.note

interface NoteDataSource {
    suspend fun insertNote(note: Note)
    suspend fun getNotebyId(id:Long):Note?
    suspend fun getAllNotes():List<Note>
    suspend fun deleteNoteById(id: Long)
}