package com.ncs.notesapp.data.local

import com.ncs.notesapp.domain.note.Note

expect class FirebaseManager {
     suspend fun signInWithEmail(email: String, password: String): Boolean
     suspend fun signUpWithEmail(email: String, password: String): Boolean
     suspend fun signOut()
     fun getCurrentUserEmail(): String?
     suspend fun upsert(note: Note): Boolean
     suspend fun deleteNote(note: Note):Boolean
     suspend fun getAllNotes():List<Note>
}