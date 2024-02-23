package com.ncs.notesapp.android.note_list


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ncs.notesapp.data.local.FirebaseManager
import com.ncs.notesapp.domain.note.Note
import com.ncs.notesapp.domain.time.DateTimeUtil
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteListScreen(
    navController: NavController,
    viewModel: NoteListViewModel = hiltViewModel(),
    firebaseManager:FirebaseManager
) {
    val state by viewModel.state.collectAsState()
    val db=Firebase.firestore
    var refreshState by remember { mutableStateOf(false) }

    LaunchedEffect(refreshState) {
        if (refreshState) {
            viewModel.loadNotes()
            delay(3000)
            refreshState = false
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.loadNotes()
        delay(5000L)

        val notes=state.notes
        for (note in notes){
            db.collection("Users").document(Firebase.auth.currentUser?.email!!).collection("Notes").document(note.id.toString()).set(note, merge = true)
        }

    }



    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("note_detail/-1L")
                },
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add note",
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                HideableSearchTextField(
                    text = state.searchText,
                    isSearchActive = state.isSearchActive,
                    onTextChange = viewModel::onSearchTextChange,
                    onSearchClick = viewModel::onToggleSearch,
                    onCloseClick = viewModel::onToggleSearch,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                )
                this@Column.AnimatedVisibility(
                    visible = !state.isSearchActive,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "All notes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    )
                }
            }

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = refreshState),
                onRefresh = {
                    refreshState=true
                },
            ) {

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        items = state.notes,
                        key = { it.id!! }
                    ) { note ->
                        NoteItem(
                            note = note,
                            backgroundColor = Color(note.colorHex),
                            onNoteClick = {
                                navController.navigate("note_detail/${note.id}")
                            },
                            onDeleteClick = {

                                viewModel.deleteNoteById(note.id!!)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .animateItemPlacement()
                        )
                    }
                }
            }

        }
    }
}