package com.ncs.notesapp.di

import com.ncs.notesapp.NoteDatabase
import com.ncs.notesapp.data.local.DatabaseDriverFactory
import com.ncs.notesapp.data.note.SqlDelightNoteDataSource
import com.ncs.notesapp.domain.note.NoteDataSource

class DatabaseModule {

    private val factory by lazy { DatabaseDriverFactory() }
    val noteDataSource: NoteDataSource by lazy {
        SqlDelightNoteDataSource(NoteDatabase(factory.createDriver()))
    }
}