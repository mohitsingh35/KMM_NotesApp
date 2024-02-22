package com.ncs.notesapp.android.note_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ncs.notesapp.domain.note.Note
import com.ncs.notesapp.domain.time.DateTimeUtil


@Composable
fun NoteItem(
    note: Note,
    backgroundColor: Color,
    onNoteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedDate = remember(note.created) {
        DateTimeUtil.formatNoteDate(note.created)
    }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .background(backgroundColor)
            .clickable { onNoteClick() }
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = note.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = Color.Black
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete note",
                tint = Color.Black,
                modifier = Modifier
                    .clickable(MutableInteractionSource(), null) {
                        onDeleteClick()
                    }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = note.content, fontWeight = FontWeight.Light, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = formattedDate,
            color = Color.DarkGray,
            modifier = Modifier.align(Alignment.End)
        )
    }
}