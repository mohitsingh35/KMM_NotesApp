package com.ncs.notesapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ncs.notesapp.android.note_detail.NoteDetailScreen
import com.ncs.notesapp.android.note_list.NoteListScreen
import com.ncs.notesapp.data.local.FirebaseManager
import com.ncs.notesapp.domain.note.Note
import com.ncs.notesapp.domain.time.DateTimeUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesActivity : ComponentActivity() {
    private val firebaseManager = FirebaseManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "note_list") {
                    composable(route = "note_list") {
                        NoteListScreen(navController = navController, firebaseManager = firebaseManager)
                    }
                    composable(
                        route = "note_detail/{noteId}",
                        arguments = listOf(
                            navArgument(name = "noteId") {
                                type = NavType.LongType
                                defaultValue = -1L
                            }
                        )
                    ) { backStackEntry ->
                        val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
                        NoteDetailScreen(noteId = noteId, navController = navController)
                    }
                }

            }
        }

    }
}

