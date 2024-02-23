package com.ncs.notesapp.firebase

import com.ncs.notesapp.data.local.FirebaseManager


class FirebaseViewmodel(private val authManager: FirebaseManager){
    suspend fun signIn(email: String, password: String): Boolean {
        return authManager.signInWithEmail(email, password)
    }

    suspend fun signUp(email: String, password: String): Boolean {
        return authManager.signUpWithEmail(email, password)
    }

    suspend fun signOut() {
        authManager.signOut()
    }

    fun getCurrentUserEmail(): String? {
        return authManager.getCurrentUserEmail()
    }
}
