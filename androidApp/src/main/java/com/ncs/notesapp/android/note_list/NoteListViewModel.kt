package com.ncs.notesapp.android.note_list

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ncs.notesapp.domain.note.Note
import com.ncs.notesapp.domain.note.NoteDataSource
import com.ncs.notesapp.domain.note.SearchNotes

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val noteDataSource: NoteDataSource,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val searchNotes = SearchNotes()
    private val db=Firebase.firestore
    private val notes = savedStateHandle.getStateFlow("notes", emptyList<Note>())
    private val searchText = savedStateHandle.getStateFlow("searchText", "")
    private val isSearchActive = savedStateHandle.getStateFlow("isSearchActive", false)

    val state = combine(notes, searchText, isSearchActive) { notes, searchText, isSearchActive ->
        NoteListState(
            notes = searchNotes.execute(notes, searchText),
            searchText = searchText,
            isSearchActive = isSearchActive
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteListState())

    fun loadNotes() {

        viewModelScope.launch {
            val res=getAllNotes()
            Log.d("result",res.toString())
            for (note in res){
                noteDataSource.insertNote(note)
            }
            savedStateHandle["notes"] = noteDataSource.getAllNotes()
        }
    }


    suspend fun getAllNotes(): List<Note> {
        val currentUserEmail = Firebase.auth.currentUser?.email
        val notesCollection = Firebase.firestore.collection("Users").document(currentUserEmail!!)
            .collection("Notes")

        return try {
            val querySnapshot = notesCollection.get().documents
            val notesList = mutableListOf<Note>()
            for (document in querySnapshot) {
                val id=document.get("id") as Int
                val title=document.get("title") as String
                val content=document.get("content") as String
                val color=document.get("colorHex") as Int
                val created=document.get("created") as kotlinx.datetime.LocalDateTime

                val note = Note(
                    id=id.toLong(),
                    title=title,
                    content = content,
                    colorHex = color.toLong(),
                    created = created
                )
                note.let {
                    notesList.add(it)
                }
            }
            notesList
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun onSearchTextChange(text: String) {
        savedStateHandle["searchText"] = text
    }

    fun onToggleSearch() {
        savedStateHandle["isSearchActive"] = !isSearchActive.value
        if(!isSearchActive.value) {
            savedStateHandle["searchText"] = ""
        }
    }

    fun deleteNoteById(id: Long) {
        viewModelScope.launch {
            noteDataSource.deleteNoteById(id)
            loadNotes()
            db.collection("Users").document(Firebase.auth.currentUser?.email!!).collection("Notes").document(id.toString()).delete()

        }
    }
}