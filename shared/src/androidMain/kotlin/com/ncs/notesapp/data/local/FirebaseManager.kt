package com.ncs.notesapp.data.local

import com.google.firebase.auth.FirebaseAuth
import com.ncs.notesapp.domain.note.Note
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


actual class FirebaseManager {
    private val auth = Firebase.auth
    private val db=Firebase.firestore

    actual suspend fun signInWithEmail(
        email: String,
        password: String
    ): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password)
            true
        } catch (e: Exception) {
            false
        }
    }

    actual suspend fun signUpWithEmail(
        email: String,
        password: String
    ): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, password)
            true
        } catch (e: Exception) {
            false
        }
    }

    actual suspend fun signOut() {
        auth.signOut()
    }

    actual fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email

    }

    actual suspend fun upsert(note: Note): Boolean {
        return try {
            val email = auth.currentUser?.email
            db.collection("Users").document("1234").collection("Notes").document(note.id.toString()).set(note, merge = true)
            true
        } catch (e: Exception) {
            false
        }
    }


    actual suspend fun deleteNote(note: Note): Boolean {
        return try {
            val email=auth.currentUser?.email
            db.document("Users").collection("Notes").document(email!!).delete()
            true
        } catch (e: Exception) {
            false
        }
    }


    actual suspend fun getAllNotes(): List<Note> {
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
}