package com.ncs.notesapp.domain.note

import com.ncs.notesapp.domain.time.DateTimeUtil

class SearchNotes {
    fun execute(notes:List<Note>,query:String) : List<Note>{
        if (query.isBlank()){
            return notes
        }
        return notes.filter {
            it.title.trim().lowercase().contains(query.lowercase()) ||
                    it.content.trim().lowercase().contains(query.toLowerCase())
        }.sortedBy {
            DateTimeUtil.toEpochMillis(it.created)
        }
    }
}