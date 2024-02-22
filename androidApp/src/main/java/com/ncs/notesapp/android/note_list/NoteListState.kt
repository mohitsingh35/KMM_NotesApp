package com.ncs.notesapp.android.note_list

import com.ncs.notesapp.domain.note.Note

data class NoteListState(
    val notes: List<Note> = emptyList(),
    val searchText: String = "",
    val isSearchActive: Boolean = false
)